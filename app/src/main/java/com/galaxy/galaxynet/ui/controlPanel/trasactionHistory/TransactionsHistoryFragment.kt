package com.galaxy.galaxynet.ui.controlPanel.trasactionHistory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.galaxy.galaxynet.databinding.FragmentTransactionsHistoryBinding
import com.galaxy.galaxynet.ui.controlPanel.ControlPanelViewModel
import com.galaxy.util.Constants.Companion.EMPLOYEE_NAME_KEY
import com.galaxy.util.Constants.Companion.USER_ID_KEY
import com.galaxy.util.UiState
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
        val userId = arguments?.getString(USER_ID_KEY)
        val employeeName = arguments?.getString(EMPLOYEE_NAME_KEY)
        iniViews(userId , employeeName)
        initObservers()
    }

    private fun initObservers() {
        viewModel.transactionList.observe(viewLifecycleOwner) {
            if (it.isEmpty()){
                binding.emptyList.visibility = View.VISIBLE
            }else{
                adapter.submitList(it)
                binding.emptyList.visibility = View.GONE
            }

        }
        viewModel.uiState.observe(viewLifecycleOwner){
            handleState(it)
        }
    }

    private fun iniViews(userId: String?, employeeName: String?) {
        binding.transactionHistoryRv.adapter = adapter
        if (userId != null){
            binding.title.textSize = 15f
            binding.title.text = "(سجل نقاط  $employeeName)"
            viewModel.getTransactionsByUserId(userId)
        }else{
            viewModel.getAllTransactions()
        }

    }

    private fun handleState(state: UiState?) {
        when (state) {
            UiState.SUCCESS -> {
                binding.progressBar.visibility = View.GONE
            }

            UiState.LOADING -> {
                binding.progressBar.visibility = View.VISIBLE
            }

            UiState.ERROR -> {
                binding.progressBar.visibility = View.GONE
            }

            else -> {

            }
        }
    }

}