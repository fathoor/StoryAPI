package com.fathoor.storyapi.model.repository

import com.fathoor.storyapi.model.remote.response.Response
import com.fathoor.storyapi.model.remote.response.StoryDetailResponse
import com.fathoor.storyapi.model.remote.response.StoryListResponse
import com.fathoor.storyapi.model.remote.retrofit.ApiService
import com.fathoor.storyapi.view.helper.AppExecutor
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.await

class StoryRepository private constructor(
    private val apiService: ApiService,
    appExecutor: AppExecutor
){
    private val networkDispatcher = appExecutor.networkIO.asCoroutineDispatcher()

    suspend fun userStory(token: String, photo: MultipartBody.Part, description: RequestBody) : Response {
        return withContext(networkDispatcher) {
            apiService.userStory("Bearer $token", photo, description).await()
        }
    }

    suspend fun userStoryList(token: String) : StoryListResponse {
        return withContext(networkDispatcher) {
            apiService.userStoryList("Bearer $token").await()
        }
    }

    suspend fun userStoryDetail(token: String, id: String) : StoryDetailResponse {
        return withContext(networkDispatcher) {
            apiService.userStoryDetail("Bearer $token", id).await()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: StoryRepository? = null
        fun getInstance(apiService: ApiService, appExecutor: AppExecutor): StoryRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = StoryRepository(apiService, appExecutor)
                INSTANCE = instance
                instance
            }
        }
    }
}