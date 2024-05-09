package com.galaxy.galaxynet.ui.controlPanel

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.galaxy.galaxynet.databinding.FragmentFiltedIPsBinding
import com.galaxy.galaxynet.ui.tabs.ip.IPListAdapter
import com.galaxy.util.Constants
import com.galaxy.util.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilteredIPsFragment : Fragment() {

    lateinit var binding: FragmentFiltedIPsBinding
    private val viewModel: ControlPanelViewModel by viewModels()
    private val adapter = IPListAdapter(null , false)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFiltedIPsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val deviceName = arguments?.getString(Constants.DEVICE_NAME_KEY)
        Log.d("test device name", deviceName?:"failed")

        initViews(deviceName)
        initObservers()
    }

    private fun initObservers() {
        viewModel.uiState.observe(viewLifecycleOwner){
            handleState(it)
        }
        viewModel.ipList.observe(viewLifecycleOwner){
            adapter.submitList(it)
        }
    }

    private fun initViews(deviceName: String?) {
        binding.ipsRecycler.adapter = adapter
        deviceName?.let { viewModel.getFilteredIPs(it) }

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