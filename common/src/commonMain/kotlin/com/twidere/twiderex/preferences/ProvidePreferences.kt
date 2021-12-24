/*
 *  Twidere X
 *
 *  Copyright (C) TwidereProject and Contributors
 * 
 *  This file is part of Twidere X.
 * 
 *  Twidere X is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Twidere X is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Twidere X. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.preferences

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.twidere.services.http.config.HttpConfig
import com.twidere.services.proxy.ProxyConfig
import com.twidere.twiderex.preferences.model.AppearancePreferences
import com.twidere.twiderex.preferences.model.DisplayPreferences
import com.twidere.twiderex.preferences.model.MiscPreferences
import com.twidere.twiderex.ui.LocalVideoPlayback
import kotlinx.coroutines.flow.map

val LocalAppearancePreferences =
    compositionLocalOf<AppearancePreferences> { error("No AppearancePreferences") }
val LocalDisplayPreferences =
    compositionLocalOf<DisplayPreferences> { error("No DisplayPreferences") }
val LocalHttpConfig = compositionLocalOf<HttpConfig> { error("No Http config preferences") }

@Composable
fun ProvidePreferences(
    holder: PreferencesHolder,
    content: @Composable () -> Unit,
) {
    val appearances by holder.appearancePreferences
        .data
        .collectAsState(initial = AppearancePreferences())
    val display by holder.displayPreferences
        .data
        .collectAsState(initial = DisplayPreferences())
    val proxyConfigFlow = remember(holder.miscPreferences.data) {
        holder.miscPreferences
            .data
            .map {
                HttpConfig(
                    proxyConfig = ProxyConfig(
                        enable = it.useProxy,
                        server = it.proxyServer,
                        port = it.proxyPort,
                        userName = it.proxyUserName,
                        password = it.proxyPassword,
                        type = when (it.proxyType) {
                            MiscPreferences.ProxyType.REVERSE -> ProxyConfig.Type.REVERSE
                            else -> ProxyConfig.Type.HTTP
                        }
                    )
                )
            }
    }
    val proxyConfig by proxyConfigFlow.collectAsState(initial = HttpConfig())

    CompositionLocalProvider(
        LocalAppearancePreferences provides appearances,
        LocalDisplayPreferences provides display,
        LocalHttpConfig provides proxyConfig,
        LocalVideoPlayback provides display.autoPlayback,
    ) {
        content.invoke()
    }
}
