package com.fathoor.storyapi.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fathoor.storyapi.model.repository.UserRepository
import kotlinx.coroutines.launch

class OnboardViewModel(private val repository: UserRepository) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _hasToken = MutableLiveData<Boolean>()
    val hasToken: LiveData<Boolean> = _hasToken

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token

    init {
        getToken()
    }

    private fun getToken() = viewModelScope.launch {
        _isLoading.value = true
        repository.getToken().let {
            _token.value = it ?: ""
            _hasToken.value = !it.isNullOrEmpty()
            _isLoading.value = false
        }
    }
}