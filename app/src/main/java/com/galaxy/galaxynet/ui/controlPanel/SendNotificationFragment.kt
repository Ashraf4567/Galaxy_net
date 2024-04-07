package com.galaxy.galaxynet.ui.controlPanel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.galaxy.galaxynet.databinding.FragmentSendNotificationBinding
import com.galaxy.util.UiState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SendNotificationFragment : BottomSheetDialogFragment() {
    lateinit var binding: FragmentSendNotificationBinding
    private val viewModel: ControlPanelViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSendNotificationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        viewModel.uiState.observe(viewLifecycleOwner) {
            handleState(it)
        }
    }

    private fun initViews() {
        binding.vm = viewModel
        binding.lifecycleOwner = this

        binding.sendBtn.setOnClickListener {
            viewModel.sendAlert()
        }
        binding.cancelBtn.setOnClickListener {
            dismiss()
        }


    }

    private fun handleState(state: UiState?) {
        when (state) {
            UiState.SUCCESS -> {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(activity, "تم ارسال الاشعار بنجاح", Toast.LENGTH_SHORT).show()
            }

            UiState.LOADING -> {
                binding.progressBar.visibility = View.VISIBLE
            }

            UiState.ERROR -> {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(activity, "حدث خطا..حاول مره اخري", Toast.LENGTH_SHORT).show()
            }

            else -> {

            }
        }
    }
}