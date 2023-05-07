package com.fathoor.storyapi.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.fathoor.storyapi.model.remote.response.Story
import com.fathoor.storyapi.model.repository.StoryRepository
import com.fathoor.storyapi.model.repository.UserRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
    ) : ViewModel() {
    fun userLogout() = viewModelScope.launch { userRepository.userLogout() }
    fun userStoryList(): LiveData<PagingData<Story>> = storyRepository.userStoryList().cachedIn(viewModelScope)
}