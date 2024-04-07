package com.galaxy.galaxynet.ui.controlPanel

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.galaxy.galaxynet.databinding.ActivityEditTaskBinding
import com.galaxy.galaxynet.ui.TasksViewModel
import com.galaxy.util.Constants
import com.galaxy.util.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditTaskBinding
    private val viewModel: TasksViewModel by viewModels()
    var taskId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        taskId = intent.getStringExtra(Constants.TASK_ID).toString()
        initViews()
        initObservers()
        viewModel.messageLiveData.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            finish()
        }

    }

    private fun initObservers() {
        viewModel.uIstate.observe(this) {
            handleUISatate(it)
        }
        viewModel.taskLiveData.observe(this) { task ->
            val categoryPosition =
                (binding?.categorySpinner?.adapter as? ArrayAdapter<String>)?.getPosition(task.taskCategory)
                    ?: -1

            if (categoryPosition >= 0) {
                binding?.categorySpinner?.setSelection(categoryPosition)
            }
        }
    }

    private fun initViews() {
        binding.vm = viewModel
        binding.lifecycleOwner = this
        binding.cancelTaskBtn.setOnClickListener {
            finish()
        }
        binding.saveTaskBtn.setOnClickListener {
            viewModel.updateTask(taskId)
        }
        Log.e("test from activity", taskId)
        if (taskId.isNotBlank()) {

            viewModel.getTaskById(taskId)
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
                Toast.makeText(this, "حدث خطأ حاول مره اخري", Toast.LENGTH_SHORT).show()
            }
        }
    }

}