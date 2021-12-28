package com.reglardo.audiogram.authentication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.marsphotos.network.AuthenticationApi
import com.reglardo.audiogram.network.LoginData
import com.reglardo.audiogram.network.AuthenticationResponse
import com.reglardo.audiogram.network.SignUpData
import kotlinx.coroutines.launch

class AuthenticationViewModel: ViewModel() {

    var signUpAuthenticationResponse: MutableLiveData<AuthenticationResponse> = MutableLiveData()
    var loginAuthenticationResponse: MutableLiveData<AuthenticationResponse> = MutableLiveData()

    fun signUp(signUpData: SignUpData) {
        viewModelScope.launch {

            val response = AuthenticationApi.retrofitService.signUp(
                signUpData.firstName, signUpData.lastName, signUpData.email,
                signUpData.username, signUpData.password, signUpData.passwordConfirmation)
            signUpAuthenticationResponse.value = response
        }
    }

    fun login(loginData: LoginData) {
        viewModelScope.launch {
            val response = AuthenticationApi.retrofitService.login(loginData.username,
                                                                   loginData.password)
            loginAuthenticationResponse.value = response
        }
    }

}