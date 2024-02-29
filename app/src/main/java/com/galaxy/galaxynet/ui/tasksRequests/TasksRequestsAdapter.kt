package com.galaxy.galaxynet.ui.tasksRequests

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.galaxy.galaxynet.databinding.ItemTaskRequestBinding
import com.galaxy.galaxynet.model.Task


class TasksRequestsAdapter(var requestsList: MutableList<Task?>?) :
    RecyclerView.Adapter<TasksRequestsAdapter.ViewHolder>() {

    class ViewHolder(val item: ItemTaskRequestBinding) : RecyclerView.ViewHolder(item.root) {
        fun bind(task: Task) {
            item.task = task
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = ItemTaskRequestBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(item)
    }

    override fun getItemCount(): Int = requestsList?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = requestsList?.get(position)
        holder.bind(task!!)
        onTaskClickListener.let {
            holder.item.editTaskBtn.setOnClickListener {
                onTaskClickListener?.onTaskClick(task, position)
            }
        }
        onAcceptTaskClickListener.let {
            holder.item.acceptBtn.setOnClickListener {
                onAcceptTaskClickListener?.onTaskClick(task, position)
            }
        }
        onDeleteTaskClickListener.let {
            holder.item.deleteBtn.setOnClickListener {
                onDeleteTaskClickListener?.onTaskClick(task, position)
            }
        }


    }

    var onTaskClickListener: OnTaskClickListener? = null
    var onAcceptTaskClickListener: OnTaskClickListener? = null
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