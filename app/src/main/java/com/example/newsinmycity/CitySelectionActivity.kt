package com.example.newsinmycity

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CitySelectionActivity : AppCompatActivity() {

    private lateinit var citiesRecyclerView: RecyclerView
    private lateinit var cityManager: CityManager
    private var isReselection = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_selection)

        // Инициализация
        cityManager = CityManager(this)
        citiesRecyclerView = findViewById(R.id.citiesRecyclerView)

        // Проверяем, это первый выбор города или изменение существующего
        isReselection = cityManager.isCitySelected()

        if (isReselection) {
            // Если город уже был выбран, показываем ActionBar с кнопкой назад
            supportActionBar?.show()
            supportActionBar?.title = "Выберите город"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            // Скрываем приветственный заголовок
            val welcomeHeaderLayout = findViewById<LinearLayout>(R.id.welcomeHeaderLayout)
            welcomeHeaderLayout.visibility = android.view.View.GONE
        } else {
            // При первом запуске убираем ActionBar
            supportActionBar?.hide()
        }

        // Настройка RecyclerView
        val cities = CityConfig.getAvailableCities()
        val cityAdapter = CityAdapter(cities) { selectedCity ->
            onCitySelected(selectedCity)
        }

        citiesRecyclerView.layoutManager = LinearLayoutManager(this)
        citiesRecyclerView.adapter = cityAdapter
    }

    private fun onCitySelected(city: City) {
        // Сохраняем выбранный город
        cityManager.setSelectedCity(city.id)

        if (isReselection) {
            // Если это изменение города, возвращаемся к главному экрану
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        } else {
            // При первом выборе переходим к основному экрану
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Закрываем экран выбора города
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        // Обрабатываем кнопку "назад" только если это изменение города
        if (isReselection) {
            finish()
            return true
        }
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        // Если это изменение города, позволяем вернуться назад
        if (isReselection) {
            super.onBackPressed()
        }
        // При первом запуске блокируем кнопку назад (чтобы пользователь обязательно выбрал город)
    }
}
