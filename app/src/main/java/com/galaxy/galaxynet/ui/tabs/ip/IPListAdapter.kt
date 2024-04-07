package com.galaxy.galaxynet.ui.tabs.ip

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.galaxy.galaxynet.databinding.ItemIpBinding
import com.galaxy.galaxynet.model.Ip


class IPListAdapter(var iPList: MutableList<Ip?>?) :
    RecyclerView.Adapter<IPListAdapter.ViewHolder>() {

    class ViewHolder(val item: ItemIpBinding) : RecyclerView.ViewHolder(item.root) {
        fun bind(ip: Ip) {
            item.ip = ip
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = ItemIpBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(item)
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