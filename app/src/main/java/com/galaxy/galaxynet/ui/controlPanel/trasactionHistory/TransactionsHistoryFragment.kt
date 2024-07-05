package com.galaxy.galaxynet.ui.controlPanel.trasactionHistory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.galaxy.galaxynet.R
import com.galaxy.galaxynet.databinding.FragmentTransactionsHistoryBinding
import com.galaxy.galaxynet.ui.controlPanel.ControlPanelViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionsHistoryFragment : Fragment() {

    private lateinit var binding: FragmentTransactionsHistoryBinding
    val adapter = TransactionsAdapter(emptyList())
    val viewModel: ControlPanelViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTransactionsHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iniViews()
        initObservers()
    }

    private fun initObservers() {
        viewModel.transactionList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun iniViews() {
        binding.transactionHistoryRv.adapter = adapter
        viewModel.getAllTransactions()
    }

}