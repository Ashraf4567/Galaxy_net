package com.galaxy.galaxynet.ui.controlPanel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.galaxy.galaxynet.databinding.ItemEmployeeBinding
import com.galaxy.galaxynet.model.User


class EmployeesAdapter(var employeesList: MutableList<User?>?) :
    RecyclerView.Adapter<EmployeesAdapter.ViewHolder>() {

    class ViewHolder(val item: ItemEmployeeBinding) : RecyclerView.ViewHolder(item.root) {
        fun bind(employee: User?) {
            item.user = employee
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = ItemEmployeeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(item)
    }

    override fun getItemCount(): Int = employeesList?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val employee = employeesList!![position]
        holder.bind(employee)
    }


    fun submitList(list: List<User?>?) {
        employeesList = list as MutableList<User?>?
        notifyDataSetChanged()
    }

}