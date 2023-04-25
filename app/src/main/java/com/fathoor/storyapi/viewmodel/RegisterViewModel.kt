package com.fathoor.storyapi.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fathoor.storyapi.model.repository.UserRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException

class RegisterViewModel(private val repository: UserRepository) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isRegistered = MutableLiveData<Boolean>()
    val isRegistered: LiveData<Boolean> = _isRegistered

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun userRegister(name: String, email: String, password: String) = viewModelScope.launch {
        _isLoading.value = true
        _isRegistered.value = false
        _error.value = null

        runCatching {
            repository.userRegister(name, email, password)
        }.let { result ->
            result.onSuccess {
                _isLoading.value = false
                _isRegistered.value = true
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
                Log.e(TAG, "userRegister: ${e.message}")
            }
        }
    }

    private companion object {
        const val TAG = "RegisterViewModel"
        const val ERROR_INTERNET = "No Internet Connection"
    }
}