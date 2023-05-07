package com.fathoor.storyapi.model.di

import android.content.Context
import com.fathoor.storyapi.model.local.preference.UserPreference
import com.fathoor.storyapi.model.local.preference.dataStore
import com.fathoor.storyapi.model.remote.retrofit.ApiConfig
import com.fathoor.storyapi.model.repository.StoryRepository
import com.fathoor.storyapi.model.repository.UserRepository
import com.fathoor.storyapi.view.helper.AppExecutor

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(context.dataStore)
        val appExecutor = AppExecutor()
        return UserRepository.getInstance(apiService, userPreference, appExecutor)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(context.dataStore)
        val appExecutor = AppExecutor()
        return StoryRepository.getInstance(apiService, userPreference, appExecutor)
    }
}