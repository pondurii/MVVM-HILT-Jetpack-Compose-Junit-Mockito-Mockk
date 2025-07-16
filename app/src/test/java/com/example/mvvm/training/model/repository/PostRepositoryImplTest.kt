package com.example.mvvm.training.model.repository

import com.example.mvvm.training.model.model.Post
import com.example.mvvm.training.model.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

class PostRepositoryImplTest {


    private val testDispatcher = StandardTestDispatcher()
    private lateinit var apiService: ApiService
    private lateinit var repository: PostRepositoryImpl

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        apiService = mock()
        repository = PostRepositoryImpl(apiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getPosts returns success result`() = runTest {
        val dummyPosts = listOf(Post(id = 1, title = "Test Post", userId = 1, body = ""))

        whenever(apiService.getPosts()).thenReturn(dummyPosts)

        val result = repository.getPosts().first()
        assertTrue(result.isSuccess)
        assertEquals(dummyPosts, result.getOrNull())
    }

    @Test
    fun `getPosts returns failure result on exception`() = runTest {
        val exception = RuntimeException("API failed")

        whenever(apiService.getPosts()).thenThrow(exception)

        val result = repository.getPosts().first()
        assertTrue(result.isFailure)
        assertEquals("API failed", result.exceptionOrNull()?.message)
    }

}