package com.galaxy.galaxynet.ui.controlPanel.employeesManagement

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.galaxy.galaxynet.databinding.FragmentEmployeesListBinding
import com.galaxy.galaxynet.model.User
import com.galaxy.galaxynet.ui.controlPanel.ControlPanelViewModel
import com.galaxy.galaxynet.ui.controlPanel.EmployeesAdapter
import com.galaxy.galaxynet.ui.controlPanel.MenuItem
import com.galaxy.util.UiState
import com.galaxy.util.showConfirmationDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmployeesListFragment : Fragment(), EmployeesAdapter.OnEmployeeClickListener {
    lateinit var binding: FragmentEmployeesListBinding
    private val viewModel: EmployeesViewModel by viewModels()
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

    override fun onOptionSelected(userId: String, menuItem: MenuItem) {

        when (menuItem) {
            MenuItem.DELETE -> {
                showConfirmationDialog(
                    title = "",
                    message = "هل تريد حذف هذا الموظف؟",
                    positiveText = "نعم",
                    negativeText = "الغاء",
                    onPositiveClick = { _, _ ->
                        viewModel.deleteUser(userId)
                        viewModel.getAllEmployees()
                    }
                )
            }
            MenuItem.EDIT_POINTS -> {
                Toast.makeText(requireActivity(), "Edit Points", Toast.LENGTH_SHORT).show()
            }
            else -> {
                // Handle other menu items if needed
            }
        }
    }


}