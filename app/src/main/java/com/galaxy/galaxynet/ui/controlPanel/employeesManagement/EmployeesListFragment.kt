package com.galaxy.galaxynet.ui.controlPanel.employeesManagement

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.galaxy.galaxynet.R
import com.galaxy.galaxynet.databinding.FragmentEmployeesListBinding
import com.galaxy.galaxynet.model.User
import com.galaxy.galaxynet.ui.controlPanel.MenuItem
import com.galaxy.util.UiState
import com.galaxy.util.showConfirmationDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmployeesListFragment : Fragment(), EmployeesAdapter.OnEmployeeClickListener {
    lateinit var binding: FragmentEmployeesListBinding
    private val viewModel: EmployeesViewModel by activityViewModels()
    private val adapter = EmployeesAdapter(null)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEmployeesListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initObservers()
    }

    private fun initObservers() {
        viewModel.users.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.uIstate.observe(viewLifecycleOwner) { it ->
            handleState(it)
        }
        viewModel.messageLiveData.observe(viewLifecycleOwner){
            Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleState(state: UiState?) {
        when (state) {
            UiState.SUCCESS -> {
                binding.progressBar.visibility = View.GONE
            }

            UiState.LOADING -> {
                binding.progressBar.visibility = View.VISIBLE
            }

            UiState.ERROR -> {
                binding.progressBar.visibility = View.GONE
            }

            else -> {

            }
        }
    }

    private fun initViews() {
        binding.employeesListRecycler.adapter = adapter
        adapter.onEmployeeClickListener = this
        viewModel.getAllEmployees()


    }

    override fun onEmployeeClick(employee: User?) {
        Log.d("EmployeesListFragment", "onEmployeeClick called")
    }

    override fun onOptionSelected(user: User, menuItem: MenuItem) {

        when (menuItem) {
            MenuItem.EDIT_POINTS -> {
                val bundle = Bundle()
                bundle.putString("userId", user.id)
                bundle.putString("EmployeeName", user.name)
                findNavController().navigate(R.id.action_employeesListFragment_to_editPointsFragment , bundle)
            }

            MenuItem.ACTIVE_ACCOUNT -> {

                showConfirmationDialog(
                    title = "",
                    message = "هل تريد تفعيل حساب الموظف؟",
                    positiveText = "نعم",
                    negativeText = "الغاء",
                    onPositiveClick = { _, _ ->
                        viewModel.activeAccount(user.id?:"")
                        viewModel.getAllEmployees()
                    }
                )

            }
            MenuItem.DISABLE_ACCOUNT -> {
                showConfirmationDialog(
                    title = "",
                    message = "هل تريد ايقاف حساب الموظف؟",
                    positiveText = "نعم",
                    negativeText = "الغاء",
                    onPositiveClick = { _, _ ->
                        viewModel.deleteUser(user.id?:"")
                        viewModel.getAllEmployees()
                    }
                )
            }
            MenuItem.UNKNOWN -> {}
        }
    }


}