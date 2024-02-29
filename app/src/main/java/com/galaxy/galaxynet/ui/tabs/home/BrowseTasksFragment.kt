package com.galaxy.galaxynet.ui.tabs.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.galaxy.galaxynet.databinding.FragmentBrowseTasksBinding
import com.galaxy.galaxynet.ui.TasksViewModel
import com.galaxy.util.Constants
import com.galaxy.util.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BrowseTasksFragment : Fragment() {

    lateinit var binding: FragmentBrowseTasksBinding
    private val viewModel: TasksViewModel by viewModels()
    private val adapter = AllTasksAdapter(null)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBrowseTasksBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val state = arguments?.getString(Constants.TASKS_STATE)
        state?.let { viewModel.getAllTasksByCompletionState(it) }

        initViews(state!!)
        initObservers()
    }

    private fun initObservers() {
        viewModel.allTasksLiveData.observe(viewLifecycleOwner) {
            if (it?.size == 0) {
                binding.notFoundView.visibility = View.VISIBLE
                binding.allTasksAdapter.visibility = View.GONE
            } else {
                adapter.submitList(it)
                binding.notFoundView.visibility = View.GONE
                binding.allTasksAdapter.visibility = View.VISIBLE
            }

        }
        viewModel.messageLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
        }
        viewModel.uIstate.observe(viewLifecycleOwner) {
            handleUISatate(it)
        }
    }

    private fun initViews(state: String) {
        val swipeToRefreshView = binding.swiperefresh
        binding.allTasksAdapter.adapter = adapter
        binding.allTasksText.text = "المهام  $state"
        adapter.onTakeTaskClickListener = AllTasksAdapter.OnTaskClickListener { task, position ->
            viewModel.takeTask(task.id!!)
            viewModel.getAllTasksByCompletionState(state)
        }

        swipeToRefreshView.setOnRefreshListener {
            viewModel.getAllTasksByCompletionState(state)
            swipeToRefreshView.isRefreshing = false
        }

    }

    private fun handleUISatate(uiState: UiState) {
        when (uiState) {
            UiState.SUCCESS -> {
                binding.progressBar.visibility = View.GONE
            }

            UiState.LOADING -> {
                binding.progressBar.visibility = View.VISIBLE
            }

            UiState.ERROR -> {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(activity, "حدث خطأ حاول مره اخري", Toast.LENGTH_SHORT).show()
            }
        }
    }

}