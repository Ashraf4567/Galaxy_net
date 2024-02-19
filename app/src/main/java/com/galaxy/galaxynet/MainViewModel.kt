package com.galaxy.galaxynet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.galaxy.galaxynet.data.local.SessionManager
import com.galaxy.galaxynet.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    val isManager = MutableLiveData<Boolean>()


    fun checkUserType() {
        if (sessionManager.getUserData()?.type.equals(User.MANAGER)) {
            isManager.postValue(true)
        } else {
            isManager.postValue(false)
        }
    }

}