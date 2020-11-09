package com.twidere.twiderex.settings

import android.content.SharedPreferences
import androidx.compose.foundation.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ambientOf
import com.twidere.twiderex.settings.types.BooleanSettingItem

class UseSystemFontSizeSettings(preferences: SharedPreferences) : BooleanSettingItem(preferences) {
    override val title: @Composable () -> Unit = {
        Text(text = "Use the system font size")
    }

    override val defaultValue: Boolean
        get() = true
}

val AmbientUseSystemFontSize = ambientOf<Boolean>()