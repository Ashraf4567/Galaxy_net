package com.galaxy.galaxynet.ui.tabs.ip

import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galaxy.galaxynet.data.ipRepo.IpRepository
import com.galaxy.galaxynet.data.local.SessionManager
import com.galaxy.galaxynet.model.Ip
import com.galaxy.util.SingleLiveEvent
import com.galaxy.util.TransactionResult
import com.galaxy.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class IpViewModel @Inject constructor(
    val ipsRepository: IpRepository,
    val sessionManager: SessionManager
) : ViewModel() {

    val deviceType = MutableLiveData<String>()
    var isLoading = MutableLiveData<Boolean>()
    val uiState = SingleLiveEvent<UiState>()
    val types = MutableLiveData<List<String>>()
    val messageLiveData = SingleLiveEvent<String>()

    val ipValue = MutableLiveData<String>()
    val ipValueError = MutableLiveData<String?>()
    val ipDescription = MutableLiveData<String>()
    val ipDescriptionError = MutableLiveData<String?>()
    val ipList = MutableLiveData<MutableList<Ip?>?>()
    val ipKeyWord = MutableLiveData<String?>()
    val ipKeyWordError = MutableLiveData<String?>()

    val fullIpList = MutableLiveData<List<Ip>>()


    var ipDeviceType = ""
    var parentIp: String? = null
    var parentIPLiveData = MutableLiveData<String?>()
    var oldDeviceName = ""

    val ipLivedata = MutableLiveData<Ip>()

    val clicksListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            ipDeviceType = parent?.getItemAtPosition(position) as String
        }
    }

    init {
        getFullIpList()
    }

    fun addIp() {

        if (validForm()) {
            uiState.postValue(UiState.LOADING)
            val ip = Ip(
                value = ipValue.value,
                description = ipDescription.value,
                ownerName = sessionManager.getUserData()?.name,
                deviceType = ipDeviceType,
                parentIp = parentIp,
                keyword = ipKeyWord.value,
                id = UUID.randomUUID().toString()
            )
            viewModelScope.launch {

                try {
                    val result = ipsRepository.addIp(ip)
                    when (result) {
                        is TransactionResult.Success -> {
                            uiState.postValue(UiState.SUCCESS)
                            Log.d("add offline", "success")
                        }

                        is TransactionResult.Failure -> {
                            if (result.exception?.message.equals("ip already exists"))
                                messageLiveData.postValue("هذا ال ip موجود بالفعل")
                            Log.d("add offline", "fail" + result.exception?.message.toString())
                            uiState.postValue(UiState.ERROR)
                        }
                    }

                } catch (e: Exception) {
                    Log.d("add offline", "exception" + e.message.toString())
                    uiState.postValue(UiState.ERROR)
                }
            }
        }
    }

    fun addDeviceType() {
        isLoading.postValue(true)
        viewModelScope.launch {
            try {
                if (!deviceType.value.isNullOrBlank()) {
                    val result = ipsRepository.addDeviceType(deviceType.value!!)
                    when (result) {
                        is TransactionResult.Failure -> {
                            uiState.postValue(UiState.ERROR)
                            if (result.exception?.message.equals("Device type already exists")) {
                                messageLiveData.postValue("نوع الجهاز موجود بالفعل")
                            }
                            uiState.postValue(UiState.ERROR)

                            Log.e("test add device type", result.exception?.message ?: "")
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

    fun gatAllDevicesTypes() {
        viewModelScope.launch {
            try {
                val result = ipsRepository.getAllDevicesTypes()
                types.postValue(result)
                Log.e("test get all types", result.size.toString())

            } catch (e: Exception) {
                Log.e("test get all types", e.message.toString())
            }
        }
    }

    private fun validForm(): Boolean {
        var isValid = true

        if (ipValue.value.isNullOrBlank()) {
            //showError
            ipValueError.postValue("برجاء ادخال ال IP")
            isValid = false
        } else {
            ipValueError.postValue(null)
        }
        if (ipKeyWord.value.isNullOrBlank()) {
            //showError
            ipValueError.postValue("برجاء ادخال الكلمه المفتاحيه")
            isValid = false
        } else {
            ipValueError.postValue(null)
        }
        if (ipDescription.value.isNullOrBlank()) {
            //showError
            ipDescriptionError.postValue("ادخل وصف ال IP")
            isValid = false
        } else {
            ipDescriptionError.postValue(null)
        }

        return isValid
    }

    fun getMainIPList() {
        uiState.postValue(UiState.LOADING)
        viewModelScope.launch {
            try {
                val result = ipsRepository.getMainIPs()
                ipList.postValue(result.toMutableList())
                uiState.postValue(UiState.SUCCESS)
            } catch (e: Exception) {
                uiState.postValue(UiState.ERROR)
                Log.e("test get main ip list", e.message.toString())
            }
        }


    }

    fun getSubIpList(parentIP: String) {
        uiState.postValue(UiState.LOADING)
        viewModelScope.launch {
            try {
                val result = ipsRepository.getSubListIPs(parentIP)
                ipList.postValue(result.toMutableList())
                uiState.postValue(UiState.SUCCESS)
            } catch (e: Exception) {
                uiState.postValue(UiState.ERROR)
                Log.e("test get sub ip list", e.message.toString())
            }

        }

    }

    fun getIp(id: String) {
        uiState.postValue(UiState.LOADING)
        viewModelScope.launch {
            try {
                val result = ipsRepository.getSpecificIp(id)
                ipLivedata.postValue(result)
                parentIPLiveData.postValue(result.parentIp)
                ipValue.postValue(result.value!!)
                ipKeyWord.postValue(result.keyword)
                ipDescription.postValue(result.description!!)
            } catch (e: Exception) {
                uiState.postValue(UiState.ERROR)
                Log.e("test get specific ip", e.message.toString())
            }
        }
    }


    private fun getFullIpList() {
        viewModelScope.launch {
            try {
                val result = ipsRepository.getAllIps()
                fullIpList.postValue(result)
            } catch (e: Exception) {
                Log.e("test get all ip list", e.message.toString())
            }
        }
    }

    fun searchLocally(query: String) {
        uiState.postValue(UiState.LOADING)
        viewModelScope.launch {
            try {
                val result = filterIpListLocally(query)
                ipList.postValue(result)
                uiState.postValue(UiState.SUCCESS)
            } catch (e: Exception) {
                uiState.postValue(UiState.ERROR)
                Log.e("Local search", "Error performing local search: $e")
            }
        }
    }

    private suspend fun filterIpListLocally(query: String): MutableList<Ip?>? {
        val filteredList = fullIpList.value?.filter { ip ->
            // Customize this condition based on how you want to perform the search
            ip.value?.contains(query, ignoreCase = true) == true ||
                    ip.description?.contains(query, ignoreCase = true) == true ||
                    ip.keyword?.contains(query, ignoreCase = true) == true
        }
        return filteredList?.toMutableList()
    }



    fun deleteIp(ip: Ip, value: String) {
        uiState.postValue(UiState.LOADING)
        viewModelScope.launch {
            try {
                val result = ipsRepository.deleteIp(ip, value)
                when (result) {
                    is TransactionResult.Success -> {
                        uiState.postValue(UiState.SUCCESS)
                        messageLiveData.postValue("تم حذف الip بنجاح")
                    }

                    is TransactionResult.Failure -> {

                        uiState.postValue(UiState.ERROR)
                        Log.d("test delete ip", result.exception?.message.toString())
                    }
                }

            } catch (e: Exception) {
                uiState.postValue(UiState.ERROR)
                Log.d("test delete ip", e.message.toString())
            }
        }
    }

    fun updateId(id: String) {
        val ip = Ip(
            value = ipValue.value,
            description = ipDescription.value,
            ownerName = sessionManager.getUserData()?.name,
            deviceType = ipDeviceType,
            keyword = ipKeyWord.value,
            parentIp = if (parentIPLiveData.value.isNullOrBlank()) null else parentIPLiveData.value
        )
        uiState.postValue(UiState.LOADING)
        viewModelScope.launch {
            try {
                val result = ipsRepository.updateIP(id, ip, oldDeviceName)
                when (result) {
                    is TransactionResult.Success -> {
                        messageLiveData.postValue("تم التعديل علي الIP بنجاح")
                        uiState.postValue(UiState.SUCCESS)
                        Log.d("test Update task", "task updated successfully")
                    }

                    is TransactionResult.Failure -> {
                        if (result.exception?.message.equals("ip already exists"))
                            messageLiveData.postValue("هذا ال ip موجود بالفعل")
                        uiState.postValue(UiState.ERROR)
                        Log.d(
                            "test Update task",
                            result.exception?.message ?: "something went wrong"
                        )
                    }
                }

            } catch (e: Exception) {

            }
        }
    }
}