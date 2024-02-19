package com.galaxy.galaxynet.ui

import androidx.lifecycle.MutableLiveData
import com.galaxy.galaxynet.data.local.SessionManager
import com.galaxy.galaxynet.data.tasksRepo.TasksRepository
import com.galaxy.galaxynet.model.TaskAcceptanceStatus
import com.galaxy.galaxynet.model.TaskCompletionState
import com.galaxy.galaxynet.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val tasksRepository: TasksRepository,
    private val sessionManager: SessionManager
) {
    val isManager: Boolean = sessionManager.getUserData()?.type.equals(User.MANAGER)
    val title = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val taskCompletionState = TaskCompletionState.NEW
    val creatorName = sessionManager.getUserData()?.name
    val taskCategory = MutableLiveData<String>()
    val taskAcceptanceStatus =
        if (isManager) TaskAcceptanceStatus.ACCEPTED else TaskAcceptanceStatus.PENDING
    val points = MutableLiveData<String>()
}