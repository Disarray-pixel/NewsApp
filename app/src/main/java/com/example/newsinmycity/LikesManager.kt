package com.example.newsinmycity

import android.content.Context
import android.content.SharedPreferences

class LikesManager(context: Context) {

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("news_likes", Context.MODE_PRIVATE)

    fun isLiked(newsId: Int): Boolean {
        return sharedPrefs.getBoolean("liked_$newsId", false)
    }

    fun setLiked(newsId: Int, liked: Boolean) {
        sharedPrefs.edit()
            .putBoolean("liked_$newsId", liked)
            .apply()
    }

    fun toggleLike(newsId: Int): Boolean {
        val newState = !isLiked(newsId)
        setLiked(newsId, newState)
        return newState
    }

    fun getLikedNews(): List<Int> {
        return sharedPrefs.all
            .filter { it.value == true }
            .map { it.key.removePrefix("liked_").toInt() }
    }
}
