package com.example.mvvm.training.viewmodel

import com.example.mvvm.training.model.model.Post
import com.example.mvvm.training.model.repository.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class PostViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var postRepository: PostRepository
    private lateinit var viewModel: PostViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        postRepository = mock()
        viewModel = PostViewModel(postRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchPosts - success updates posts`() = runTest {
        val dummyPosts = listOf(Post(id = 1, title = "Demo Post", userId = 1, body = ""))

        whenever(postRepository.getPosts()).thenReturn(flow {
            emit(Result.success(dummyPosts))
        })

        viewModel = PostViewModel(postRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals(dummyPosts, state.posts)
        assertNull(state.error)
    }

    @Test
    fun `fetchPosts - failure updates error`() = runTest {
        val throwable = RuntimeException("Fetch error")

        whenever(postRepository.getPosts()).thenReturn(flow {
            emit(Result.failure(throwable))
        })

        viewModel = PostViewModel(postRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals("Fetch error", state.error)
        assertTrue(state.posts.isEmpty())
    }
}