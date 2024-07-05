package com.galaxy.galaxynet.ui.controlPanel.devicesManagement

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galaxy.galaxynet.data.ip.ipRepo.IpRepository
import com.galaxy.galaxynet.model.DeviceType
import com.galaxy.galaxynet.model.Ip
import com.galaxy.util.SingleLiveEvent
import com.galaxy.util.TransactionResult
import com.galaxy.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DevicesViewModel @Inject constructor(
    private val ipRepository: IpRepository
): ViewModel() {

    val devicesLivedata = MutableLiveData<List<DeviceType>>()

    val uiState = SingleLiveEvent<UiState>()
    val editNameUiState = SingleLiveEvent<UiState>()

    private val _ipList = MutableLiveData<MutableList<Ip?>>()
    val ipList: LiveData<MutableList<Ip?>> = _ipList

    val deviceType = MutableLiveData<String>()
    var isLoading = MutableLiveData<Boolean>()
    val messageLiveData = SingleLiveEvent<String>()


    fun getDeviceStatistics() {
        uiState.postValue(UiState.LOADING)
        viewModelScope.launch {
            try {
                val result = ipRepository.getAllDevices()
                devicesLivedata.postValue(result)
                uiState.postValue(UiState.SUCCESS)

            } catch (e: Exception) {
                Log.d("test All devices", e.message.toString())
                uiState.postValue(UiState.ERROR)
            }
        }
    }

    fun updateStatistics() {
        uiState.postValue(UiState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = ipRepository.updateDeviceTypeCounters()
                when (result) {
                    is TransactionResult.Failure -> {
                        uiState.postValue(UiState.ERROR)
                    }

                    is TransactionResult.Success -> {
                        getDeviceStatistics()
                    }
                }
            } catch (e: Exception) {
                uiState.postValue(UiState.ERROR)
            }
        }
    }

    fun changeDeviceName(oldName: String, newName: String){
        editNameUiState.postValue(UiState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val res = ipRepository.changeDeviceName(oldName, newName)
                when(res){
                    is TransactionResult.Failure -> {
                        editNameUiState.postValue(UiState.ERROR)
                        if (res.exception?.message.equals("Device type already exists")) {
                            messageLiveData.postValue("نوع الجهاز موجود بالفعل")
                        }
                        uiState.postValue(UiState.ERROR)
                    }
                    is TransactionResult.Success -> {
                        editNameUiState.postValue(UiState.SUCCESS)
                    }

                }

            }catch (e:Exception){
                Log.d("test changeDeviceName", e.message.toString())
                editNameUiState.postValue(UiState.ERROR)
            }
        }
    }

    fun getFilteredIPs(deviceName: String){
        uiState.postValue(UiState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result= ipRepository.getIpsByDevice(deviceName)
                _ipList.postValue(result.toMutableList())
                uiState.postValue(UiState.SUCCESS)
            }catch (e:Exception){
                Log.d("test getFilteredIPs", e.message.toString())
                uiState.postValue(UiState.ERROR)
            }
        }
    }

    fun addDeviceType() {
        isLoading.postValue(true)
        viewModelScope.launch {
            try {
                if (!deviceType.value.isNullOrBlank()) {
                    val result = ipRepository.addDeviceType(deviceType.value!!)
                    when (result) {
                        is TransactionResult.Failure -> {
                            uiState.postValue(UiState.ERROR)
                            if (result.exception?.message.equals("Device type already exists")) {
                                messageLiveData.postValue("نوع الجهاز موجود بالفعل")
                            }
                            uiState.postValue(UiState.ERROR)

                        }

                        is TransactionResult.Success -> {
                            uiState.postValue(UiState.SUCCESS)
                        }
                    }
                    return@launch
                }

            } catch (e: Exception) {
                uiState.postValue(UiState.ERROR)
                Log.e("test add device type", e.message.toString())
            } finally {
                isLoading.postValue(false)
            }
        }
    }
}