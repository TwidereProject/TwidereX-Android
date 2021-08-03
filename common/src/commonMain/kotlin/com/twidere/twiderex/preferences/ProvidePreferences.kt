/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
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
import androidx.datastore.core.DataStore
import com.twidere.services.http.config.HttpConfig
import com.twidere.services.proxy.ProxyConfig
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

val LocalAppearancePreferences = compositionLocalOf<AppearancePreferences> { error("No AppearancePreferences") }
val LocalDisplayPreferences = compositionLocalOf<DisplayPreferences> { error("No DisplayPreferences") }
val LocalHttpConfig = compositionLocalOf<HttpConfig> { error("No Http config preferences") }
data class PreferencesHolder(
    val appearancePreferences: DataStore<AppearancePreferences>,
    val displayPreferences: DataStore<DisplayPreferences>,
    val miscPreferences: DataStore<MiscPreferences>
) {
    suspend fun warmup() = coroutineScope {
        awaitAll(
            async { appearancePreferences.data.firstOrNull() },
            async { displayPreferences.data.firstOrNull() },
            async { miscPreferences.data.firstOrNull() },
        )
    }
}

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
    val proxyConfig by holder.miscPreferences
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
        .collectAsState(initial = HttpConfig())

    CompositionLocalProvider(
        LocalAppearancePreferences provides appearances,
        LocalDisplayPreferences provides display,
        LocalHttpConfig provides proxyConfig
    ) {
        content.invoke()
    }
}
