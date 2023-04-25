package com.fathoor.storyapi.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fathoor.storyapi.model.remote.response.Story
import com.fathoor.storyapi.model.repository.StoryRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DetailStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _storyDetail = MutableLiveData<Story>()
    val storyDetail: MutableLiveData<Story> = _storyDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: MutableLiveData<String?> = _error

    fun userStoryDetail(token: String, id: String) = viewModelScope.launch {
        _isLoading.value = true
        _error.value = null
        try {
            storyRepository.userStoryDetail(token, id).let {
                _isLoading.value = false
                _storyDetail.value = it.story ?: throw Exception(ERROR_STORY)
            }
        } catch (e: Exception) {
            _isLoading.value = false
            if (e is HttpException) {
                if (e.code() == 404) {
                    _error.value = ERROR_STORY
                } else if (e.code() == 401) {
                    _error.value = ERROR_TOKEN
                } else {
                    _error.value = "HTTP Error ${e.code()}"
                }
            }
            Log.e(TAG, "userStoryDetail: ${e.message}")
        }
    }

    private companion object {
        const val TAG = "DetailStoryViewModel"
        const val ERROR_STORY = "Story not found"
        const val ERROR_TOKEN = "You are not logged in!"
    }
}