package com.galaxy.galaxynet.ui.tabs.ip

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.galaxy.galaxynet.R
import com.galaxy.galaxynet.databinding.FragmentIpListBinding
import com.galaxy.galaxynet.model.Ip
import com.galaxy.galaxynet.model.User
import com.galaxy.util.Constants
import com.galaxy.util.UiState
import com.galaxy.util.showConfirmationDialog
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder
import java.util.Stack

@AndroidEntryPoint
class IpListFragment : Fragment() {
    lateinit var binding: FragmentIpListBinding
    private val viewModel: IpViewModel by viewModels()
    val bundle = Bundle()
    private val adapter = IPListAdapter(null , true)
    private val clickedIpStack = Stack<Ip>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentIpListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getMainIPList()
        initViews()
        initObservers()
    }

    private fun initObservers() {
        viewModel.ipList.observe(viewLifecycleOwner) {
            if (it?.isEmpty() == true) {
                binding.emptyListView.visibility = View.VISIBLE
                binding.ipRecycler.visibility = View.GONE
            } else {
                adapter.submitList(it)
                binding.emptyListView.visibility = View.GONE
                binding.ipRecycler.visibility = View.VISIBLE
            }

        }
        viewModel.uiState.observe(viewLifecycleOwner) {
            handleState(it)
        }
    }

    private fun initViews() {
        binding.addIPBtn.setOnClickListener {

            if (clickedIpStack.isNotEmpty()) {
                val parent = clickedIpStack.peek().value!!
                bundle.putString(Constants.PARENT_IP, parent)
            } else {
                bundle.putString(Constants.PARENT_IP, null)
            }
            findNavController().navigate(R.id.action_ipListFragment_to_addIPFragment, bundle)
        }
        binding.ipRecycler.adapter = adapter

        adapter.onAddIPInListClickListener = IPListAdapter.OnIpClickListener { ip, position ->
            bundle.putString(Constants.PARENT_IP, ip.value)

            findNavController().navigate(R.id.action_ipListFragment_to_addIPFragment, bundle)
        }

        adapter.onIpClickListener = IPListAdapter.OnIpClickListener { ip, position ->
            // Update UI and stack
            clickedIpStack.push(ip)
            binding.headerText.text = " القائمه متفرعه من  ${ip.value} \n ${ip.keyword}"
            binding.backBtn.visibility = View.VISIBLE


            viewModel.getSubIpList(ip.value!!)
        }

        binding.backBtn.setOnClickListener {
            clickedIpStack.pop()
            if (clickedIpStack.isNotEmpty()) {
                val previousIp = clickedIpStack.peek()
                binding.headerText.text =
                    " القائمه متفرعه من  ${previousIp.value} \n ${previousIp.keyword}"
                viewModel.getSubIpList(previousIp.value!!)
            } else {
                binding.headerText.text = "القائمه الرئيسيه"
                binding.backBtn.visibility = View.GONE
                viewModel.getMainIPList()
            }
            Log.d("trace stack", clickedIpStack.size.toString())
        }
        adapter.onOpenInBrowserClickListener = IPListAdapter.OnIpClickListener { ip, position ->
            navigateToBrowser(ip.value)
        }
        adapter.onDeleteIpClickListener = IPListAdapter.OnIpClickListener { ip, position ->
            if (viewModel.sessionManager.getUserData()?.type.equals(User.MANAGER)) {
                this.showConfirmationDialog(
                    title = "تأكيد الحذف",
                    message = "هل انت متأكد من انك تريد حذف '${ip.value}'؟",
                    positiveText = "حذف",
                    onPositiveClick = { _, _ ->
                        viewModel.deleteIp(ip, ip.value!!)
                        if (clickedIpStack.empty()) {
                            viewModel.getMainIPList()
                            binding.headerText.text = "القائمه الرئيسيه"
                            binding.backBtn.visibility = View.GONE
                        } else {
                            viewModel.getSubIpList(clickedIpStack.peek().value.toString())
                        }

                    }
                )
            } else {
                Toast.makeText(requireActivity(), "لايوجد لديك صلاحيه الحذف", Toast.LENGTH_SHORT)
                    .show()
            }


        }

        adapter.onEditIpClickListener = IPListAdapter.OnIpClickListener { ip, position ->
            if (viewModel.sessionManager.getUserData()?.type.equals(com.galaxy.galaxynet.model.User.MANAGER)) {
                val intent = Intent(requireActivity(), EditIPActivity::class.java).apply {
                    putExtra(Constants.IP_ID, ip.id)
                    Log.d("test id", ip.id.toString())
                    startActivity(this)
                }
            } else {
                Toast.makeText(requireActivity(), "لايوجد لديك صلاحيه التعديل", Toast.LENGTH_SHORT)
                    .show()
            }

        }


        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                Log.d("test search", query ?: "null")
                if (query?.isNullOrBlank() == true) {
                    viewModel.getMainIPList()
                } else {
                    viewModel.searchLocally(query)
                }

                return true
            }

        })

        binding.swiperefresh.setOnRefreshListener {
            if (clickedIpStack.isNotEmpty()) {
                val sub = clickedIpStack.peek()
                viewModel.getSubIpList(sub.value!!)
                binding.headerText.text = " القائمه متفرعه من  ${sub.value} \n ${sub.keyword}"
                binding.swiperefresh.isRefreshing = false
            } else {
                viewModel.getMainIPList()
                binding.headerText.text = "القائمه الرئيسيه"
                binding.swiperefresh.isRefreshing = false

            }


        }

    }

    private fun navigateToBrowser(text: String?) {
        val encodedText = URLEncoder.encode(text, "UTF-8")
        val searchUrl = "http://$encodedText/"

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl))
        context?.startActivity(intent)
    }


    private fun handleState(state: UiState) {
        when (state) {
            UiState.SUCCESS -> {
                binding.progressBar.visibility = View.GONE

            }

            UiState.LOADING -> {
                binding.progressBar.visibility = View.VISIBLE
            }

            UiState.ERROR -> {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(activity, "حدث خطأ حاول مره اخري", Toast.LENGTH_LONG).show()
            }
        }
    }



}