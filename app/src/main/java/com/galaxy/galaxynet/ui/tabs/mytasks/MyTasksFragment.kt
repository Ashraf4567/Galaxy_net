package com.galaxy.galaxynet.ui.tabs.mytasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.galaxy.galaxynet.databinding.FragmentMyTasksBinding
import com.galaxy.galaxynet.ui.TasksViewModel
import com.galaxy.util.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyTasksFragment : Fragment() {
    lateinit var binding: FragmentMyTasksBinding
    private val adapter = MyTasksAdapter(null)
    private val viewModel: TasksViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMyTasksBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initObservers()
    }

    private fun initViews() {
        val swipeToRefreshView = binding.swiperefresh
        binding.allTasksAdapter.adapter = adapter
        viewModel.getCurrentUserTasks()
        adapter.onCompleteTaskClickListener = MyTasksAdapter.OnTaskClickListener { task, position ->
            viewModel.completeTask(task)
            viewModel.getCurrentUserTasks()

        }
        swipeToRefreshView.setOnRefreshListener {
            viewModel.getCurrentUserTasks()
            swipeToRefreshView.isRefreshing = false
        }
    }

    private fun initObservers() {
        viewModel.allTasksLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.uIstate.observe(viewLifecycleOwner) {
            handleUISatate(it)
        }
        viewModel.messageLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
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