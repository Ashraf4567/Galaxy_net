package com.galaxy.galaxynet.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.galaxy.galaxynet.databinding.FragmentAddAccountBinding
import com.galaxy.galaxynet.ui.auth.AuthViewModel
import com.galaxy.util.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class AddAccountFragment : Fragment() {
    lateinit var binding: FragmentAddAccountBinding
    private val viewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddAccountBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        binding.addAccountBtn.setOnClickListener {
            lifecycleScope.launch {
                viewModel.addAccount()
                withContext(Dispatchers.Main) {
                    binding.addAccountBtn.isEnabled = false
                }
            }
        }

        initObserevers()
    }

    private fun initObserevers() {
        viewModel.state.observe(viewLifecycleOwner) {
            handleStete(it)
        }
    }

    private fun handleError() {
        Toast.makeText(activity, "حدث خطا حاول مره اخري ", Toast.LENGTH_LONG).show()
        binding.addAccountBtn.isEnabled = true
    }

    private fun handleSuccess() {
        Toast.makeText(
            activity,
            "تم اضافه الحساب بنجاج برجاء ارسال البيانات للموظف ",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun handleStete(state: UiState?) {
        when (state) {
            UiState.ERROR -> handleError()
            UiState.SUCCESS -> handleSuccess()
            else -> {}
        }

    }

}