package com.example.newsinmycity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class FavoritesActivity : AppCompatActivity() {

    private lateinit var favoritesRecyclerView: RecyclerView
    private lateinit var emptyTextView: TextView
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var newsService: NewsService
    private lateinit var likesManager: LikesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        // Настройка ActionBar с кнопкой назад
        supportActionBar?.title = "Избранные новости"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Инициализация компонентов
        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView)
        emptyTextView = findViewById(R.id.emptyTextView)
        likesManager = LikesManager(this)
        newsService = NewsService()

        // Настройка RecyclerView
        newsAdapter = NewsAdapter(mutableListOf()) { newsItem, position ->
            handleLikeClick(newsItem, position)
        }
        favoritesRecyclerView.layoutManager = LinearLayoutManager(this)
        favoritesRecyclerView.adapter = newsAdapter

        // Загружаем избранные новости
        loadFavoriteNews()
    }

    private fun handleLikeClick(newsItem: NewsItem, position: Int) {
        val newLikeState = likesManager.toggleLike(newsItem.id)

        if (newLikeState) {
            // Если лайк поставлен - обновляем элемент
            newsAdapter.updateLike(position, true)
        } else {
            // Если лайк убран - удаляем из списка
            newsAdapter.removeItem(position)

            // Проверяем, остались ли избранные новости
            if (newsAdapter.itemCount == 0) {
                showEmptyState()
            }
        }
    }

    private fun loadFavoriteNews() {
        lifecycleScope.launch {
            val likedNewsIds = likesManager.getLikedNews()

            if (likedNewsIds.isEmpty()) {
                showEmptyState()
                return@launch
            }

            // Получаем все новости из API и фильтруем только лайкнутые
            val allNews = newsService.getNewsForCity("nizhny-novgorod")
            val favoriteNews: List<NewsItem> = allNews.filter { newsItem: NewsItem ->
                likedNewsIds.contains(newsItem.id)
            }.map { newsItem: NewsItem ->
                // Создаем новый объект NewsItem с isLiked = true
                NewsItem(
                    id = newsItem.id,
                    title = newsItem.title,
                    description = newsItem.description,
                    imageUrl = newsItem.imageUrl,
                    sourceUrl = newsItem.sourceUrl,
                    publishedAt = newsItem.publishedAt,
                    viewCount = newsItem.viewCount,
                    category = newsItem.category,
                    source = newsItem.source,
                    isLiked = true
                )
            }

            if (favoriteNews.isNotEmpty()) {
                showFavoritesList()
                newsAdapter.updateNews(favoriteNews)
            } else {
                showEmptyState()
            }
        }
    }

    private fun showFavoritesList() {
        favoritesRecyclerView.visibility = View.VISIBLE
        emptyTextView.visibility = View.GONE
    }

    private fun showEmptyState() {
        favoritesRecyclerView.visibility = View.GONE
        emptyTextView.visibility = View.VISIBLE
    }

    // Обработка кнопки "назад" в ActionBar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Просто закрываем activity и возвращаемся к предыдущей
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Альтернативный метод для кнопки назад (должен работать автоматически)
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
