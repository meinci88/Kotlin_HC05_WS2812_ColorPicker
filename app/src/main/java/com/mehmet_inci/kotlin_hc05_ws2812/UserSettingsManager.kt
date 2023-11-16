package com.mehmet_inci.kotlin_hc05_ws2812 // ktlint-disable package-name

import android.content.Context

class UserSettingsManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("MyAppSettings", Context.MODE_PRIVATE)

    fun saveUserSetting(key: String, value: Any) {
        val editor = sharedPreferences.edit()

        when (value) {
            is String -> editor.putString(key, value)
            is Int -> editor.putInt(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Float -> editor.putFloat(key, value)
            is Long -> editor.putLong(key, value)
        }
        editor.apply()
    }

    fun getUserSetting(key: String, defaultValue: Any): Any {
        return when (defaultValue) {
            is String -> sharedPreferences.getString(key, defaultValue) ?: defaultValue
            is Int -> sharedPreferences.getInt(key, defaultValue)
            is Boolean -> sharedPreferences.getBoolean(key, defaultValue)
            is Float -> sharedPreferences.getFloat(key, defaultValue)
            is Long -> sharedPreferences.getLong(key, defaultValue)
            else -> defaultValue
        }
    }
    // ___________________________________________________________________________________

    fun saveUserSettingSeekbar1(key: String, value: Any) {
        val editor = sharedPreferences.edit()

        when (value) {
            is String -> editor.putString(key, value)
        }
        editor.apply()
    }

    fun getUserSettingSeekbar1(key: String, defaultValue: Any): Any {
        return when (defaultValue) {
            is String -> sharedPreferences.getString(key, defaultValue) ?: defaultValue
            else -> defaultValue
        }
    }

    fun saveUserSettingSeekbar2(key: String, value: Any) {
        val editor = sharedPreferences.edit()

        when (value) {
            is String -> editor.putString(key, value)
        }
        editor.apply()
    }

    fun getUserSettingSeekbar2(key: String, defaultValue: Any): Any {
        return when (defaultValue) {
            is String -> sharedPreferences.getString(key, defaultValue) ?: defaultValue
            else -> defaultValue
        }
    }
    fun saveUserSettingSeekbar3(key: String, value: Any) {
        val editor = sharedPreferences.edit()

        when (value) {
            is String -> editor.putString(key, value)
        }
        editor.apply()
    }

    fun getUserSettingSeekbar3(key: String, defaultValue: Any): Any {
        return when (defaultValue) {
            is String -> sharedPreferences.getString(key, defaultValue) ?: defaultValue
            else -> defaultValue
        }
    }
}
