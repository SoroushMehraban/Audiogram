package com.reglardo.audiogram.fragments.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.marsphotos.network.UserApi
import com.reglardo.audiogram.MainActivity
import com.reglardo.audiogram.network.SearchResponse
import kotlinx.coroutines.launch

class SearchViewModel: ViewModel() {
    val searchResponse: MutableLiveData<SearchResponse> = MutableLiveData()

    fun searchUsername(username: String) {
        viewModelScope.launch {
            val response = UserApi.retrofitService.searchUsers(MainActivity.token, username)
            searchResponse.value = response
        }
    }
}