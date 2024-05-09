package com.galaxy.galaxynet.data.tasksRepo

import com.galaxy.galaxynet.model.Task
import com.galaxy.util.AddTaskResult
import com.galaxy.util.TransactionResult


interface TasksRepository {

    suspend fun addTask(task: Task): AddTaskResult

    suspend fun getBindingTasks(): List<Task?>?

    suspend fun updateTask(id: String, updatedTask: Task): TransactionResult

    suspend fun acceptTask(id: String): TransactionResult

    suspend fun takeTask(id: String, workerName: String): TransactionResult
    suspend fun completeTask(task: Task, workerId: String): TransactionResult
    suspend fun deleteTask(id: String): TransactionResult

    suspend fun getAllTasksByCategory(category: String): List<Task?>?
    suspend fun getTasksByCompletionState(state: String): List<Task?>?
    suspend fun getCurrentUserTasks(userName: String): List<Task?>?
    suspend fun getTaskCount(): Int

    suspend fun getTaskById(id: String): Task
}