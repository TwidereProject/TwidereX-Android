package com.twidere.twiderex.settings.types

import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.core.content.edit

abstract class BooleanSettingItem(
    private val preferences: SharedPreferences,
) : SettingItem<Boolean>() {
    abstract val title: @Composable () -> Unit

    override fun save(value: Boolean) {
        preferences.edit {
            putBoolean(key, value)
        }
    }

    override fun load(): Boolean {
        return preferences.getBoolean(key, defaultValue)
    }

    protected open val defaultValue: Boolean = false
}
