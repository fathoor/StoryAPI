package com.fathoor.storyapi.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fathoor.storyapi.model.repository.UserRepository
import kotlinx.coroutines.launch
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
        try {
            val response = repository.userRegister(name, email, password)
            if (!response.error) {
                _isLoading.value = false
                _isRegistered.value = true
            } else {
                _isLoading.value = false
            }
        } catch (e: Exception) {
            _isLoading.value = false
            _error.value = e.message
            if (e is HttpException) {
                if (e.code() == 400) {
                    _error.value = ERROR_REGISTER
                } else {
                    _error.value = "HTTP Error ${e.code()}"
                }
            } else {
                _error.value = e.message
            }
            Log.e(TAG, "userRegister: ${e.message}")
        }
    }

    companion object {
        const val TAG = "RegisterViewModel"
        const val ERROR_REGISTER = "Email is already taken"
    }
}