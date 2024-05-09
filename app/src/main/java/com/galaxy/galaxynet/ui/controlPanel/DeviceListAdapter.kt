package com.galaxy.galaxynet.ui.controlPanel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.galaxy.galaxynet.databinding.ItemDeviceBinding
import com.galaxy.galaxynet.model.DeviceType


class DeviceListAdapter(var devicesList: List<DeviceType?>?) :
    RecyclerView.Adapter<DeviceListAdapter.ViewHolder>() {

    class ViewHolder(val item: ItemDeviceBinding) : RecyclerView.ViewHolder(item.root) {
        fun bind(device: DeviceType) {
            item.device = device
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = ItemDeviceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(item)
    }

    override fun getItemCount(): Int = devicesList?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ip = devicesList?.get(position)
        holder.bind(ip!!)

        holder.item.root.setOnClickListener {
            onDeviceClickListener?.onClick(ip)
        }
    }

    var onDeviceClickListener: OnDeviceClickListener? = null
    fun interface OnDeviceClickListener {
        fun onClick(device: DeviceType)
    }


    fun submitList(list: List<DeviceType>) {
        devicesList = list
        notifyDataSetChanged()
    }

}