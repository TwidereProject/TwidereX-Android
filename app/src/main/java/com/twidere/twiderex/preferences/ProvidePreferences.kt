package com.twidere.twiderex.preferences

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.ambientOf
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.datastore.core.DataStore
import com.twidere.twiderex.preferences.proto.AppearancePreferences
import com.twidere.twiderex.preferences.proto.DisplayPreferences

val AmbientAppearancePreferences = ambientOf<AppearancePreferences>()
val AmbientDisplayPreferences = ambientOf<DisplayPreferences>()

@Composable
fun ProvidePreferences(
    appearancePreferences: DataStore<AppearancePreferences>,
    displayPreferences: DataStore<DisplayPreferences>,
    content: @Composable () -> Unit,
) {
    val appearances by appearancePreferences
        .data
        .collectAsState(initial = AppearancePreferences.getDefaultInstance())
    val display by displayPreferences
        .data
        .collectAsState(initial = DisplayPreferences.getDefaultInstance())

    Providers(
        AmbientAppearancePreferences provides appearances,
        AmbientDisplayPreferences provides display,
    ) {
        content.invoke()
    }
}