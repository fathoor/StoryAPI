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

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token

    fun userLogin(email: String, password: String) = viewModelScope.launch {
        _isLoading.value = true
        _isLoggedIn.value = false
        _error.value = null

        runCatching {
            repository.userLogin(email, password)
        }.let { result ->
            result.onSuccess { response ->
                _token.value = response.loginResult.token
                saveToken(_token.value.toString())
                _isLoading.value = false
                _isLoggedIn.value = true
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
                Log.e(TAG, "userLogin: ${e.message}")
            }
        }
    }

    private fun saveToken(key: String) = viewModelScope.launch {
        repository.saveToken(key)
    }

    private companion object {
        const val TAG = "LoginViewModel"
        const val ERROR_INTERNET = "No Internet Connection"
    }
}