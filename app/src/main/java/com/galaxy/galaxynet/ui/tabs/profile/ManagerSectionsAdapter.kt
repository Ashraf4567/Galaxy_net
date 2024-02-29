package com.galaxy.galaxynet.ui.tabs.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.galaxy.galaxynet.databinding.ItemManagerSectionBinding

class ManagerSectionsAdapter(var sectionsList: List<String>) :
    RecyclerView.Adapter<ManagerSectionsAdapter.ViewHolder>() {

    class ViewHolder(val item: ItemManagerSectionBinding) : RecyclerView.ViewHolder(item.root) {
        fun bind(itemSectionName: String) {
            item.name = itemSectionName

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = ItemManagerSectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(item)
    }

    override fun getItemCount(): Int = sectionsList.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemSectionName = sectionsList[position]
        holder.bind(itemSectionName)
        onSectionClickListener.let {
            holder.item.sectionName.setOnClickListener {
                onSectionClickListener?.onSectionClick(itemSectionName)
            }
        }
    }

    var onSectionClickListener: OnSectionClickListener? = null

    fun interface OnSectionClickListener {
        fun onSectionClick(sectionName: String)
    }

}