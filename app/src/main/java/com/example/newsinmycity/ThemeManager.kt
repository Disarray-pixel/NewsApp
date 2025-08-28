package com.example.newsinmycity

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class ThemeManager(context: Context) {

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("theme_settings", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_THEME_MODE = "theme_mode"
        const val THEME_LIGHT = 0
        const val THEME_DARK = 1
        const val THEME_SYSTEM = 2
    }

    fun setThemeMode(themeMode: Int) {
        sharedPrefs.edit()
            .putInt(KEY_THEME_MODE, themeMode)
            .apply()

        applyTheme(themeMode)
    }

    fun getThemeMode(): Int {
        return sharedPrefs.getInt(KEY_THEME_MODE, THEME_SYSTEM)
    }

    fun applyTheme(themeMode: Int = getThemeMode()) {
        when (themeMode) {
            THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            THEME_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            THEME_SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    fun getThemeName(themeMode: Int = getThemeMode()): String {
        return when (themeMode) {
            THEME_LIGHT -> "Светлая"
            THEME_DARK -> "Темная"
            THEME_SYSTEM -> "Системная"
            else -> "Системная"
        }
    }
}
