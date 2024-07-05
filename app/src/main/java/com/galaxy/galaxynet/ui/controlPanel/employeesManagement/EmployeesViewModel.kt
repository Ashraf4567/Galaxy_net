package com.galaxy.galaxynet.ui.controlPanel.employeesManagement

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galaxy.galaxynet.data.TransactionRepo
import com.galaxy.galaxynet.data.usersRepo.UsersRepository
import com.galaxy.galaxynet.model.TransactionHistory
import com.galaxy.galaxynet.model.User
import com.galaxy.util.SingleLiveEvent
import com.galaxy.util.TransactionResult
import com.galaxy.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeesViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
    private val transactionRepository: TransactionRepo
): ViewModel() {

    val uIstate = SingleLiveEvent<UiState>()
    val editPointsUiState = SingleLiveEvent<UiState>()
    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    val messageLiveData = SingleLiveEvent<String>()



    fun getAllEmployees() {
        uIstate.postValue(UiState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = usersRepository.getAllUsers()
                _users.postValue(result)
                uIstate.postValue(UiState.SUCCESS)
            } catch (e: Exception) {
                Log.e("test get all users", e.message.toString())
                uIstate.postValue(UiState.ERROR)
            }
        }
    }

    fun deleteUser(id: String){
        uIstate.postValue(UiState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val res = usersRepository.disableUser(id)
                if (res.isSuccess){
                    uIstate.postValue(UiState.SUCCESS)
                    messageLiveData.postValue("تم ايقاف حساب الموظف بنجاح")
                }
                if (res.isFailure){
                    uIstate.postValue(UiState.ERROR)
                    messageLiveData.postValue("حدث خطأ .. حاول مره اخري")
                }

            }catch (e: Exception){
                Log.e("test delete user", e.message.toString())
                uIstate.postValue(UiState.ERROR)
            }
        }
    }

    fun editPoints(userId: String , points: Int , transactionType: String , transactionNotes: String , employeeName : String){
        editPointsUiState.postValue(UiState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val res = usersRepository.updateUserPoints(userId, points)
                if (res.isSuccess){
                    insertTransaction(points, transactionType, transactionNotes, employeeName)

                }
                if (res.isFailure){
                    editPointsUiState.postValue(UiState.ERROR)
                    messageLiveData.postValue("لايمكن ان تكون النقاط سالب")
                }
            }catch (e: Exception){
                Log.e("test edit points", e.message.toString())
                if (e.message.equals("Points cannot be negative")){
                    messageLiveData.postValue("لايمكن ان تكون النقاط سالب")
                }
                editPointsUiState.postValue(UiState.ERROR)
            }
        }
    }

    private fun insertTransaction(
        points: Int,
        transactionType: String,
        transactionNotes: String,
        employeeName: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val res2 = transactionRepository.addTransaction(
                TransactionHistory(
                    points = points,
                    transactionNotes = transactionNotes,
                    transactionType = transactionType,
                    employeeName = employeeName
                )
            )
            when(res2){
                is TransactionResult.Failure -> {
                    uIstate.postValue(UiState.ERROR)
                    messageLiveData.postValue("حدث خطأ .. حاول مره اخري")
                }
                is TransactionResult.Success ->{
                    editPointsUiState.postValue(UiState.SUCCESS)
                    messageLiveData.postValue("تم تعديل النقاط بنجاح")
                }
            }
        }
    }

    fun activeAccount(id: String) {
        uIstate.postValue(UiState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val res = usersRepository.activeUser(id)
                if (res.isSuccess){
                    uIstate.postValue(UiState.SUCCESS)
                    messageLiveData.postValue("تم تفعيل حساب الموظف بنجاح")
                }
                if (res.isFailure){
                    uIstate.postValue(UiState.ERROR)
                    messageLiveData.postValue("حدث خطأ .. حاول مره اخري")
                }

            }catch (e: Exception){
                Log.e("test delete user", e.message.toString())
                uIstate.postValue(UiState.ERROR)
            }
        }
    }
}