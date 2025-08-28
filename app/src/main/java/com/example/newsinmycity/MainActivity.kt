package com.example.newsinmycity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var errorTextView: TextView
    private lateinit var favoritesButton: ImageButton
    private lateinit var settingsButton: ImageButton
    private lateinit var headerTitleTextView: TextView
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var skeletonAdapter: SkeletonAdapter
    private lateinit var newsService: NewsService
    private lateinit var likesManager: LikesManager
    private lateinit var cityManager: CityManager
    private lateinit var themeManager: ThemeManager

    // Для отслеживания изменений города
    private var currentCityId: Int = -1

    companion object {
        private const val REQUEST_SETTINGS = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Инициализируем темы до вызова super.onCreate()
        themeManager = ThemeManager(this)
        themeManager.applyTheme()

        super.onCreate(savedInstanceState)

        // Проверяем, выбран ли город
        cityManager = CityManager(this)
        if (!cityManager.isCitySelected()) {
            // Если город не выбран, показываем экран выбора города
            val intent = Intent(this, CitySelectionActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        // Инициализация компонентов
        newsRecyclerView = findViewById(R.id.newsRecyclerView)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        errorTextView = findViewById(R.id.errorTextView)
        favoritesButton = findViewById(R.id.favoritesButton)
        settingsButton = findViewById(R.id.settingsButton)
        headerTitleTextView = findViewById(R.id.headerTitleTextView)
        likesManager = LikesManager(this)

        // Сохраняем текущий город для отслеживания изменений
        currentCityId = cityManager.getSelectedCityId()

        // Обновляем заголовок с названием города
        updateHeaderTitle()

        // Настройка RecyclerView
        newsAdapter = NewsAdapter(mutableListOf()) { newsItem, position ->
            handleLikeClick(newsItem, position)
        }

        // Skeleton adapter для загрузки
        skeletonAdapter = SkeletonAdapter()

        newsRecyclerView.layoutManager = LinearLayoutManager(this)
        newsRecyclerView.adapter = newsAdapter

        // Инициализация сервиса
        newsService = NewsService()

        // Настройка Pull-to-Refresh
        swipeRefreshLayout.setOnRefreshListener {
            loadNews()
        }

        // Настройка цветов для индикатора обновления
        swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )

        // Кнопка избранного с анимацией
        favoritesButton.setOnClickListener {
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // Кнопка настроек с анимацией
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivityForResult(intent, REQUEST_SETTINGS)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // Первоначальная загрузка новостей
        loadNews()
    }

    override fun onResume() {
        super.onResume()

        // Проверяем, изменился ли выбранный город
        val newCityId = cityManager.getSelectedCityId()
        if (currentCityId != newCityId) {
            // Город изменился - обновляем все
            currentCityId = newCityId
            updateHeaderTitle()
            loadNews()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SETTINGS) {
            // При возврате из настроек проверяем изменения
            val newCityId = cityManager.getSelectedCityId()
            if (currentCityId != newCityId) {
                currentCityId = newCityId
                updateHeaderTitle()
                loadNews()
            }
        }
    }

    private fun updateHeaderTitle() {
        val selectedCity = cityManager.getSelectedCity()
        headerTitleTextView.text = "Новости • ${selectedCity.name}"
    }

    private fun handleLikeClick(newsItem: NewsItem, position: Int) {
        val newLikeState = likesManager.toggleLike(newsItem.id)
        newsAdapter.updateLike(position, newLikeState)
    }

    private fun loadNews() {
        lifecycleScope.launch {
            try {
                // Показываем skeleton loading только если не используется pull-to-refresh
                if (!swipeRefreshLayout.isRefreshing) {
                    showSkeletonLoading()
                }

                hideError()

                // Уменьшаем delay до 1 секунды, так как API теперь быстрый
                delay(1000)

                // Загружаем новости из API - всегда Нижний Новгород для начала
                val newsList = newsService.getNewsForCity("nizhny-novgorod").map { newsItem: NewsItem ->
                    // Проверяем состояние лайка для каждой новости
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
                        isLiked = likesManager.isLiked(newsItem.id)
                    )
                }

                if (newsList.isNotEmpty()) {
                    hideSkeletonLoading()
                    newsAdapter.updateNews(newsList)
                    showNewsList()
                } else {
                    hideSkeletonLoading()
                    val selectedCity = cityManager.getSelectedCity()
                    showError("Не удалось загрузить новости для ${selectedCity.name}")
                }

            } catch (e: Exception) {
                hideSkeletonLoading()
                showError("Не удалось загрузить новости: ${e.message}")
            } finally {
                // Скрываем индикатор pull-to-refresh
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun showSkeletonLoading() {
        newsRecyclerView.adapter = skeletonAdapter
        newsRecyclerView.visibility = View.VISIBLE
        errorTextView.visibility = View.GONE
    }

    private fun hideSkeletonLoading() {
        newsRecyclerView.adapter = newsAdapter
    }

    private fun showNewsList() {
        newsRecyclerView.visibility = View.VISIBLE
        errorTextView.visibility = View.GONE
    }

    private fun showError(message: String) {
        newsRecyclerView.visibility = View.GONE
        errorTextView.visibility = View.VISIBLE
        errorTextView.text = message
    }

    private fun hideError() {
        errorTextView.visibility = View.GONE
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
