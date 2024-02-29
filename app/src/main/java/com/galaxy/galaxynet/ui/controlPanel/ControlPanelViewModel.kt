package com.galaxy.galaxynet.ui.controlPanel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galaxy.galaxynet.data.usersRepo.UsersRepository
import com.galaxy.galaxynet.model.User
import com.galaxy.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ControlPanelViewModel @Inject constructor(
    val usersRepository: UsersRepository
) : ViewModel() {

    val uIstate = MutableLiveData<UiState>()
    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    fun getAllEmployees() {
        uIstate.postValue(UiState.LOADING)
        viewModelScope.launch {
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
}