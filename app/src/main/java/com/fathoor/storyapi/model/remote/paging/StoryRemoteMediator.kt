package com.fathoor.storyapi.model.remote.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.fathoor.storyapi.model.local.preference.UserPreference
import com.fathoor.storyapi.model.remote.response.Story
import com.fathoor.storyapi.model.remote.retrofit.ApiService
import retrofit2.await

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(private val apiService: ApiService, private val preference: UserPreference): RemoteMediator<Int, Story>() {
    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Story>): MediatorResult {
        return try {
            val page = STARTING_PAGE_INDEX
            val token = preference.getToken()

            if (token != null) {
                val response = apiService.userStoryList("Bearer $token", page, state.config.pageSize).await()
                val stories = response.listStory
                MediatorResult.Success(endOfPaginationReached = stories.isEmpty())
            } else {
                MediatorResult.Error(Exception(ERROR_TOKEN))
            }
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private companion object {
        const val STARTING_PAGE_INDEX = 1
        const val ERROR_TOKEN = "Token is null"
    }
}