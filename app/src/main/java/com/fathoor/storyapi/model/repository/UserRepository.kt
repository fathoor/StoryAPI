package com.fathoor.storyapi.model.repository

import com.fathoor.storyapi.model.local.preference.UserPreference
import com.fathoor.storyapi.model.remote.response.AuthResponse
import com.fathoor.storyapi.model.remote.response.Response
import com.fathoor.storyapi.model.remote.retrofit.ApiService
import com.fathoor.storyapi.view.helper.AppExecutor
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import retrofit2.await

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    appExecutor: AppExecutor
) {
    private val diskDispatcher = appExecutor.diskIO.asCoroutineDispatcher()
    private val networkDispatcher = appExecutor.networkIO.asCoroutineDispatcher()

    suspend fun userRegister(name: String, email: String, password: String) : Response {
        return coroutineScope {
            withContext(networkDispatcher) {
                apiService.userRegister(name, email, password).await()
            }
        }
    }

    suspend fun userLogin(email: String, password: String) : AuthResponse {
        return coroutineScope {
            withContext(networkDispatcher) {
                apiService.userLogin(email, password).await()
            }
        }
    }

    suspend fun userLogout() {
        return withContext(diskDispatcher) {
            userPreference.clearToken()
        }
    }

    suspend fun saveToken(token: String) {
        return withContext(diskDispatcher) {
            userPreference.saveToken(token)
        }
    }

    suspend fun getToken(): String? {
        return userPreference.getToken()
    }

    companion object {
        @Volatile
        private var INSTANCE: UserRepository? = null
        fun getInstance(apiService: ApiService, userPreference: UserPreference, appExecutor: AppExecutor): UserRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = UserRepository(apiService, userPreference, appExecutor)
                INSTANCE = instance
                instance
            }
        }
    }
}