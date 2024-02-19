package com.galaxy.galaxynet.data.tasksRepo

import com.galaxy.galaxynet.model.Task
import com.galaxy.util.AddTaskResult
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TasksRepositoryImpl @Inject constructor(val firestore: FirebaseFirestore) :
    TasksRepository {

    override suspend fun addTask(task: Task): AddTaskResult {
        return try {
            val result = firestore.collection(Task.COLLECTION_NAME).add(task).await()
            AddTaskResult.Success(result)
        } catch (e: Exception) {
            AddTaskResult.Failure(e)
        }
    }
}