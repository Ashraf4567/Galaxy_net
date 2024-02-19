package com.galaxy.galaxynet.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.galaxy.galaxynet.R
import com.galaxy.galaxynet.databinding.FragmentLoginBinding
import com.galaxy.util.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    val viewModel: AuthViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        binding.loginBtn.setOnClickListener {
            lifecycleScope.launch {
                viewModel.logIn()
            }
            binding.loginBtn.isEnabled = false
        }

        observeData()
    }

    private fun observeData() {


        viewModel.state.observe(viewLifecycleOwner) {
            handleState(it)
        }
    }

    private fun handleState(state: UiState) {
        when (state) {
            UiState.ERROR -> {
                Toast.makeText(activity, "حدث خطا حاول مره اخري ", Toast.LENGTH_LONG).show()
                binding.loginBtn.isEnabled = true
            }

            UiState.SUCCESS -> {
                Toast.makeText(activity, "تم تسجيل الدخول بنجاح", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            }

            else -> {}
        }
    }


}