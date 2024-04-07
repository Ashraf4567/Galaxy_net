package com.galaxy.galaxynet.ui.controlPanel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.galaxy.galaxynet.databinding.FragmentAddDeviceTypeBinding
import com.galaxy.galaxynet.ui.tabs.ip.IpViewModel
import com.galaxy.util.UiState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddDeviceTypeFragment : BottomSheetDialogFragment() {
    lateinit var binding: FragmentAddDeviceTypeBinding
    private val viewModel: IpViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddDeviceTypeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initObservers()
    }

    private fun initObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            handleUISatate(it)
        }
        viewModel.messageLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(requireActivity(), it, Toast.LENGTH_LONG).show()
        }
    }

    private fun initViews() {
        binding.vm = viewModel
        binding.lifecycleOwner = this
        binding.addDeviceBtn.setOnClickListener {
            viewModel.addDeviceType()
        }

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }

    }

    private fun handleUISatate(uiState: UiState) {
        when (uiState) {
            UiState.SUCCESS -> {
                Toast.makeText(activity, "تم اضافه نوع الجهاز بنجاح", Toast.LENGTH_LONG).show()
                dismiss()
            }

            UiState.ERROR -> {
                Toast.makeText(activity, "حدث خطأ حاول مره اخري", Toast.LENGTH_SHORT).show()
            }

            else -> {}
        }
    }
}