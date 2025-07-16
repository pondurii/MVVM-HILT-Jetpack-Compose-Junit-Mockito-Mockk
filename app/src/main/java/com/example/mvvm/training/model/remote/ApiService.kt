package com.example.mvvm.training.model.remote

import com.example.mvvm.training.model.model.Post
import retrofit2.http.GET

interface ApiService {
    @GET("posts")
    suspend fun getPosts(): List<Post>
}