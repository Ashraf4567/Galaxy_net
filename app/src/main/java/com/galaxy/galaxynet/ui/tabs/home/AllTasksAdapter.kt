package com.galaxy.galaxynet.ui.tabs.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.galaxy.galaxynet.R
import com.galaxy.galaxynet.databinding.ItemAllTasksBinding
import com.galaxy.galaxynet.model.Task
import com.galaxy.galaxynet.model.TaskCompletionState


class AllTasksAdapter(var tasksList: MutableList<Task?>?) :
    RecyclerView.Adapter<AllTasksAdapter.ViewHolder>() {

    class ViewHolder(val item: ItemAllTasksBinding) : RecyclerView.ViewHolder(item.root) {
        fun bind(task: Task) {
            item.task = task
            if (task.taskCompletionState.equals(TaskCompletionState.NEW.state)) {
                item.taskState.setBackgroundResource(R.drawable.new_task_bg)
                item.takeTaskBtn.visibility = View.VISIBLE
                item.taskWorkerName.visibility = View.GONE
            }
            if (task.taskCompletionState.equals(TaskCompletionState.IN_PROGRESS.state)) {
                item.taskState.setBackgroundResource(R.drawable.inprogress_task_bg)
                item.takeTaskBtn.visibility = View.GONE
                item.taskWorkerName.visibility = View.VISIBLE
            }
            if (task.taskCompletionState.equals(TaskCompletionState.COMPLETED.state)) {
                item.taskState.setBackgroundResource(R.drawable.completed_task_bg)
                item.takeTaskBtn.visibility = View.GONE
                item.taskWorkerName.visibility = View.VISIBLE
            }
            item.icEdit.visibility = View.VISIBLE
            item.icDelete.visibility = View.VISIBLE
            item.executePendingBindings()
            
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = ItemAllTasksBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(item)
    }

    override fun getItemCount(): Int = tasksList?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = tasksList?.get(position)
        holder.bind(task!!)
        onTakeTaskClickListener.let {
            holder.item.takeTaskBtn.setOnClickListener {
                onTakeTaskClickListener?.onTaskClick(task, position)
            }
        }

        onDeleteTaskClickListener.let {
            holder.item.icDelete.setOnClickListener {
                onDeleteTaskClickListener?.onTaskClick(task, position)
            }
        }
        onEditTaskClickListener.let {
            holder.item.icEdit.setOnClickListener {
                onEditTaskClickListener?.onTaskClick(task, position)
            }
        }

    }

    var onTakeTaskClickListener: OnTaskClickListener? = null
    var onDeleteTaskClickListener: OnTaskClickListener? = null
    var onEditTaskClickListener: OnTaskClickListener? = null

    fun interface OnTaskClickListener {
        fun onTaskClick(task: Task, position: Int)
    }

    fun submitList(list: MutableList<Task?>?) {
        tasksList = list
        notifyDataSetChanged()
    }

    fun deleteTask(task: Task, position: Int) {
        tasksList?.remove(task)

        notifyItemRemoved(position)
    }
}