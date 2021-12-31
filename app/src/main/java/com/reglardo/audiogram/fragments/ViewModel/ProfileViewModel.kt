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
import java.lang.Exception

class ProfileViewModel : ViewModel() {

    var profileResponse: MutableLiveData<InfoResponse> = MutableLiveData()
    var profileUpdateResponse: MutableLiveData<InfoResponse> = MutableLiveData()
    var profileImageUpdateResponse: MutableLiveData<GeneralResponse> = MutableLiveData()
    var voiceUploadResponse: MutableLiveData<GeneralResponse> = MutableLiveData()
    var followResponse: MutableLiveData<GeneralResponse> = MutableLiveData()
    var unfollowResponse: MutableLiveData<GeneralResponse> = MutableLiveData()

    fun getProfile(username: String) {
        viewModelScope.launch {
            try {
                val response = UserApi.retrofitService.getInfo(MainActivity.token, username)
                profileResponse.value = response
            } catch (e: Exception) { }
        }
    }

    fun getMyProfile() {
        viewModelScope.launch {
            try {
                val response = UserApi.retrofitService.getMyInfo(MainActivity.token)
                profileResponse.value = response
            } catch (e: Exception) { }
        }
    }

    fun updateProfile(username: String?) {
        viewModelScope.launch {
            try {
                val response = if (username != null) {
                    UserApi.retrofitService.getInfo(MainActivity.token, username)
                } else {
                    UserApi.retrofitService.getMyInfo(MainActivity.token)
                }
                profileUpdateResponse.value = response
            } catch (e: Exception) { }
        }
    }

    fun followUser(username: String) {
        viewModelScope.launch {
            try {
                val response = UserApi.retrofitService.followUser(MainActivity.token, username)
                followResponse.value = response
            } catch (e: Exception) { }
        }
    }

    fun unfollowUser(username: String) {
        viewModelScope.launch {
            try {
                val response = UserApi.retrofitService.unfollowUser(MainActivity.token, username)
                unfollowResponse.value = response
            } catch (e: Exception) { }
        }
    }

    fun uploadProfilePhoto(image: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                val response = UserApi.retrofitService.uploadImage(MainActivity.token, image)
                profileImageUpdateResponse.value = response
            } catch (e: Exception) { }
        }
    }

    fun uploadVoice(voice: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                val response = UserApi.retrofitService.uploadVoice(MainActivity.token, voice)
                voiceUploadResponse.value = response
            } catch (e: Exception) { }
        }
    }
}