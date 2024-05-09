package com.galaxy.galaxynet.ui.tabs.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.galaxy.galaxynet.R
import com.galaxy.galaxynet.databinding.FragmentHomeBinding
import com.galaxy.galaxynet.model.TaskCompletionState
import com.galaxy.galaxynet.ui.auth.AuthViewModel
import com.galaxy.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    private val adapter = AllTasksAdapter(null)
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var tasksState = ""

        val userName = viewModel.sessionManager.getUserData()?.name
        binding.greetingText.text = "مرحبا, " + userName

        initObservers()

        binding.allTasksContainer.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_allTasksFragment)
        }
        binding.completedTasksContainer.setOnClickListener {
            tasksState = TaskCompletionState.COMPLETED.state
            handleNavigation(tasksState)
        }
        binding.newTasksContainer.setOnClickListener {
            tasksState = TaskCompletionState.NEW.state
            handleNavigation(tasksState)
        }

        binding.tasksInProgressContainer.setOnClickListener {
            tasksState = TaskCompletionState.IN_PROGRESS.state
            handleNavigation(tasksState)
        }


    }

    private fun initObservers() {
        viewModel.numOfNewTasks.observe(viewLifecycleOwner) {num ->
            binding.numberOfNewTasks.text = num.toString()
        }
        viewModel.numOfCompletedTasks.observe(viewLifecycleOwner) {num ->
            binding.numberOfCompletedTasks.text = num.toString()
        }
        viewModel.numOfInProcessTasks.observe(viewLifecycleOwner) {num ->
            binding.numberOfInProgressTasks.text = num.toString()
        }
        viewModel.numOfAllTasks.observe(viewLifecycleOwner) {num ->
            binding.numberOfAllTasks.text = num.toString()
        }
    }

    private fun handleNavigation(tasksState: String) {

        val bundle = Bundle()
        bundle.putString(Constants.TASKS_STATE, tasksState)
        findNavController().navigate(R.id.action_homeFragment_to_browseTasksFragment, bundle)
    }


}