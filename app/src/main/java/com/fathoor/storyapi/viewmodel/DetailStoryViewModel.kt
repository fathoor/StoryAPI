package com.fathoor.storyapi.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fathoor.storyapi.model.remote.response.Story
import com.fathoor.storyapi.model.repository.StoryRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException

class DetailStoryViewModel(private val repository: StoryRepository) : ViewModel() {
    private val _storyDetail = MutableLiveData<Story?>()
    val storyDetail: LiveData<Story?> = _storyDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun userStoryDetail(token: String, id: String) = viewModelScope.launch {
        _isLoading.value = true
        _error.value = null

        runCatching {
            repository.userStoryDetail(token, id)
        }.let { result ->
            result.onSuccess { response ->
                _isLoading.value = false
                _storyDetail.value = response.story
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
                Log.e(TAG, "userStoryDetail: ${e.message}")
            }
        }
    }

    private companion object {
        const val TAG = "DetailStoryViewModel"
        const val ERROR_INTERNET = "No Internet Connection"
    }
}