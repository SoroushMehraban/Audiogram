package com.reglardo.audiogram.fragments.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.marsphotos.network.UserApi
import com.reglardo.audiogram.MainActivity
import com.reglardo.audiogram.network.GeneralResponse
import com.reglardo.audiogram.network.InfoResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ProfileViewModel : ViewModel() {

    var profileResponse: MutableLiveData<InfoResponse> = MutableLiveData()
    var profileUpdateResponse: MutableLiveData<InfoResponse> = MutableLiveData()
    var profileImageUpdateResponse: MutableLiveData<GeneralResponse> = MutableLiveData()
    var followResponse: MutableLiveData<GeneralResponse> = MutableLiveData()
    var unfollowResponse: MutableLiveData<GeneralResponse> = MutableLiveData()

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

    fun updateProfile(username: String?) {
        viewModelScope.launch {
            val response = if (username != null) {
                UserApi.retrofitService.getInfo(MainActivity.token, username)
            } else {
                UserApi.retrofitService.getMyInfo(MainActivity.token)
            }
            profileUpdateResponse.value = response
        }
    }

    fun followUser(username: String) {
        viewModelScope.launch {
            val response = UserApi.retrofitService.followUser(MainActivity.token, username)
            followResponse.value = response
        }
    }

    fun unfollowUser(username: String) {
        viewModelScope.launch {
            val response = UserApi.retrofitService.unfollowUser(MainActivity.token, username)
            unfollowResponse.value = response
        }
    }

    fun uploadProfilePhoto(image: MultipartBody.Part) {
        viewModelScope.launch {
            val response = UserApi.retrofitService.uploadImage(MainActivity.token, image)
            profileImageUpdateResponse.value = response
        }
    }
}