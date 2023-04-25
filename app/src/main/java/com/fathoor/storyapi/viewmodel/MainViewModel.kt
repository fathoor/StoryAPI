package com.fathoor.storyapi.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fathoor.storyapi.model.remote.response.Story
import com.fathoor.storyapi.model.repository.StoryRepository
import com.fathoor.storyapi.model.repository.UserRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException

class MainViewModel(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
    ) : ViewModel() {
    private val _story = MutableLiveData<List<Story>>()
    val story: LiveData<List<Story>> = _story

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun userLogout() = viewModelScope.launch {
        userRepository.userLogout()
    }

    fun userStoryList(token: String) = viewModelScope.launch {
        _isLoading.value = true
        _error.value = null

        runCatching {
            storyRepository.userStoryList(token)
        }.let { result ->
            result.onSuccess { response ->
                _isLoading.value = false
                _story.value = response.listStory
            }
            result.onFailure { e ->
                if (e is HttpException) {
                    val errorResponse = e.response()?.errorBody()?.string()
                    val errorMessage = errorResponse?.let { JSONObject(it).getString("message") }
                    _error.value = errorMessage
                } else {
                    _error.value = ERROR_INTERNET
                }
                _isLoading.value = false
                Log.e(TAG, "userStoryList: ${e.message}")
            }
        }
    }

    private companion object {
        const val TAG = "MainViewModel"
        const val ERROR_INTERNET = "No Internet Connection"
    }
}