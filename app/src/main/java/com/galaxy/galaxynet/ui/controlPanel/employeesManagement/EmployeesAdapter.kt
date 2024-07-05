package com.galaxy.galaxynet.ui.controlPanel.employeesManagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.galaxy.galaxynet.R
import com.galaxy.galaxynet.databinding.ItemEmployeeBinding
import com.galaxy.galaxynet.model.User
import com.galaxy.galaxynet.ui.controlPanel.MenuItem


class EmployeesAdapter(var employeesList: MutableList<User?>?) :
    RecyclerView.Adapter<EmployeesAdapter.ViewHolder>() {

    class ViewHolder(val item: ItemEmployeeBinding) : RecyclerView.ViewHolder(item.root) {
        val optionsMenu = item.optionsMenu

        fun bind(employee: User?, listener: OnEmployeeClickListener) {
            item.user = employee
            optionsMenu.setOnClickListener {
                popMenu(it, employee?:User() , listener)
            }
            if (employee?.active == true){
                item.activeState.background = item.root.context.getDrawable(R.color.completed_task_color)
            }
            if (employee?.active == false){
                item.activeState.background = item.root.context.getDrawable(R.color.red)
            }
        }

        private fun popMenu(v: View, user: User , onOptionSelected: OnEmployeeClickListener) {
            val popupMenu = PopupMenu(v.context, v)
            popupMenu.inflate(R.menu.options_menu)
            val menu = popupMenu.menu
            if (user.active == true){
                menu.findItem(R.id.activeAccount).isVisible = false
            }
            if (user.active == false){
                menu.findItem(R.id.disableAccount).isVisible = false
            }

            popupMenu.setOnMenuItemClickListener { menuItem ->
                val menuItemId = menuItem.itemId
                val menuItemEnum = when (menuItemId) {
                    R.id.edit_points -> MenuItem.EDIT_POINTS
                    R.id.activeAccount -> MenuItem.ACTIVE_ACCOUNT
                    R.id.disableAccount -> MenuItem.DISABLE_ACCOUNT
                    else -> MenuItem.UNKNOWN
                }
                onOptionSelected.onOptionSelected(user, menuItemEnum)
                true
            }
            popupMenu.show()
            val popUp = PopupMenu::class.java.getDeclaredField("mPopup")
            popUp.isAccessible = true
            val menuPopHelper = popUp.get(popupMenu)
            menuPopHelper.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(menuPopHelper, true)
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
        onEmployeeClickListener?.let { holder.bind(employee, it) }
    }

    var onEmployeeClickListener: OnEmployeeClickListener? = null

    interface OnEmployeeClickListener {
        fun onEmployeeClick(employee: User?)
        fun onOptionSelected(user: User, menuItem: MenuItem)
    }

    fun submitList(list: List<User?>?) {
        employeesList = list as MutableList<User?>?
        notifyDataSetChanged()
    }
}
