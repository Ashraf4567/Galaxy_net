package com.galaxy.galaxynet.ui.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galaxy.galaxynet.R
import com.galaxy.galaxynet.data.local.SessionManager
import com.galaxy.galaxynet.data.usersRepo.UsersRepository
import com.galaxy.galaxynet.model.Token
import com.galaxy.galaxynet.model.User
import com.galaxy.util.SingleLiveEvent
import com.galaxy.util.UiState
import com.galaxy.util.UserResult
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    val sessionManager: SessionManager,
    val auth: FirebaseAuth,
    val usersRepository: UsersRepository
) : ViewModel() {

    val userName = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val passwordConfirmation = MutableLiveData<String>()
    var userType: String = ""

    val emailError = MutableLiveData<String?>()
    val passwordError = MutableLiveData<String?>()
    val userNameError = MutableLiveData<String?>()
    val passwordConfirmationError = MutableLiveData<String?>()

    val userLiveData = MutableLiveData<User?>()
    val state = SingleLiveEvent<UiState>()
    val isManager = MutableLiveData<Boolean>()
    val uiState = SingleLiveEvent<UiState>()

    fun checkUserType() {
        if (sessionManager.getUserData()?.type.equals(User.MANAGER)) {
            isManager.postValue(true)
        } else {
            isManager.postValue(false)
        }
    }


    fun onRadioGroupChanged(checkedId: Int) {
        when (checkedId) {
            R.id.employee -> userType = User.EMPLOYEE
            R.id.manager -> userType = User.MANAGER
        }
    }

    var isLoading = MutableLiveData<Boolean>()


    suspend fun logIn() {


        try {
            isLoading.postValue(true)
            if (validForm()) {
                val authResult = withContext(Dispatchers.IO) {
                    auth.signInWithEmailAndPassword(email.value!!, password.value!!).await()
                }

                // Check if authentication was successful
                if (authResult.user != null) {
                    val user = User(name = "Ashraf", email = email.value, id = authResult.user?.uid)
                    getUserData(user.id)

                } else {
                    state.postValue(UiState.ERROR)
                    // Handle authentication errors:
                }
            }
        } catch (e: Exception) {
            // Handle other non-authentication errors
            state.postValue(UiState.ERROR)
            e.printStackTrace()
        } finally {
            isLoading.postValue(false)
        }
    }

    fun addAccount() {
        if (!validAddAccountForm()) return
        // Start loading
        isLoading.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Perform the asynchronous operation, e.g., createUserWithEmailAndPassword
                val task = auth.createUserWithEmailAndPassword(email.value!!, password.value!!)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Handle successful account creation

                            val userId = task.result.user?.uid
                            val user = User(userId, userName.value, email.value, userType, 0)
                            Log.d("test save data", user.toString())
                            addUser(user)
                        } else {
                            // Handle failed account creation
                            state.postValue(UiState.ERROR)
                        }

                    }
                    .await() // Use await to suspend until the task completes
            } catch (e: Exception) {
                e.printStackTrace()
                state.postValue(UiState.ERROR)
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    suspend fun getUserData(id: String?) {
        uiState.postValue(UiState.LOADING)
        try {
            val result = usersRepository.getUserById(id)

            when (result) {
                is UserResult.Success -> {
                    val user = result.user
                    // Cache user data as session manager
                    userLiveData.postValue(user)
                    uiState.postValue(UiState.SUCCESS)
                    sessionManager.saveUserData(user)
                    checkUserType()
                    state.postValue(UiState.SUCCESS)
                }

                is UserResult.Failure -> {
                    Log.d("test save data", "Failed: ${result.exception.localizedMessage}")
                    uiState.postValue(UiState.ERROR)
                }

                else -> {}
            }
            Log.d("test save data", "End getUserData coroutine")
        } catch (e: CancellationException) {
            Log.d("test save data", "Coroutine canceled: ${e.localizedMessage}")
            e.printStackTrace() // Log the stack trace for debugging
        } catch (e: Exception) {
            Log.e("test save data", "Exception in getUserData coroutine: ${e.localizedMessage}")
        }

    }

    private fun addUser(user: User) = viewModelScope.launch {
        val result = usersRepository.insertUserToFirestore(user)
        if (result.isFailure) {
            state.postValue(UiState.ERROR)

        }
        if (result.isSuccess) {
            state.postValue(UiState.SUCCESS)
            Log.d("test save data", "saved in firestore" + result.isSuccess)
        }
    }

    fun saveToken(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    val userToken = Token(tokenValue = token)
                    val saveTokenResult = usersRepository.saveUserToken(userToken)
                    if (saveTokenResult.isFailure) {
                        Log.e("updateToken", "Error saving token: ${saveTokenResult.isFailure}")
                    } else {
                        Log.d("updateToken", "Token saved successfully")
                    }
                } else {
                    Log.w("updateToken", "Failed to get current user ID")
                }
            } catch (e: Exception) {
                Log.e("updateToken", "Error during token update: ${e.message}", e)
            }
        }
    }

    private fun validAddAccountForm(): Boolean {
        var isValid = true
        if (userName.value.isNullOrBlank()) {
            //showError
            userNameError.postValue("من فضلك ادخل الاسم")
            isValid = false
        } else {
            userNameError.postValue(null)
        }
        if (email.value.isNullOrBlank()) {
            //showError
            emailError.postValue("من فضلك ادخل البريد الالكتروني")
            isValid = false
        } else {
            emailError.postValue(null)
        }
        if (password.value.isNullOrBlank()) {
            //showError
            passwordError.postValue("من فضلك ادخل كلمه المرور")
            isValid = false
        } else {
            passwordError.postValue(null)
        }
        if (passwordConfirmation.value.isNullOrBlank() ||
            passwordConfirmation.value != password.value
        ) {
            //showError
            passwordConfirmationError.postValue("كلمات المرور غير متطابقه")
            isValid = false
        } else {
            passwordConfirmationError.postValue(null)
        }
        return isValid
    }

    private fun validForm(): Boolean {
        var isValid = true

        if (email.value.isNullOrBlank()) {
            //showError
            emailError.postValue("برجاء ادخال البريد الاليكتروني")
            isValid = false
        } else {
            emailError.postValue(null)
        }
        if (password.value.isNullOrBlank()) {
            //showError
            passwordError.postValue("ادخل كلمه المرور")
            isValid = false
        } else {
            passwordError.postValue(null)
        }

        return isValid
    }
}
