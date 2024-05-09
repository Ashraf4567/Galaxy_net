package com.galaxy.galaxynet.ui.controlPanel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galaxy.galaxynet.data.ipRepo.IpRepository
import com.galaxy.galaxynet.data.usersRepo.UsersRepository
import com.galaxy.galaxynet.model.DeviceType
import com.galaxy.galaxynet.model.Ip
import com.galaxy.galaxynet.model.User
import com.galaxy.galaxynet.notification.PushNotificationService
import com.galaxy.util.SingleLiveEvent
import com.galaxy.util.TransactionResult
import com.galaxy.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ControlPanelViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
    private val ipRepository: IpRepository
) : ViewModel() {

    val uIstate = MutableLiveData<UiState>()

    private val _ipList = MutableLiveData<MutableList<Ip?>>()
    val ipList: LiveData<MutableList<Ip?>> = _ipList

    val uiState = SingleLiveEvent<UiState>()
    val devicesLivedata = MutableLiveData<List<DeviceType>>()

    val notificationTitle = MutableLiveData<String>()
    val notificationTitleError = MutableLiveData<String?>()
    val notificationMessage = MutableLiveData<String?>()
    val notificationMessageError = MutableLiveData<String?>()

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
        viewModelScope.launch {
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

    fun sendAlert() {
        if (!validateSendNotificationForm()) return
        uiState.postValue(UiState.LOADING)
        viewModelScope.launch {
            try {
                val result = usersRepository.getAllTokens()
                result.forEach {
                    PushNotificationService.sendNotificationToDevice(
                        it.tokenValue!!,
                        notificationTitle.value!!,
                        notificationMessage.value!!
                    )
                }
                uiState.postValue(UiState.SUCCESS)
            } catch (e: Exception) {
                Log.d("error send Alert", e.message.toString())
                uiState.postValue(UiState.ERROR)
            }

        }
    }

    private fun validateSendNotificationForm(): Boolean {
        var isValid = true

        if (notificationTitle.value.isNullOrBlank()) {
            //showError
            notificationTitleError.postValue("برجاء ادخال عنوان التنبيه")
            isValid = false
        } else {
            notificationTitleError.postValue(null)
        }
        if (notificationMessage.value.isNullOrBlank()) {
            //showError
            notificationMessageError.postValue("برجاء ادخال رساله التنبيه")
            isValid = false
        } else {
            notificationMessageError.postValue(null)
        }

        return isValid
    }


}