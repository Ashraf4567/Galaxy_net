package com.galaxy.galaxynet.ui.controlPanel.employeesManagement

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.galaxy.galaxynet.R
import com.galaxy.galaxynet.databinding.FragmentEditPointsBinding
import com.galaxy.galaxynet.model.TransactionHistory
import com.galaxy.galaxynet.model.TransactionType
import com.galaxy.util.Constants
import com.galaxy.util.UiState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPointsFragment: BottomSheetDialogFragment() {

    private lateinit var binding: FragmentEditPointsBinding
    private val viewModel: EmployeesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditPointsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = arguments?.getString(Constants.USER_ID_KEY)
        val employeeName = arguments?.getString("EmployeeName")


        binding.addPontsBtn.setOnClickListener {
            val note = binding.notes.text.toString()
            if (note.isEmpty()){
                Toast.makeText(requireContext(), "Please enter a note", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else{
                val points =  binding.points.text.toString().toInt()
                viewModel.editPoints(userId?:"", points , TransactionType.ADD.value , note , employeeName?:"")
            }

        }
        binding.decreasePointsBtn.setOnClickListener {
            val note = binding.notes.text.toString()
            if (note.isEmpty()){
                Toast.makeText(requireContext(), "Please enter a note", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else{
                val points =  binding.points.text.toString().toInt()
                viewModel.editPoints(userId?:"", -points , TransactionType.REMOVE.value , note , employeeName?:"")
            }
        }
        initObservers()
    }

    private fun initObservers() {
        viewModel.editPointsUiState.observe(viewLifecycleOwner){
            handleState(it)
        }
        viewModel.messageLiveData.observe(viewLifecycleOwner){
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleState(state: UiState?) {
        when (state) {
            UiState.SUCCESS -> {
                binding.progressBar.visibility = View.GONE
                viewModel.getAllEmployees()
                dismiss()

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

}