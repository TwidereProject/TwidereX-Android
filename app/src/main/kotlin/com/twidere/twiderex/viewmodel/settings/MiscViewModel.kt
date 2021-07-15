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
package com.twidere.twiderex.viewmodel.settings

import androidx.datastore.core.DataStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.preferences.proto.MiscPreferences
import dagger.assisted.AssistedInject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.lang.NumberFormatException

@OptIn(FlowPreview::class)
class MiscViewModel @AssistedInject constructor(
    private val miscPreferences: DataStore<MiscPreferences>,
    private val inAppNotification: InAppNotification,
) : ViewModel() {
    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(): MiscViewModel
    }

    val nitter by lazy {
        MutableLiveData("")
    }

    val useProxy by lazy {
        MutableStateFlow(false)
    }

    val proxyType by lazy {
        MutableStateFlow(MiscPreferences.ProxyType.HTTP)
    }

    val proxyServer by lazy {
        MutableStateFlow("")
    }

    val proxyPort by lazy {
        MutableStateFlow<Int?>(null)
    }

    val proxyUserName by lazy {
        MutableStateFlow("")
    }

    val proxyPassword by lazy {
        MutableStateFlow("")
    }

    init {
        viewModelScope.launch {
            nitter.value = miscPreferences.data.first().nitterInstance
            useProxy.value = miscPreferences.data.first().useProxy
            proxyServer.value = miscPreferences.data.first().proxyServer
            proxyPort.value = miscPreferences.data.first().proxyPort
            proxyUserName.value = miscPreferences.data.first().proxyUserName
            proxyPassword.value = miscPreferences.data.first().proxyPassword
            proxyType.value = miscPreferences.data.first().proxyType
        }
    }

    fun setNitterInstance(value: String) {
        nitter.value = value
        viewModelScope.launch {
            miscPreferences.updateData {
                it.toBuilder()
                    .setNitterInstance(value)
                    .build()
            }
        }
    }

    fun setUseProxy(value: Boolean) {
        useProxy.value = value
        viewModelScope.launch {
            miscPreferences.updateData {
                it.toBuilder()
                    .setUseProxy(value)
                    .build()
            }
        }
    }

    fun setProxyType(value: String) {
        proxyType.value = MiscPreferences.ProxyType.valueOf(value)
        viewModelScope.launch {
            miscPreferences.updateData {
                it.toBuilder()
                    .setProxyType(proxyType.value)
                    .build()
            }
        }
    }

    fun setProxyServer(value: String) {
        proxyServer.value = value
        viewModelScope.launch {
            miscPreferences.updateData {
                it.toBuilder()
                    .setProxyServer(value)
                    .build()
            }
        }
    }

    fun setProxyPort(value: String) {
        try {
            proxyPort.value = value.toInt()
        } catch (e: NumberFormatException) {
            inAppNotification.show("Proxy server port must be numbers")
            return
        }
        viewModelScope.launch {
            miscPreferences.updateData {
                it.toBuilder()
                    .setProxyPort(value.toInt())
                    .build()
            }
        }
    }

    fun setProxyUserName(value: String) {
        proxyUserName.value = value
        viewModelScope.launch {
            miscPreferences.updateData {
                it.toBuilder()
                    .setProxyUserName(value)
                    .build()
            }
        }
    }

    fun setProxyPassword(value: String) {
        proxyPassword.value = value
        viewModelScope.launch {
            miscPreferences.updateData {
                it.toBuilder()
                    .setProxyPassword(value)
                    .build()
            }
        }
    }
}
