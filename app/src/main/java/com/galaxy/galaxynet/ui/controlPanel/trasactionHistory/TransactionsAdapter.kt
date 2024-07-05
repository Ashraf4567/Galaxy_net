package com.galaxy.galaxynet.ui.controlPanel.trasactionHistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.galaxy.galaxynet.databinding.ItemTransactionBinding
import com.galaxy.galaxynet.model.TransactionHistory

class TransactionsAdapter(private var transactions: List<TransactionHistory>):
    RecyclerView.Adapter<TransactionsAdapter.ViewHolder>() {

    class ViewHolder(val item: ItemTransactionBinding): RecyclerView.ViewHolder(item.root) {
        val transactionType = item.transaction
        fun bind(transaction: TransactionHistory) {
            item.transaction = transaction
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = transactions.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.bind(transaction)
    }

    fun submitList(list: List<TransactionHistory>) {
        transactions = list
        notifyDataSetChanged()
    }

}