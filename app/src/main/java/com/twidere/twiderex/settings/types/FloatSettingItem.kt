package com.twidere.twiderex.settings.types

import android.content.SharedPreferences
import androidx.core.content.edit

abstract class FloatSettingItem(
    private val preferences: SharedPreferences,
) : SettingItem<Float>() {
    override fun save(value: Float) {
        preferences.edit {
            putFloat(key, value)
        }
    }

    override fun load(): Float {
        return preferences.getFloat(key, defaultValue)
    }

    protected abstract val defaultValue: Float
}