package com.galaxy.galaxynet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.galaxy.util.SingleLiveEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import kotlinx.coroutines.tasks.await

class AuthViewModel(): ViewModel() {

    val email = MutableLiveData<String>("admin1@gmail.com")
    val password = MutableLiveData<String>("admin1122")

    val emailError = MutableLiveData<String?>()
    val passwordError = MutableLiveData<String?>()

    val messageLiveData = SingleLiveEvent<String>()

    var isLoading = MutableLiveData<Boolean>()
    val auth = FirebaseAuth.getInstance()

    suspend fun logIn(){
        isLoading.postValue(true)
        try {
            if (validForm()) {

                val user = auth.signInWithEmailAndPassword(email.value!!, password.value!!).await()
                PhoneAuthOptions.newBuilder(auth).setPhoneNumber("")

                messageLiveData.postValue("تم تسجيل الدخول بنجاح")
            }
        } catch (e: Exception) {
            // Handle authentication errors
            messageLiveData.postValue("حدث خطأ حاول مره اخري")
        }
        finally {
            isLoading.postValue(false)
        }
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