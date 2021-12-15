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
package com.twidere.twiderex.http

import androidx.datastore.core.DataStore
import com.twidere.services.http.HttpConfigProvider
import com.twidere.services.http.config.HttpConfig
import com.twidere.services.proxy.ProxyConfig
import com.twidere.twiderex.preferences.model.MiscPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class TwidereHttpConfigProvider(
    private val miscPreferences: DataStore<MiscPreferences>
) : HttpConfigProvider {
    override fun provideConfig(): HttpConfig {
        return runBlocking {
            miscPreferences.data.map {
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
            }.first()
        }
    }
}
