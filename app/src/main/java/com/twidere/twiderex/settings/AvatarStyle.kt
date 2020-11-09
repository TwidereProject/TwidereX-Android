package com.twidere.twiderex.settings

import android.content.SharedPreferences
import androidx.compose.foundation.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ambientOf
import com.twidere.twiderex.settings.types.RadioSettingItem

enum class AvatarStyle {
    Round,
    Square,
}

class AvatarStyleSettings(
    private val preferences: SharedPreferences
) : RadioSettingItem<AvatarStyle>(preferences) {
    override val options: List<AvatarStyle>
        get() = AvatarStyle.values().toList()
    override val itemContent: @Composable (item: AvatarStyle) -> Unit = {
        Text(text = it.name)
    }
    override val title: @Composable () -> Unit = {
        Text(text = "Avatar Style")
    }
    override fun load(): AvatarStyle {
        return preferences.getString(key, AvatarStyle.Round.name)
            ?.let { enumValueOf<AvatarStyle>(it) }
            ?: AvatarStyle.Round
    }
}

val AmbientAvatarStyle = ambientOf<AvatarStyle>()