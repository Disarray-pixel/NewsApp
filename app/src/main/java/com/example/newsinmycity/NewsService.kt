package com.example.newsinmycity

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class NewsService {

    private val apiBaseUrl = "http://10.0.2.2:3002" // Для эмулятора Android
    // Если тестируете на реальном устройстве, используйте IP вашего компьютера:
    // private val apiBaseUrl = "http://192.168.1.XXX:3001"

    // ОСНОВНОЙ метод - теперь получает объединенные новости (RSS + Telegram)
    suspend fun getNewsForCity(cityName: String): List<NewsItem> {
        return withContext(Dispatchers.IO) {
            try {
                // Используем endpoint объединенных новостей
                val url = "$apiBaseUrl/api/news/combined/${cityName.lowercase().replace(" ", "-")}"
                val response = makeHttpRequest(url)
                parseNewsResponse(response)
            } catch (e: Exception) {
                println("Combined news request failed: ${e.message}")
                e.printStackTrace()
                emptyList()
            }
        }
    }

    // Метод только для RSS новостей (резерв)
    suspend fun getRssNewsForCity(cityName: String): List<NewsItem> {
        return withContext(Dispatchers.IO) {
            try {
                val url = "$apiBaseUrl/api/news/${cityName.lowercase().replace(" ", "-")}"
                val response = makeHttpRequest(url)
                parseNewsResponse(response)
            } catch (e: Exception) {
                println("RSS news request failed: ${e.message}")
                e.printStackTrace()
                emptyList()
            }
        }
    }

    // Метод только для Telegram новостей (резерв)
    suspend fun getTelegramNews(): List<NewsItem> {
        return withContext(Dispatchers.IO) {
            try {
                val url = "$apiBaseUrl/api/telegram/news"
                val response = makeHttpRequest(url)
                parseNewsResponse(response)
            } catch (e: Exception) {
                println("Telegram news request failed: ${e.message}")
                e.printStackTrace()
                emptyList()
            }
        }
    }

    private fun makeHttpRequest(urlString: String): String {
        println("Making request to: $urlString")

        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection

        return try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 15000 // Увеличили таймаут для Telegram
            connection.readTimeout = 15000
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("User-Agent", "NewsApp/1.0")

            val responseCode = connection.responseCode
            println("Response code: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                println("Response received: ${response.take(200)}...")
                response
            } else {
                throw Exception("HTTP Error: $responseCode")
            }
        } catch (e: Exception) {
            println("Request failed: ${e.message}")
            throw e
        } finally {
            connection.disconnect()
        }
    }

    private fun parseNewsResponse(jsonResponse: String): List<NewsItem> {
        val jsonObject = JSONObject(jsonResponse)
        val success = jsonObject.optBoolean("success", false)

        if (!success) {
            throw Exception("API returned success=false")
        }

        val dataArray = jsonObject.optJSONArray("data") ?: return emptyList()
        val newsList = mutableListOf<NewsItem>()

        for (i in 0 until dataArray.length()) {
            val newsJson = dataArray.getJSONObject(i)
            val sourceJson = newsJson.getJSONObject("source")

            // Определяем тип источника по platform поле
            val platform = newsJson.optString("platform", "rss")
            val sourceType = when (platform) {
                "telegram" -> SourceType.TELEGRAM
                "rss" -> SourceType.RSS
                else -> SourceType.RSS
            }

            val newsItem = NewsItem(
                id = i + 1, // Генерируем простой ID
                title = newsJson.optString("title", "Без заголовка"),
                description = newsJson.optString("description", ""),
                imageUrl = if (newsJson.optString("imageUrl").isNullOrEmpty()) null
                else newsJson.optString("imageUrl"),
                sourceUrl = newsJson.optString("sourceUrl", ""),
                publishedAt = newsJson.optString("publishedAt", "Неизвестно"),
                viewCount = newsJson.optInt("viewCount", 0),
                category = newsJson.optString("category", "Общее"),
                source = NewsSource(
                    name = sourceJson.optString("name", "Неизвестный источник"),
                    type = sourceType, // Теперь корректно определяем тип
                    url = newsJson.optString("sourceUrl", "")
                ),
                isLiked = false // Будет загружаться из LikesManager
            )

            newsList.add(newsItem)
        }

        println("Parsed ${newsList.size} news items:")
        println("  RSS: ${newsList.count { it.source.type == SourceType.RSS }}")
        println("  Telegram: ${newsList.count { it.source.type == SourceType.TELEGRAM }}")

        return newsList
    }
}
