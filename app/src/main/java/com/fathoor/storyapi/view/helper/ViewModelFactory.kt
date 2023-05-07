package com.fathoor.storyapi.view.helper

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fathoor.storyapi.model.di.Injection.provideStoryRepository
import com.fathoor.storyapi.model.di.Injection.provideUserRepository
import com.fathoor.storyapi.viewmodel.AddStoryViewModel
import com.fathoor.storyapi.viewmodel.DetailStoryViewModel
import com.fathoor.storyapi.viewmodel.LoginViewModel
import com.fathoor.storyapi.viewmodel.MainViewModel
import com.fathoor.storyapi.viewmodel.MapViewModel
import com.fathoor.storyapi.viewmodel.OnboardViewModel
import com.fathoor.storyapi.viewmodel.RegisterViewModel

class ViewModelFactory(private val application: Application) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when (modelClass) {
            OnboardViewModel::class.java -> OnboardViewModel(provideUserRepository(application))
            LoginViewModel::class.java -> LoginViewModel(provideUserRepository(application))
            RegisterViewModel::class.java -> RegisterViewModel(provideUserRepository(application))
            MainViewModel::class.java -> MainViewModel(provideUserRepository(application), provideStoryRepository(application))
            DetailStoryViewModel::class.java -> DetailStoryViewModel(provideStoryRepository(application))
            AddStoryViewModel::class.java -> AddStoryViewModel(provideStoryRepository(application), application)
            MapViewModel::class.java -> MapViewModel(provideStoryRepository(application))
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        } as T

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(application: Application): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(application)
            }.also { instance = it }
    }
}