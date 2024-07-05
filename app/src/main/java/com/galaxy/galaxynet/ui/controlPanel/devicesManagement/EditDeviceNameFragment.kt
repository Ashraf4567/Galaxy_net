package com.galaxy.galaxynet.ui.controlPanel.devicesManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.galaxy.galaxynet.databinding.FragmentEditDeviceNameBinding
import com.galaxy.util.Constants
import com.galaxy.util.UiState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditDeviceNameFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentEditDeviceNameBinding
    private val viewModel: DevicesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditDeviceNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addDeviceBtn.setOnClickListener {
            val oldName = arguments?.getString(Constants.OLD_DEVICE_NAME_KEY)
            val newName = binding.deviceName.text.toString()
            viewModel.changeDeviceName(oldName = oldName?:"" , newName = newName)
        }
        binding.cancelBtn.setOnClickListener {
            dismiss()
        }
        initObservers()
    }

    private fun initObservers() {
        viewModel.editNameUiState.observe(viewLifecycleOwner){
            handleUiState(it)
        }
        viewModel.messageLiveData.observe(viewLifecycleOwner){
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleUiState(uiState: UiState) {
        when (uiState) {
            UiState.SUCCESS -> {
                Toast.makeText(activity, "تم تغيير اسم الجهاز بنجاح", Toast.LENGTH_LONG).show()
                viewModel.getDeviceStatistics()
                dismiss()
            }

            UiState.ERROR -> {
//                Toast.makeText(activity, "حدث خطأ حاول مره اخري", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }

            UiState.LOADING -> {
                binding.progressBar.visibility = View.VISIBLE
            }
        }
    }


}