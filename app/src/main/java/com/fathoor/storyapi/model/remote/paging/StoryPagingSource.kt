package com.fathoor.storyapi.model.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.fathoor.storyapi.model.local.preference.UserPreference
import com.fathoor.storyapi.model.remote.response.Story
import com.fathoor.storyapi.model.remote.retrofit.ApiService
import retrofit2.await

class StoryPagingSource(private val apiService: ApiService, private val preference: UserPreference) : PagingSource<Int, Story>() {
    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            val page = params.key ?: STARTING_PAGE_INDEX
            val token = preference.getToken()

            if (token != null) {
                val response = apiService.userStoryList("Bearer $token", page, params.loadSize).await()
                val stories = response.listStory
                LoadResult.Page(
                    data = stories,
                    prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                    nextKey = if (stories.isEmpty()) null else page + 1
                )
            } else {
                LoadResult.Error(Exception(ERROR_TOKEN))
            }
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    private companion object {
        const val STARTING_PAGE_INDEX = 1
        const val ERROR_TOKEN = "Token is null"
    }
}