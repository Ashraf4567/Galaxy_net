package com.galaxy.galaxynet.ui.controlPanel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.galaxy.galaxynet.databinding.FragmentDevicesListBinding
import com.galaxy.util.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DevicesListFragment : Fragment() {
    lateinit var binding: FragmentDevicesListBinding
    private val viewModel: ControlPanelViewModel by viewModels()
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