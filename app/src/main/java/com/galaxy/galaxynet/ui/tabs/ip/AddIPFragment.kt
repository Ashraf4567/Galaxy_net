package com.galaxy.galaxynet.ui.tabs.ip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.galaxy.galaxynet.databinding.FragmentAddIPBinding
import com.galaxy.util.Constants
import com.galaxy.util.UiState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddIPFragment : BottomSheetDialogFragment() {
    lateinit var binding: FragmentAddIPBinding
    private val viewModel: IpViewModel by viewModels()
    var deviceTypes = listOf<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddIPBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.gatAllDevicesTypes()
        val parentIP = arguments?.getString(Constants.PARENT_IP)
        binding.parentIpText.text =
            if (!parentIP.isNullOrBlank()) " متفرع من  $parentIP" else "IP رئيسي"
        viewModel.parentIp = parentIP
        initObservers()
        initViews()

    }

    private fun initObservers() {
        viewModel.types.observe(viewLifecycleOwner) { types ->
            if (types.isEmpty()) {
                // Handle empty list case (e.g., display a message)
            } else {
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    types
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.ipDeviceType.adapter = adapter // Set the adapter only once
                binding.ipDeviceType.setSelection(1) // Set the initial selection if desired
            }
        }

        viewModel.uiState.observe(viewLifecycleOwner) {
            handleUISatate(it)
        }
        viewModel.messageLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
        }
    }

    private fun initViews() {
        binding.vm = viewModel
        binding.lifecycleOwner = this
        binding.saveIpBtn.setOnClickListener {
            viewModel.addIp()
        }
        binding.cancelBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun handleUISatate(uiState: UiState) {
        when (uiState) {
            UiState.SUCCESS -> {
                Toast.makeText(activity, "تم اضافه ال IP بنجاح", Toast.LENGTH_LONG).show()
                binding.progressBar.visibility = View.GONE
                dismiss()
            }

            UiState.ERROR -> {
                Toast.makeText(activity, "حدث خطأ حاول مره اخري", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                binding.saveIpBtn.isEnabled = true
            }

            UiState.LOADING -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.saveIpBtn.isEnabled = false
            }
        }
    }

}