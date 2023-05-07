package com.fathoor.storyapi.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.fathoor.storyapi.model.local.preference.UserPreference
import com.fathoor.storyapi.model.remote.paging.StoryPagingSource
import com.fathoor.storyapi.model.remote.paging.StoryRemoteMediator
import com.fathoor.storyapi.model.remote.response.Response
import com.fathoor.storyapi.model.remote.response.Story
import com.fathoor.storyapi.model.remote.response.StoryDetailResponse
import com.fathoor.storyapi.model.remote.response.StoryListResponse
import com.fathoor.storyapi.model.remote.retrofit.ApiService
import com.fathoor.storyapi.view.helper.AppExecutor
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.await

class StoryRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    appExecutor: AppExecutor
){
    private val networkDispatcher = appExecutor.networkIO.asCoroutineDispatcher()

    suspend fun userStory(token: String, photo: MultipartBody.Part, description: RequestBody, lat: Float?, lon: Float?) : Response {
        return withContext(networkDispatcher) {
            apiService.userStory("Bearer $token", photo, description, lat, lon).await()
        }
    }

    fun userStoryList() : LiveData<PagingData<Story>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = 10, prefetchDistance = 5),
            remoteMediator = StoryRemoteMediator(apiService, userPreference),
            pagingSourceFactory = { StoryPagingSource(apiService, userPreference) }
        ).liveData
    }

    suspend fun userStoryMap(token: String, location: Int) : StoryListResponse {
        return withContext(networkDispatcher) {
            apiService.userStoryMap("Bearer $token", location).await()
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
        fun getInstance(apiService: ApiService, userPreference: UserPreference, appExecutor: AppExecutor): StoryRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = StoryRepository(apiService, userPreference, appExecutor)
                INSTANCE = instance
                instance
            }
        }
    }
}