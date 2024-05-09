package com.galaxy.galaxynet.ui.controlPanel

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.galaxy.galaxynet.R
import com.galaxy.galaxynet.databinding.ItemEmployeeBinding
import com.galaxy.galaxynet.model.User


class EmployeesAdapter(var employeesList: MutableList<User?>?) :
    RecyclerView.Adapter<EmployeesAdapter.ViewHolder>() {

    class ViewHolder(val item: ItemEmployeeBinding) : RecyclerView.ViewHolder(item.root) {
        val optionsMenu = item.optionsMenu

        fun bind(employee: User?, listener: OnEmployeeClickListener) {
            item.user = employee
            optionsMenu.setOnClickListener {
                popMenu(it, employee?.id ?: "" , listener)
            }
        }

        private fun popMenu(v: View, userId: String , onOptionSelected: OnEmployeeClickListener) {
            val popupMenu = PopupMenu(v.context, v)
            popupMenu.inflate(R.menu.options_menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                val menuItemId = menuItem.itemId
                val menuItemEnum = when (menuItemId) {
                    R.id.deleteAccount -> MenuItem.DELETE
                    R.id.edit_points -> MenuItem.EDIT_POINTS
                    else -> MenuItem.UNKNOWN
                }
                onOptionSelected.onOptionSelected(userId, menuItemEnum)
                true
            }
            popupMenu.show()
            val popUp = PopupMenu::class.java.getDeclaredField("mPopup")
            popUp.isAccessible = true
            val menu = popUp.get(popupMenu)
            menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(menu, true)
        }

//        private fun onOptionSelected(userId: String, menuItem: MenuItem) {
//            // Callback to fragment with selected item information
//            Log.d("EmployeesAdapter", "Option selected: $menuItem")
//            (itemView.context as? AppCompatActivity)?.let { activity ->
//                val listener = activity as? OnEmployeeClickListener
//                listener?.onOptionSelected(userId, menuItem)
//            }
//        }
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
        fun onOptionSelected(userId: String, menuItem: MenuItem)
    }

    fun submitList(list: List<User?>?) {
        employeesList = list as MutableList<User?>?
        notifyDataSetChanged()
    }
}
