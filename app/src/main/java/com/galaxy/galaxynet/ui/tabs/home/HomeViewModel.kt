package com.galaxy.galaxynet.ui.tabs.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galaxy.galaxynet.data.local.SessionManager
import com.galaxy.galaxynet.data.tasksRepo.TasksRepository
import com.galaxy.galaxynet.model.TaskCompletionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val sessionManager: SessionManager,
    private val taskRepository: TasksRepository
): ViewModel() {

    private val _numOfCompletedTasks = MutableLiveData<Int?>()
    val numOfCompletedTasks: MutableLiveData<Int?> = _numOfCompletedTasks

    private val _numOfAllTasks = MutableLiveData<Int?>()
    val numOfAllTasks: MutableLiveData<Int?> = _numOfAllTasks

    private val _numOfInProcessTasks = MutableLiveData<Int?>()
    val numOfInProcessTasks: MutableLiveData<Int?> = _numOfInProcessTasks

    private val _numOfNewTasks = MutableLiveData<Int?>()
    val numOfNewTasks: MutableLiveData<Int?> = _numOfNewTasks

    init {
        bindTasksNumbers()
    }

    private fun bindTasksNumbers() {
        viewModelScope.launch(Dispatchers.IO) {
            val numOfAllTasks = async { taskRepository.getTaskCount().collect { _numOfAllTasks.postValue(it.data) } }
            val numOfCompletedTasks = async { taskRepository.getTasksByCompletionState(TaskCompletionState.COMPLETED.state)?.size }
            val numOfInProcessTasks = async { taskRepository.getTasksByCompletionState(TaskCompletionState.IN_PROGRESS.state)?.size }
            val numOfNewTasks = async { taskRepository.getTasksByCompletionState(TaskCompletionState.NEW.state)?.size }

            _numOfCompletedTasks.postValue(numOfCompletedTasks.await())
            _numOfInProcessTasks.postValue(numOfInProcessTasks.await())
            _numOfNewTasks.postValue(numOfNewTasks.await())
        }
    }
}