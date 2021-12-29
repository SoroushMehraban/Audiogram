package com.reglardo.audiogram.fragments.ViewModel

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.marsphotos.network.UserApi
import com.reglardo.audiogram.MainActivity
import com.reglardo.audiogram.network.GeneralResponse
import com.reglardo.audiogram.network.InfoResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ProfileViewModel: ViewModel() {

    var profileResponse: MutableLiveData<InfoResponse> = MutableLiveData()
    var profileUpdateResponse: MutableLiveData<GeneralResponse> = MutableLiveData()

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

    fun uploadProfilePhoto(image: MultipartBody.Part) {
        viewModelScope.launch {
            val response = UserApi.retrofitService.uploadImage(MainActivity.token, image)
            profileUpdateResponse.value = response
        }
    }
}