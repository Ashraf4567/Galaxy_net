package com.galaxy.galaxynet.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.galaxy.galaxynet.databinding.FragmentAddTaskBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTaskFragment : BottomSheetDialogFragment() {
    lateinit var binding: FragmentAddTaskBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddTaskBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveTaskBtn.setOnClickListener {
            onTaskAddedListener?.onTaskSent()
            dismiss()
        }

    }
    /*
    page profile for manager is different than employee
    manger -> menu (my account - add tasks request - employees list - add Account)
    employee -> just his profile name and points and completed task by him
     */

    var onTaskAddedListener: OnTaskSentListener? = null

    fun interface OnTaskSentListener{
        fun onTaskSent()
    }


}