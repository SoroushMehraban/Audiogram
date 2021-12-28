package com.reglardo.audiogram.authentication

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.marsphotos.network.AuthenticationApi
import com.reglardo.audiogram.network.LoginData
import com.reglardo.audiogram.network.Response
import com.reglardo.audiogram.network.SignUpData
import kotlinx.coroutines.launch

class AuthenticationViewModel: ViewModel() {

    var signUpResponse: MutableLiveData<Response> = MutableLiveData()
    var loginResponse: MutableLiveData<Response> = MutableLiveData()

    fun signUp(signUpData: SignUpData) {
        viewModelScope.launch {

            val response = AuthenticationApi.retrofitService.signUp(
                signUpData.firstName, signUpData.lastName, signUpData.email,
                signUpData.username, signUpData.password, signUpData.passwordConfirmation)
            signUpResponse.value = response
        }
    }

    fun login(loginData: LoginData) {
        viewModelScope.launch {
            val response = AuthenticationApi.retrofitService.login(loginData.email,
                                                                   loginData.password)
            loginResponse.value = response
        }
    }

}