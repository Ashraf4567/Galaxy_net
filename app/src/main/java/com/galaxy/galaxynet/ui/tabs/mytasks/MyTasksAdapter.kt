package com.galaxy.galaxynet.ui.tabs.mytasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.galaxy.galaxynet.R
import com.galaxy.galaxynet.databinding.ItemMyTasksBinding
import com.galaxy.galaxynet.model.Task
import com.galaxy.galaxynet.model.TaskCompletionState


class MyTasksAdapter(var requestsList: MutableList<Task?>?) :
    RecyclerView.Adapter<MyTasksAdapter.ViewHolder>() {

    class ViewHolder(val item: ItemMyTasksBinding) : RecyclerView.ViewHolder(item.root) {
        fun bind(task: Task) {
            item.task = task
            if (task.taskCompletionState.equals(TaskCompletionState.IN_PROGRESS.state)) {
                item.taskState.setBackgroundResource(R.drawable.inprogress_task_bg)
                item.completeTaskBtn.visibility = View.VISIBLE
                item.taskWorkerName.visibility = View.VISIBLE
            }
            if (task.taskCompletionState.equals(TaskCompletionState.COMPLETED.state)) {
                item.taskState.setBackgroundResource(R.drawable.completed_task_bg)
                item.completeTaskBtn.visibility = View.GONE
                item.taskWorkerName.visibility = View.VISIBLE
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = ItemMyTasksBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(item)
    }

    override fun getItemCount(): Int = requestsList?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = requestsList?.get(position)
        holder.bind(task!!)
        onCompleteTaskClickListener.let {
            holder.item.completeTaskBtn.setOnClickListener {
                onCompleteTaskClickListener?.onTaskClick(task, position)
            }
        }

    }

    var onCompleteTaskClickListener: OnTaskClickListener? = null
    var onDeleteTaskClickListener: OnTaskClickListener? = null

    fun interface OnTaskClickListener {
        fun onTaskClick(task: Task, position: Int)
    }

    fun submitList(list: MutableList<Task?>?) {
        requestsList = list
        notifyDataSetChanged()
    }

    fun deleteTask(task: Task, position: Int) {
        requestsList?.remove(task)

        notifyItemRemoved(position)
    }
}