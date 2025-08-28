package com.example.newsinmycity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var cityManager: CityManager
    private lateinit var themeManager: ThemeManager
    private lateinit var currentCityTextView: TextView
    private lateinit var currentThemeTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Настройка ActionBar
        supportActionBar?.title = "Настройки"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Инициализация менеджеров
        cityManager = CityManager(this)
        themeManager = ThemeManager(this)

        // Инициализация элементов
        currentCityTextView = findViewById(R.id.currentCityTextView)
        currentThemeTextView = findViewById(R.id.currentThemeTextView)

        val changeCityLayout = findViewById<LinearLayout>(R.id.changeCityLayout)
        val changeThemeLayout = findViewById<LinearLayout>(R.id.changeThemeLayout)

        // Обновляем текущие значения
        updateCurrentValues()

        // Обработчик смены города - переходим к экрану выбора города
        changeCityLayout.setOnClickListener {
            openCitySelection()
        }

        // Обработчик смены темы
        changeThemeLayout.setOnClickListener {
            showThemeSelectionDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        // Обновляем значения при возвращении на экран
        updateCurrentValues()
    }

    private fun updateCurrentValues() {
        val selectedCity = cityManager.getSelectedCity()
        currentCityTextView.text = selectedCity.name

        currentThemeTextView.text = themeManager.getThemeName()
    }

    private fun openCitySelection() {
        val intent = Intent(this, CitySelectionActivity::class.java)
        startActivity(intent)

        // Отправляем результат, что настройки изменились
        setResult(Activity.RESULT_OK)

        // Закрываем настройки
        finish()
    }

    private fun showThemeSelectionDialog() {
        val themeNames = arrayOf("Светлая", "Темная", "Системная")
        val currentTheme = themeManager.getThemeMode()

        AlertDialog.Builder(this)
            .setTitle("Выберите тему")
            .setSingleChoiceItems(themeNames, currentTheme) { dialog, which ->
                themeManager.setThemeMode(which)
                updateCurrentValues()

                // Отправляем результат, что настройки изменились
                setResult(Activity.RESULT_OK)

                dialog.dismiss()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
