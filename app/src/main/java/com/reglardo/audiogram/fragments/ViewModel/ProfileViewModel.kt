package com.reglardo.audiogram.fragments.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.marsphotos.network.UserApi
import com.reglardo.audiogram.MainActivity
import com.reglardo.audiogram.network.InfoResponse
import kotlinx.coroutines.launch

class ProfileViewModel: ViewModel() {

    var profileResponse: MutableLiveData<InfoResponse> = MutableLiveData()

    fun getProfile(username: String) {
        viewModelScope.launch {
            val response = UserApi.retrofitService.getInfo(MainActivity.token, username)
            profileResponse.value = response
        }
    }

    fun getMyProfile() {
        viewModelScope.launch {
            val response = UserApi.retrofitService.getMyInfo(MainActivity.token)
            profileResponse.value = response
        }
    }
}