package com.fathoor.storyapi.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.fathoor.storyapi.helper.Dummy
import com.fathoor.storyapi.helper.MainDispatcherRule
import com.fathoor.storyapi.helper.getOrAwaitValue
import com.fathoor.storyapi.model.remote.response.Story
import com.fathoor.storyapi.model.repository.StoryRepository
import com.fathoor.storyapi.model.repository.UserRepository
import com.fathoor.storyapi.view.helper.StoryDiffCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    private lateinit var mainViewModel: MainViewModel

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var userRepository: UserRepository
    private lateinit var storyRepository: StoryRepository

    @Before
    fun setUp() {
        userRepository = mock(UserRepository::class.java)
        storyRepository = mock(StoryRepository::class.java)
        mainViewModel = MainViewModel(userRepository, storyRepository)
    }

    @Test
    fun `when userStoryList is Success`() = runTest {
        val storiesDummy = Dummy.generateStoryDummy()
        val stories: PagingData<Story> = StoryPagingSourceTest.snapshot(storiesDummy)
        val expectedStories = MutableLiveData<PagingData<Story>>()

        expectedStories.value = stories
        `when`(storyRepository.userStoryList()).thenReturn(expectedStories)

        val actualStories: PagingData<Story> = mainViewModel.userStoryList().getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(diffCallback = StoryDiffCallback(), updateCallback = listUpdateCallback, workerDispatcher = Dispatchers.Main)
        differ.submitData(actualStories)

        assertNotNull(differ.snapshot()) // Assert that the returned list is not null
        assertEquals(storiesDummy, differ.snapshot()) // Assert that the returned list is equal to the dummy list
        assertEquals(storiesDummy.size, differ.snapshot().size) // Assert that the returned list size is equal to the dummy list size
        assertEquals(storiesDummy[0], differ.snapshot()[0]) // Assert that the returned list first item is equal to the dummy list first item
    }

    @Test
    fun `when userStoryList is Success but No Stories`() = runTest {
        val storiesDummy = emptyList<Story>()
        val stories: PagingData<Story> = StoryPagingSourceTest.snapshot(storiesDummy)
        val expectedStories = MutableLiveData<PagingData<Story>>()

        expectedStories.value = stories
        `when`(storyRepository.userStoryList()).thenReturn(expectedStories)

        val actualStories: PagingData<Story> = mainViewModel.userStoryList().getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(diffCallback = StoryDiffCallback(), updateCallback = listUpdateCallback, workerDispatcher = Dispatchers.Main)
        differ.submitData(actualStories)

        assertNotNull(differ.snapshot()) // Assert that the returned list is not null
        assertEquals(storiesDummy, differ.snapshot()) // Assert that the returned list is equal to the dummy list
        assertEquals(0, differ.snapshot().size) // Assert that the returned list size is equal to 0
    }

    private val listUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}

class StoryPagingSourceTest: PagingSource<Int, LiveData<List<Story>>>() {
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<Story>>>): Int { return 0 }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<Story>>> { return LoadResult.Page(emptyList(), 0, 1) }

    companion object {
        fun snapshot(stories: List<Story>): PagingData<Story> { return PagingData.from(stories) }
    }
}