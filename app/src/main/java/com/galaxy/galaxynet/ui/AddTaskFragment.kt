package com.galaxy.galaxynet.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.galaxy.galaxynet.databinding.FragmentAddTaskBinding
import com.galaxy.util.UiState
import com.galaxy.util.hideKeyboard
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTaskFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddTaskBinding
    private val viewModel: TasksViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddTaskBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initObservers()
    }

    private fun initObservers() {
        viewModel.uIstate.observe(viewLifecycleOwner) {
            handleState(it)
        }
    }

    private fun handleState(state: UiState) {
        when (state) {
            UiState.SUCCESS -> {
                binding.progressBar.visibility = View.GONE
                onTaskAddedListener?.onTaskSent()
                dismiss()
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

    private fun initViews() {
        binding.vm = viewModel
        binding.lifecycleOwner = this
        binding.saveTaskBtn.setOnClickListener {
            viewModel.addTask()
            hideKeyboard()
        }
        binding.cancelTaskBtn.setOnClickListener {
            dismiss()
        }
    }
    /*
    page profile for manager is different than employee
    manger -> menu (my account - add tasks request - employees list - add Account)
    employee -> just his profile name and points and completed task by him
     */

    var onTaskAddedListener: OnTaskSentListener? = null

    fun interface OnTaskSentListener {
        fun onTaskSent()
    }


}