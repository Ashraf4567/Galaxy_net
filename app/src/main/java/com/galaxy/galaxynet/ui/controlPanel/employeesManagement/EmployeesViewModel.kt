package com.galaxy.galaxynet.ui.controlPanel.employeesManagement

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galaxy.galaxynet.data.usersRepo.UsersRepository
import com.galaxy.galaxynet.model.User
import com.galaxy.util.SingleLiveEvent
import com.galaxy.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeesViewModel @Inject constructor(
    private val usersRepository: UsersRepository
): ViewModel() {

    val uIstate = MutableLiveData<UiState>()
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
                val res = usersRepository.deleteUser(id)
                if (res.isSuccess){
                    uIstate.postValue(UiState.SUCCESS)
                    messageLiveData.postValue("تم حذف الموظف بنجاح")
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