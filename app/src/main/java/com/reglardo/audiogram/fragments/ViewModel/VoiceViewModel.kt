package com.reglardo.audiogram.fragments.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.marsphotos.network.VoiceApi
import com.reglardo.audiogram.MainActivity
import com.reglardo.audiogram.network.VoiceResponse
import kotlinx.coroutines.launch

class VoiceViewModel: ViewModel() {
    val voiceResponse: MutableLiveData<VoiceResponse> = MutableLiveData()

    fun getProfileVoices(username: String) {
        viewModelScope.launch {
            val response = VoiceApi.retrofitService.getProfileVoices(MainActivity.token, username)
            voiceResponse.value = response
        }
    }

    fun getMyVoices() {
        viewModelScope.launch {
            val response = VoiceApi.retrofitService.getProfileVoices(MainActivity.token, null)
            voiceResponse.value = response
        }
    }
}