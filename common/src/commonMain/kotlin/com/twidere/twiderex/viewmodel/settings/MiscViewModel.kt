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
package com.twidere.twiderex.viewmodel.settings

import androidx.datastore.core.DataStore
import com.twidere.twiderex.preferences.model.MiscPreferences
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.NitterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class MiscViewModel(
    private val miscPreferences: DataStore<MiscPreferences>,
    private val accountRepository: AccountRepository,
    private val nitterRepository: NitterRepository,
) : ViewModel() {

    private val account by lazy {
        accountRepository.activeAccount.mapNotNull { it }
    }

    val nitter by lazy {
        MutableStateFlow("")
    }

    val isNitterInputValid by lazy {
        MutableStateFlow(true)
    }

    val nitterVerifyLoading by lazy {
        MutableStateFlow(false)
    }

    val nitterVerify by lazy {
        MutableStateFlow(false)
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
        verifyNitterInstance()
    }

    fun checkIfNitterInputValid(value: String) {
        isNitterInputValid.value = value.isEmpty() ||
            (
                (value.startsWith("http://") || value.startsWith("https://")) &&
                    !value.endsWith("/")
                )
    }

    fun setNitterInstance(value: String) {
        if (nitter.value == value) return
        nitter.value = value
        viewModelScope.launch {
            miscPreferences.updateData {
                it.copy(nitterInstance = value)
            }
        }
        verifyNitterInstance()
    }

    fun verifyNitterInstance() {
        if (nitter.value.isEmpty()) {
            nitterVerify.value = false
            nitterVerifyLoading.value = false
            return
        }
        viewModelScope.launch {
            try {
                nitterVerifyLoading.value = true
                nitterRepository.verifyInstance(account.first().user.screenName, instance = nitter.value)
                nitterVerify.value = true
            } catch (e: Exception) {
                e.printStackTrace()
                nitterVerify.value = false
            } finally {
                nitterVerifyLoading.value = false
            }
        }
    }

    fun setUseProxy(value: Boolean) {
        useProxy.value = value
        viewModelScope.launch {
            miscPreferences.updateData {
                it.copy(useProxy = value)
            }
        }
    }

    fun setProxyType(value: String) {
        proxyType.value = MiscPreferences.ProxyType.valueOf(value)
        viewModelScope.launch {
            miscPreferences.updateData {
                it.copy(proxyType = proxyType.value)
            }
        }
    }

    fun setProxyServer(value: String) {
        proxyServer.value = value
        viewModelScope.launch {
            miscPreferences.updateData {
                it.copy(proxyServer = value)
            }
        }
    }

    fun setProxyPort(value: Int) {
        proxyPort.value = value
        viewModelScope.launch {
            miscPreferences.updateData {
                it.copy(proxyPort = value.toInt())
            }
        }
    }

    fun setProxyUserName(value: String) {
        proxyUserName.value = value
        viewModelScope.launch {
            miscPreferences.updateData {
                it.copy(proxyUserName = value)
            }
        }
    }

    fun setProxyPassword(value: String) {
        proxyPassword.value = value
        viewModelScope.launch {
            miscPreferences.updateData {
                it.copy(proxyPassword = value)
            }
        }
    }
}
