package com.example.newsinmycity

data class NewsItem(
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val imageUrl: String? = null,
    val sourceUrl: String,
    val publishedAt: String,
    val viewCount: Int = 0,
    val category: String = "Общее",
    val source: NewsSource,
    val isLiked: Boolean = false
)

data class NewsSource(
    val name: String,        // "NN.RU", "РБК", "МК"
    val type: SourceType,    // WEBSITE, TELEGRAM, RSS
    val url: String         // Источник
)

enum class SourceType {
    WEBSITE,
    TELEGRAM,
    RSS,
    API
}
