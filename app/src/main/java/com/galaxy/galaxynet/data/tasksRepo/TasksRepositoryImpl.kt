package com.galaxy.galaxynet.data.tasksRepo

import android.util.Log
import com.galaxy.galaxynet.model.Task
import com.galaxy.galaxynet.model.TaskAcceptanceStatus
import com.galaxy.galaxynet.model.TaskCompletionState
import com.galaxy.galaxynet.model.User
import com.galaxy.util.AddTaskResult
import com.galaxy.util.TransactionResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TasksRepositoryImpl @Inject constructor(
    val firestore: FirebaseFirestore
) :
    TasksRepository {
    private val usersCollection = firestore.collection(User.COLLECTION_NAME)


    override suspend fun addTask(task: Task): AddTaskResult {
        return try {
            val result = firestore.collection(Task.COLLECTION_NAME).add(task).await()
            AddTaskResult.Success(result)
        } catch (e: Exception) {
            AddTaskResult.Failure(e)
        }
    }

    override suspend fun getBindingTasks(): List<Task?> {
        val pendingTasksQuery: Query = firestore.collection(Task.COLLECTION_NAME)
            .whereEqualTo(
                "taskAcceptanceStatus",
                TaskAcceptanceStatus.PENDING.state
            ) // Filter for pending tasks

        val snapshot = pendingTasksQuery.get().await()
        val tasks = snapshot.toObjects(Task::class.java) // Convert documents to Task objects

        return tasks
    }

    override suspend fun updateTask(id: String, updatedTask: Task): TransactionResult {
        return try {
            // Query to find the document with the matching ID field
            val querySnapshot = firestore.collection(Task.COLLECTION_NAME)
                .whereEqualTo("id", id)
                .get()
                .await()

            if (querySnapshot.documents.isEmpty()) {
                // Task document not found
                return TransactionResult.Failure(Exception("Task with ID $id not found"))
            }

            val taskDocument = querySnapshot.documents.first()
            val result = taskDocument.reference.set(updatedTask.copy(id = id))
                .await() // Preserve original ID
            TransactionResult.Success()

        } catch (e: Exception) {
            TransactionResult.Failure(e)
        }
    }

    override suspend fun acceptTask(id: String): TransactionResult {
        return try {
            // Query to find the task with the matching ID
            val querySnapshot = firestore.collection(Task.COLLECTION_NAME)
                .whereEqualTo("id", id)
                .get()
                .await()

            if (querySnapshot.documents.isEmpty()) {
                // Task not found
                return TransactionResult.Failure(Exception("Task with ID $id not found"))
            }

            val taskDocument = querySnapshot.documents.first()
            val task = taskDocument.toObject(Task::class.java)!!

            val updatedTask = task.copy(
                taskAcceptanceStatus = TaskAcceptanceStatus.ACCEPTED.state,
                taskCompletionState = TaskCompletionState.NEW.state
            )

            // Update the task in Firestore
            taskDocument.reference.set(updatedTask).await()

            // Return the updated task
            TransactionResult.Success()

        } catch (e: Exception) {
            Log.d("test Update task", " error task ${e.localizedMessage}")
            TransactionResult.Failure(e)
        }
    }

    override suspend fun takeTask(id: String, workerName: String): TransactionResult {
        return try {
            // Query to find the task with the matching ID
            val querySnapshot = firestore.collection(Task.COLLECTION_NAME)
                .whereEqualTo("id", id)
                .get()
                .await()

            if (querySnapshot.documents.isEmpty()) {
                // Task not found
                return TransactionResult.Failure(Exception("Task with ID $id not found"))
            }

            val taskDocument = querySnapshot.documents.first()
            val task = taskDocument.toObject(Task::class.java)!!

            val updatedTask = task.copy(
                workerName = workerName,
                taskCompletionState = TaskCompletionState.IN_PROGRESS.state
            )

            // Update the task in Firestore
            taskDocument.reference.set(updatedTask).await()

            // Return the updated task
            TransactionResult.Success()

        } catch (e: Exception) {
            Log.d("test Update task", " error task ${e.localizedMessage}")
            TransactionResult.Failure(e)
        }
    }

    override suspend fun deleteTask(id: String): TransactionResult {
        try {
            // Query to find the task with the matching ID
            val querySnapshot = firestore.collection(Task.COLLECTION_NAME)
                .whereEqualTo("id", id)
                .get()
                .await()

            if (querySnapshot.documents.isEmpty()) {
                // Task not found
                Log.d("test Delete task", "Task with ID $id not found")
                return TransactionResult.Failure(Exception("Task with ID $id not found"))
            }

            val taskDocument = querySnapshot.documents.first()
            taskDocument.reference.delete().await()

            return TransactionResult.Success()


        } catch (e: Exception) {
            Log.e("test Delete task", "Error deleting task: $e")
            return TransactionResult.Failure(e)
        }
    }

    override suspend fun getAllTasksByCategory(category: String): List<Task?>? {
        try {
            val querySnapshot = firestore.collection(Task.COLLECTION_NAME)
                .whereEqualTo("taskAcceptanceStatus", TaskAcceptanceStatus.ACCEPTED.state)
                .whereEqualTo("taskCategory", category)
                .orderBy("dateTime", Query.Direction.DESCENDING)
                .get()
                .await()

            val tasks = mutableListOf<Task>()
            for (document in querySnapshot.documents) {
                val task = document.toObject(Task::class.java)!!
                Log.e("test Get tasks suc", "${task.creatorName}")
                tasks.add(task)
            }

            return tasks
        } catch (e: Exception) {
            Log.e("test Get tasks error", "Error getting tasks: $e")
            // Handle errors (e.g., show an error message)
            return emptyList() // Return an empty list on error
        }
    }

    override suspend fun getTasksByCompletionState(state: String): List<Task?>? {
        try {
            val querySnapshot = firestore.collection(Task.COLLECTION_NAME)
                .whereEqualTo("taskAcceptanceStatus", TaskAcceptanceStatus.ACCEPTED.state)
                .whereEqualTo("taskCompletionState", state)
                .orderBy("dateTime", Query.Direction.DESCENDING)
                .get()
                .await()

            val tasks = mutableListOf<Task>()
            for (document in querySnapshot.documents) {
                val task = document.toObject(Task::class.java)!!
                Log.e("test Get tasks suc", "${task.creatorName}")
                tasks.add(task)
            }

            return tasks
        } catch (e: Exception) {
            Log.e("test Get tasks error", "Error getting tasks: $e")
            // Handle errors (e.g., show an error message)
            return emptyList() // Return an empty list on error
        }
    }

    override suspend fun getCurrentTasks(userName: String): List<Task?>? {
        try {
            val querySnapshot = firestore.collection(Task.COLLECTION_NAME)
                .whereEqualTo("workerName", userName)
                .orderBy("dateTime", Query.Direction.DESCENDING)
                .get()
                .await()

            val tasks = mutableListOf<Task>()
            for (document in querySnapshot.documents) {
                val task = document.toObject(Task::class.java)!!
                Log.e("test current user tasks", "${task.creatorName}")
                tasks.add(task)
            }

            return tasks
        } catch (e: Exception) {
            Log.e("test current user tasks", "Error getting tasks: $e")
            // Handle errors (e.g., show an error message)
            return emptyList() // Return an empty list on error
        }
    }

    override suspend fun completeTask(task: Task, workerId: String): TransactionResult {
        return try {
            // 1. Update task completion state by querying
            val taskRef = firestore.collection("tasks")
                .whereEqualTo("id", task.id)
                .get().await().firstOrNull()

            if (taskRef != null) {
                taskRef.reference.update("taskCompletionState", TaskCompletionState.COMPLETED.state)
                    .await()
            } else {
                throw Exception("Task with ID ${task.id} not found")
            }

            // 2. Update user's points
            val userRef = usersCollection.document(workerId)
            val userSnapshot = userRef.get().await()
            val user = userSnapshot.toObject(User::class.java)
                ?: throw Exception("User with ID $workerId not found")

            val updatedPoints = user.points!!.toInt() + (task.points?.toInt()
                ?: 0)  // Handle potential nullity for points
            val updatedNumberOfCompletedTasks = user.numberOfCompletedTasks.toInt() + 1
            userRef.update("points", updatedPoints).await()
            userRef.update("numberOfCompletedTasks", updatedNumberOfCompletedTasks).await()

            TransactionResult.Success()
        } catch (e: Exception) {
            TransactionResult.Failure(e)
        }
    }

    override suspend fun getTaskById(id: String): Task {
        try {
            Log.e("test id", id)
            val querySnapshot = firestore.collection(Task.COLLECTION_NAME)
                .whereEqualTo("id", id)
                .get()
                .await()

            if (querySnapshot.documents.isEmpty()) {
                // Task not found
                throw Exception("Task with ID $id not found")
            }

            val taskDocument = querySnapshot.documents.first()
            val task = taskDocument.toObject(Task::class.java)!!

            return task

        } catch (e: Exception) {
            Log.e("Get task by ID", "Error getting task: $e")
            throw e // Re-throw the exception for proper handling
        }
    }

}