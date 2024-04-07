package com.galaxy.galaxynet.ui.tabs.ip

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.galaxy.galaxynet.databinding.ActivityEditIpactivityBinding
import com.galaxy.util.Constants
import com.galaxy.util.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditIPActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditIpactivityBinding
    private val viewModel: IpViewModel by viewModels()

    var iP_Id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditIpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.gatAllDevicesTypes()
        iP_Id = intent.getStringExtra(Constants.IP_ID).toString()
        Log.d("test send ID", iP_Id)
        initViews()
        initObservers()
    }

    private fun initViews() {
        viewModel.getIp(iP_Id)
        binding.vm = viewModel

        binding.lifecycleOwner = this

        binding.editIpBtn.setOnClickListener {
            viewModel.updateId(iP_Id)
        }
        binding.cancelBtn.setOnClickListener {
            finish()
        }

        if (iP_Id.isNotBlank()) {

            viewModel.getIp(iP_Id)
        }
    }

    private fun initObservers() {
        viewModel.uiState.observe(this) {
            handleUISatate(it)
        }

        viewModel.types.observe(this) { types ->
            if (types.isEmpty()) {
                // Handle empty list case (e.g., display a message)
            } else {
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    types
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.ipDeviceType.adapter = adapter // Set the adapter only once
                binding.ipDeviceType.setSelection(1) // Set the initial selection if desired
            }

            viewModel.ipLivedata.observe(this) { ip ->
                val categoryPosition =
                    (binding?.ipDeviceType?.adapter as? ArrayAdapter<String>)?.getPosition(ip.deviceType)
                        ?: -1

                if (categoryPosition >= 0) {
                    binding?.ipDeviceType?.setSelection(categoryPosition)
                }
                if (!ip.deviceType.isNullOrBlank()) {
                    viewModel.oldDeviceName = ip.deviceType
                }
                binding.progressBar.visibility = View.GONE
            }
            viewModel.messageLiveData.observe(this) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }


        }
    }

    private fun handleUISatate(uiState: UiState) {
        when (uiState) {
            UiState.SUCCESS -> {
                binding.progressBar.visibility = View.GONE
                binding.cancelBtn.text = "خروج"
            }

            UiState.LOADING -> {
                binding.progressBar.visibility = View.VISIBLE
            }

            UiState.ERROR -> {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "حدث خطأ حاول مره اخري", Toast.LENGTH_SHORT).show()
            }
        }
    }
}