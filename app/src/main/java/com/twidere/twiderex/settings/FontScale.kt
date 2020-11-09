package com.twidere.twiderex.settings

import android.content.SharedPreferences
import androidx.compose.runtime.ambientOf
import com.twidere.twiderex.settings.types.FloatSettingItem

class FontScaleSettings(preferences: SharedPreferences) : FloatSettingItem(preferences) {
    override val defaultValue: Float = 1.0f
}

val AmbientFontScale = ambientOf<Float>()