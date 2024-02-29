package com.galaxy.galaxynet.ui.tasksRequests

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.galaxy.galaxynet.databinding.FragmentTasksRequestsBinding
import com.galaxy.galaxynet.ui.TasksViewModel
import com.galaxy.galaxynet.ui.controlPanel.EditTaskActivity
import com.galaxy.util.Constants
import com.galaxy.util.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksRequestsFragment : Fragment() {
    lateinit var binding: FragmentTasksRequestsBinding
    private val adapter = TasksRequestsAdapter(null)
    private val viewModel: TasksViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTasksRequestsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getTasksRequests()
        initViews()
        initObservers()
    }

    private fun initObservers() {
        viewModel.requestsLiveData.observe(viewLifecycleOwner) {
            if (it?.isEmpty() == true) {
                binding.notFoundView.visibility = View.VISIBLE
            }
            adapter.submitList(it)
        }
        viewModel.uIstate.observe(viewLifecycleOwner) {
            handleUISatate(it)
        }

        viewModel.messageLiveData.observe(viewLifecycleOwner) {
            if (it.equals("تم قبول المهمه بنجاح")) {
                Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
            }
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
            }
        }
    }

    private fun initViews() {
        binding.requestsAdapter.adapter = adapter
        adapter.onTaskClickListener = TasksRequestsAdapter.OnTaskClickListener { task, position ->
            val intent = Intent(requireActivity(), EditTaskActivity::class.java).apply {
                putExtra(Constants.TASK_ID, task.id)
                Log.d("test id", task.id.toString())
                startActivity(this)
            }
        }
        adapter.onAcceptTaskClickListener =
            TasksRequestsAdapter.OnTaskClickListener { task, position ->
                viewModel.acceptTask(task.id!!)
                adapter.deleteTask(task, position)
                if (adapter.requestsList?.isEmpty() == true) {
                    binding.notFoundView.visibility = View.VISIBLE
                }
            }
        adapter.onDeleteTaskClickListener =
            TasksRequestsAdapter.OnTaskClickListener { task, position ->
                viewModel.deleteTask(task.id!!)
                adapter.deleteTask(task, position)
                if (adapter.requestsList?.isEmpty() == true) {
                    binding.notFoundView.visibility = View.VISIBLE
                }
            }


    }

    override fun onStart() {
        super.onStart()
        viewModel.getTasksRequests()
    }


}