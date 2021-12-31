package com.reglardo.audiogram.authentication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.marsphotos.network.AuthenticationApi
import com.reglardo.audiogram.network.LoginData
import com.reglardo.audiogram.network.GeneralResponse
import com.reglardo.audiogram.network.SignUpData
import kotlinx.coroutines.launch
import java.lang.Exception

class AuthenticationViewModel: ViewModel() {

    var signUpAuthenticationResponse: MutableLiveData<GeneralResponse> = MutableLiveData()
    var loginAuthenticationResponse: MutableLiveData<GeneralResponse> = MutableLiveData()
    var connectionFailed: MutableLiveData<Boolean> = MutableLiveData(false)

    fun signUp(signUpData: SignUpData) {
        viewModelScope.launch {
            try {
                val response = AuthenticationApi.retrofitService.signUp(
                    signUpData.firstName, signUpData.lastName, signUpData.email,
                    signUpData.username, signUpData.password, signUpData.passwordConfirmation)
                signUpAuthenticationResponse.value = response
            } catch (e: Exception) {
                connectionFailed.value = true
            }
        }
    }

    fun login(loginData: LoginData) {
        viewModelScope.launch {
            try {
                val response = AuthenticationApi.retrofitService.login(loginData.username,
                    loginData.password)
                loginAuthenticationResponse.value = response
            } catch (e: Exception) {
                connectionFailed.value = true
            }
        }
    }

}