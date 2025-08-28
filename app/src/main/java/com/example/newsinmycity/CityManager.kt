package com.example.newsinmycity

import android.content.Context
import android.content.SharedPreferences

class CityManager(context: Context) {

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("city_settings", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SELECTED_CITY_ID = "selected_city_id"
        private const val KEY_CITY_SELECTED = "city_selected"
        private const val DEFAULT_CITY_ID = 1 // Нижний Новгород по умолчанию
    }

    fun isCitySelected(): Boolean {
        return sharedPrefs.getBoolean(KEY_CITY_SELECTED, false)
    }

    fun setSelectedCity(cityId: Int) {
        sharedPrefs.edit()
            .putInt(KEY_SELECTED_CITY_ID, cityId)
            .putBoolean(KEY_CITY_SELECTED, true)
            .apply()
    }

    fun getSelectedCityId(): Int {
        return sharedPrefs.getInt(KEY_SELECTED_CITY_ID, DEFAULT_CITY_ID)
    }

    fun getSelectedCity(): City {
        val cityId = getSelectedCityId()
        return CityConfig.getCityById(cityId) ?: CityConfig.getAvailableCities().first()
    }

    fun resetCitySelection() {
        sharedPrefs.edit()
            .putBoolean(KEY_CITY_SELECTED, false)
            .apply()
    }
}
