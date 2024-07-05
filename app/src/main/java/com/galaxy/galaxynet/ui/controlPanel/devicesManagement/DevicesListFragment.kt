package com.galaxy.galaxynet.ui.controlPanel.devicesManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.galaxy.galaxynet.R
import com.galaxy.galaxynet.databinding.FragmentDevicesListBinding
import com.galaxy.galaxynet.model.DeviceType
import com.galaxy.util.Constants
import com.galaxy.util.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DevicesListFragment : Fragment() {
    lateinit var binding: FragmentDevicesListBinding
    private val viewModel: DevicesViewModel by activityViewModels()
    private val adapter = DeviceListAdapter(null)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDevicesListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initObservers()
    }

    private fun initObservers() {
        viewModel.devicesLivedata.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.uiState.observe(viewLifecycleOwner) {
            handleUISatate(it)
        }
    }

    private fun initViews() {
        viewModel.getDeviceStatistics()
        binding.devicesAdapter.adapter = adapter

        binding.updateBtn.setOnClickListener {
            viewModel.updateStatistics()
        }

        adapter.onDeviceClickListener = DeviceListAdapter.OnDeviceClickListener{ device: DeviceType ->
            // navigate to filtered ips fragment with device name
            val bundle = Bundle()
            bundle.putString(Constants.DEVICE_NAME_KEY, device.type)
            findNavController().navigate(R.id.action_devicesListFragment_to_filteredIPsFragment , bundle)
        }

        adapter.onDeviceLongClickListener = DeviceListAdapter.OnDeviceClickListener{ device: DeviceType ->
            val bundle = Bundle()
            bundle.putString(Constants.OLD_DEVICE_NAME_KEY, device.type)
            findNavController().navigate(R.id.action_devicesListFragment_to_editDeviceNameFragment2 , bundle)
        }

    }

    private fun handleUISatate(uiState: UiState) {
        when (uiState) {
            UiState.SUCCESS -> {
                binding.progressBar.visibility = View.GONE
            }

            UiState.LOADING -> {
                binding.progressBar.visibility = View.VISIBLE
            }

            UiState.ERROR -> {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(activity, "حدث خطأ حاول مره اخري", Toast.LENGTH_SHORT).show()
            }
        }
    }

}