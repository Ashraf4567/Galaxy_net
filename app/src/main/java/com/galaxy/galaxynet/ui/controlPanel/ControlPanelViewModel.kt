package com.galaxy.galaxynet.ui.controlPanel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galaxy.galaxynet.data.TransactionRepo
import com.galaxy.galaxynet.data.ip.ipRepo.IpRepository
import com.galaxy.galaxynet.data.usersRepo.UsersRepository
import com.galaxy.galaxynet.model.DeviceType
import com.galaxy.galaxynet.model.Ip
import com.galaxy.galaxynet.model.TransactionHistory
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
    private val transactionRepository: TransactionRepo
) : ViewModel() {




    val uiState = SingleLiveEvent<UiState>()

    val notificationTitle = MutableLiveData<String>()
    val notificationTitleError = MutableLiveData<String?>()
    val notificationMessage = MutableLiveData<String?>()
    val notificationMessageError = MutableLiveData<String?>()

    val transactionList = MutableLiveData<List<TransactionHistory>>()




    fun getAllTransactions() {
        uiState.postValue(UiState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = transactionRepository.getAllTransactions()
                Log.d("getAllTransactions", result.toString())
                transactionList.postValue(result)
                uiState.postValue(UiState.SUCCESS)
            } catch (e: Exception) {
                Log.d("error getAllTransactions", e.message.toString())
                uiState.postValue(UiState.ERROR)
            }
        }
    }

    fun getTransactionsByUserId(userId: String){
        uiState.postValue(UiState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = transactionRepository.getTransactionsById(userId)
                transactionList.postValue(result)
                uiState.postValue(UiState.SUCCESS)


            }catch (e: Exception){
                Log.e("test get transactions by user id", e.message.toString())
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