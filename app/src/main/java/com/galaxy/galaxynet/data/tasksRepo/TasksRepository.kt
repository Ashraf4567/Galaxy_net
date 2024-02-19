package com.galaxy.galaxynet.data.tasksRepo

import com.galaxy.galaxynet.model.Task
import com.galaxy.util.AddTaskResult


interface TasksRepository {

    suspend fun addTask(task: Task): AddTaskResult
}