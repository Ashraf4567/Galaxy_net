package com.galaxy.galaxynet.ui.tabs.ip

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.galaxy.galaxynet.databinding.ItemIpBinding
import com.galaxy.galaxynet.model.Ip


class IPListAdapter(var iPList: MutableList<Ip?>?, private val controllersVisibility :Boolean = true) :
    RecyclerView.Adapter<IPListAdapter.ViewHolder>() {

    class ViewHolder(val item: ItemIpBinding, private val controllersVisibility: Boolean) : RecyclerView.ViewHolder(item.root) {
        fun bind(ip: Ip) {
            item.ip = ip

            if (!controllersVisibility){
                item.icDelete.visibility = View.GONE
                item.icEditIp.visibility = View.GONE
                item.icOpenInBrowser.visibility = View.GONE
                item.icAddIpInList.visibility = View.GONE
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = ItemIpBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(item , controllersVisibility)
    }

    override fun getItemCount(): Int = iPList?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ip = iPList?.get(position)
        holder.bind(ip!!)
        onAddIPInListClickListener.let {
            holder.item.icAddIpInList.setOnClickListener {
                onAddIPInListClickListener?.onIpClick(ip, position)
            }
        }
        onIpClickListener.let {
            holder.item.root.setOnClickListener {
                onIpClickListener?.onIpClick(ip, position)
            }
        }
        onOpenInBrowserClickListener.let {
            holder.item.icOpenInBrowser.setOnClickListener {
                onOpenInBrowserClickListener?.onIpClick(ip, position)
            }
        }
        onDeleteIpClickListener.let {
            holder.item.icDelete.setOnClickListener {
                onDeleteIpClickListener?.onIpClick(ip, position)
            }
        }

        onEditIpClickListener.let {
            holder.item.icEditIp.setOnClickListener {
                onEditIpClickListener?.onIpClick(ip, position)
            }
        }
    }

    var onAddIPInListClickListener: OnIpClickListener? = null
    var onOpenInBrowserClickListener: OnIpClickListener? = null
    var onIpClickListener: OnIpClickListener? = null
    var onEditIpClickListener: OnIpClickListener? = null
    var onDeleteIpClickListener: OnIpClickListener? = null


    fun interface OnIpClickListener {
        fun onIpClick(ip: Ip, position: Int)
    }

    fun submitList(list: MutableList<Ip?>?) {
        iPList = list
        notifyDataSetChanged()
    }

}