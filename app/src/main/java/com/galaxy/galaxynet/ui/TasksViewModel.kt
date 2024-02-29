package com.galaxy.galaxynet.ui

import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galaxy.galaxynet.data.local.SessionManager
import com.galaxy.galaxynet.data.tasksRepo.TasksRepository
import com.galaxy.galaxynet.model.Task
import com.galaxy.galaxynet.model.TaskAcceptanceStatus
import com.galaxy.galaxynet.model.TaskCompletionState
import com.galaxy.galaxynet.model.User
import com.galaxy.util.AddTaskResult
import com.galaxy.util.TransactionResult
import com.galaxy.util.UiState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val tasksRepository: TasksRepository,
    private val sessionManager: SessionManager,
    val auth: FirebaseAuth
) : ViewModel() {
    val isManager: Boolean = sessionManager.getUserData()?.type.equals(User.MANAGER)
    val title = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val taskCompletionState = TaskCompletionState.NEW.state
    val creatorName = sessionManager.getUserData()?.name
    var taskCategory = ""
    val taskAcceptanceStatus =
        if (isManager) TaskAcceptanceStatus.ACCEPTED.state else TaskAcceptanceStatus.PENDING.state
    val points = MutableLiveData<String?>()

    val uIstate = MutableLiveData<UiState>()
    val requestsLiveData = MutableLiveData<MutableList<Task?>?>()
    val allTasksLiveData = MutableLiveData<MutableList<Task?>?>()
    val messageLiveData = MutableLiveData<String>()
    val taskLiveData = MutableLiveData<Task>()

    val clicksListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            taskCategory = parent?.getItemAtPosition(position) as String
        }
    }

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.toString()


    fun getTaskById(id: String) {
        uIstate.postValue(UiState.LOADING)
        viewModelScope.launch {
            try {
                val result = tasksRepository.getTaskById(id)

                title.postValue(result.title!!)
                description.postValue(result.description!!)
                points.postValue(result.points)
                //make result.taskCategory is selected on spinner
                taskLiveData.postValue(result)
                uIstate.postValue(UiState.SUCCESS)
                Log.e("test task id from vm", id)
            } catch (e: Exception) {
                uIstate.postValue(UiState.ERROR)
                Log.e("test get task object", e.message.toString())
            }
        }
    }

    fun addTask() {
        uIstate.postValue(UiState.LOADING)
        val task = Task(
            id = UUID.randomUUID().toString(),
            title = title.value,
            description = description.value,
            taskCompletionState = taskCompletionState,
            creatorName = creatorName,
            taskCategory = taskCategory,
            taskAcceptanceStatus = taskAcceptanceStatus,
            points = points.value
        )
        viewModelScope.launch {
            try {
                val result = tasksRepository.addTask(task)
                when (result) {
                    is AddTaskResult.Success -> {
                        uIstate.postValue(UiState.SUCCESS)
                        Log.d("test Add task", "task added successfully")
                    }

                    is AddTaskResult.Failure -> {
                        val exception = result.exception.localizedMessage
                        uIstate.postValue(UiState.SUCCESS)
                        Log.d("test Add task ", exception ?: "")
                    }
                }
            } catch (e: Exception) {
                uIstate.postValue(UiState.ERROR)
                Log.d("test get Tasks", e.localizedMessage ?: "something went wrong")
            }

        }
    }

    fun updateTask(id: String) {
        val task = Task(
            id = id,
            title = title.value,
            description = description.value,
            creatorName = creatorName,
            taskCategory = taskCategory,
            taskAcceptanceStatus = taskAcceptanceStatus,
            points = points.value,
            taskCompletionState = taskCompletionState
        )
        uIstate.postValue(UiState.LOADING)
        viewModelScope.launch {
            try {
                val result = tasksRepository.updateTask(id, task)
                when (result) {
                    is TransactionResult.Success -> {
                        messageLiveData.postValue("تم التعديل علي المهمه والاضافه بنجاح")
                        uIstate.postValue(UiState.SUCCESS)
                        Log.d("test Update task", "task updated successfully")
                    }

                    is TransactionResult.Failure -> {
                        val exception = result.exception?.localizedMessage
                        uIstate.postValue(UiState.ERROR)
                        Log.d("test Update task", exception ?: "")
                    }
                }
            } catch (e: Exception) {
                uIstate.postValue(UiState.ERROR)
                Log.d("test Update task", e.localizedMessage ?: "something went wrong")
            }
        }
    }

    fun getTasksRequests() {
        uIstate.postValue(UiState.LOADING)
        viewModelScope.launch {
            try {
                val result = tasksRepository.getBindingTasks()
                requestsLiveData.postValue(result as MutableList<Task?>?)
                uIstate.postValue(UiState.SUCCESS)
            } catch (e: Exception) {
                uIstate.postValue(UiState.ERROR)
                Log.d("test get Tasks", e.localizedMessage ?: "something went wrong")
            }

        }
    }


    fun getAllTasksByCategory(category: String) {
        uIstate.postValue(UiState.LOADING)
        viewModelScope.launch {
            try {
                val result = tasksRepository.getAllTasksByCategory(category)
                allTasksLiveData.postValue(result as MutableList<Task?>?)
                uIstate.postValue(UiState.SUCCESS)
            } catch (e: Exception) {
                uIstate.postValue(UiState.ERROR)
                Log.d("test get Tasks", e.localizedMessage ?: "something went wrong")
            }

        }
    }

    fun getAllTasksByCompletionState(state: String) {
        uIstate.postValue(UiState.LOADING)
        viewModelScope.launch {
            try {
                val result = tasksRepository.getTasksByCompletionState(state)
                allTasksLiveData.postValue(result as MutableList<Task?>?)
                uIstate.postValue(UiState.SUCCESS)
            } catch (e: Exception) {
                uIstate.postValue(UiState.ERROR)
                Log.d("test get Tasks", e.localizedMessage ?: "something went wrong")
            }

        }
    }

    fun getCurrentUserTasks() {
        uIstate.postValue(UiState.LOADING)
        viewModelScope.launch {
            try {
                val result =
                    tasksRepository.getCurrentTasks(sessionManager.getUserData()?.name.toString())
                allTasksLiveData.postValue(result as MutableList<Task?>?)
                uIstate.postValue(UiState.SUCCESS)
            } catch (e: Exception) {
                uIstate.postValue(UiState.ERROR)
                Log.d("test get Tasks", e.localizedMessage ?: "something went wrong")
            }

        }
    }

    fun acceptTask(id: String) {
        uIstate.postValue(UiState.LOADING)
        viewModelScope.launch {
            try {
                val result = tasksRepository.acceptTask(id)
                when (result) {
                    is TransactionResult.Success -> {
                        messageLiveData.postValue("تم قبول المهمه بنجاح")
                        uIstate.postValue(UiState.SUCCESS)
                    }

                    is TransactionResult.Failure -> {
                        messageLiveData.postValue("حدث خطأ ما حاول مره اخري")
                        uIstate.postValue(UiState.ERROR)
                        Log.d("test Update task", "fail")
                    }
                }
            } catch (e: Exception) {
                uIstate.postValue(UiState.ERROR)
                messageLiveData.postValue("حدث خطأ ما حاول مره اخري")
                Log.d("test Update task", e.localizedMessage ?: "something went wrong")
            }
        }
    }

    fun takeTask(id: String) {
        uIstate.postValue(UiState.LOADING)
        viewModelScope.launch {
            try {
                val result =
                    tasksRepository.takeTask(id, sessionManager.getUserData()?.name.toString())
                when (result) {
                    is TransactionResult.Failure -> {
                        messageLiveData.postValue("حدث خطأ ما حاول مره اخري")
                        uIstate.postValue(UiState.ERROR)
                        Log.d("test Update task", "fail")
                    }

                    is TransactionResult.Success -> {
                        messageLiveData.postValue("تم سحب المهمه بنجاح")
                        uIstate.postValue(UiState.SUCCESS)
                    }
                }

            } catch (e: Exception) {
                uIstate.postValue(UiState.ERROR)
                messageLiveData.postValue("حدث خطأ ما حاول مره اخري")
                Log.d("test Update task", e.localizedMessage ?: "something went wrong")
            }
        }
    }


    fun deleteTask(id: String) {
        uIstate.postValue(UiState.LOADING)
        viewModelScope.launch {
            try {
                if (sessionManager.getUserData()?.type.equals(User.MANAGER)) {
                    val result = tasksRepository.deleteTask(id)
                    when (result) {
                        is TransactionResult.Failure -> {
                            uIstate.postValue(UiState.ERROR)
                            messageLiveData.postValue("حدث خطأ ما حاول مره اخري")
                        }

                        is TransactionResult.Success -> {
                            messageLiveData.postValue("تم حذف المهمه")
                            uIstate.postValue(UiState.SUCCESS)
                        }
                    }
                } else {
                    messageLiveData.postValue("لا يوجد لديك صلاحيه الحذف")
                    uIstate.postValue(UiState.SUCCESS)
                }


            } catch (e: Exception) {
                uIstate.postValue(UiState.ERROR)
                messageLiveData.postValue("حدث خطأ ما حاول مره اخري")
                Log.d("test Update task", e.localizedMessage ?: "something went wrong")
            }
        }
    }

    fun completeTask(task: Task) {
        uIstate.postValue(UiState.LOADING)
        viewModelScope.launch {
            try {
                val result = tasksRepository.completeTask(task, currentUserId)
                when (result) {
                    is TransactionResult.Failure -> {
                        uIstate.postValue(UiState.ERROR)
                        messageLiveData.postValue("حدث خطأ ما حاول مره اخري")
                    }

                    is TransactionResult.Success -> {
                        messageLiveData.postValue("تم اكمال المهمه")
                        uIstate.postValue(UiState.SUCCESS)
                    }
                }

            } catch (e: Exception) {
                uIstate.postValue(UiState.ERROR)
                messageLiveData.postValue("حدث خطأ ما حاول مره اخري")
                Log.d("test Update task", e.localizedMessage ?: "something went wrong")
            }
        }
    }

}