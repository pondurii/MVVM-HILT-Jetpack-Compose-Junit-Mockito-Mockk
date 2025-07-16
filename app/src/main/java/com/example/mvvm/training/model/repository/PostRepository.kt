package com.example.mvvm.training.model.repository

import com.example.mvvm.training.model.model.Post
import com.example.mvvm.training.model.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface PostRepository {
    fun getPosts(): Flow<Result<List<Post>>>
}

class PostRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : PostRepository {
    override fun getPosts(): Flow<Result<List<Post>>> = flow {
        try {
            val posts = apiService.getPosts()
            emit(Result.success(posts))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}