package com.example.newsinmycity

import retrofit2.Response
import retrofit2.http.GET

data class JsonPlaceholderPost(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)

interface NewsApi {

    @GET("posts")
    suspend fun getTestPosts(): Response<List<JsonPlaceholderPost>>

    // Для будущего основного API
    @GET("news")
    suspend fun getNews(): Response<List<NewsItem>>
}
