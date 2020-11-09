package com.twidere.twiderex.settings

import android.content.SharedPreferences
import androidx.compose.foundation.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ambientOf
import com.twidere.twiderex.settings.types.BooleanSettingItem

class MediaPreviewSettings(preferences: SharedPreferences) : BooleanSettingItem(preferences) {
    override val title: @Composable () -> Unit = {
        Text(text = "Media Previews")
    }

    override val defaultValue: Boolean
        get() = true
}

val AmbientMediaPreview = ambientOf<Boolean>()