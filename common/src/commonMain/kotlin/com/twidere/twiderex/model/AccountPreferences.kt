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
package com.twidere.twiderex.model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.map

class AccountPreferences(
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope,
) {
    private val isNotificationEnabledKey = booleanPreferencesKey("isNotificationEnabled")
    val isNotificationEnabled
        get() = dataStore.data.map { preferences ->
            preferences[isNotificationEnabledKey] ?: true
        }
    val homeMenuOrder
        get() = dataStore.data.map { preferences ->
            if (!preferences.contains(homeMenuOrderKey) || !preferences.contains(visibleHomeMenuKey)) {
                HomeMenus.values().map { it to it.showDefault }
            } else {
                val order = preferences[homeMenuOrderKey].orEmpty()
                    .split(",")
                    .withIndex()
                    .associate { HomeMenus.valueOf(it.value) to it.index }
                val visible = preferences[visibleHomeMenuKey].orEmpty().split(",")
                HomeMenus.values().sortedBy {
                    order[it]
                }.map { it to visible.contains(it.name) }
            }
        }

    suspend fun setIsNotificationEnabled(value: Boolean) {
        dataStore.edit {
            it[isNotificationEnabledKey] = value
        }
    }

    fun close() {
        // cancel scope will remove file from activeFiles in Datastore
        // prevent crashes caused by multiple DataStores active for the same file
        scope.cancel()
    }

    private val homeMenuOrderKey = stringPreferencesKey("homeMenuOrder")
    private val visibleHomeMenuKey = stringPreferencesKey("visibleHomeMenu")
    suspend fun setHomeMenuOrder(
        data: List<Pair<HomeMenus, Boolean>>,
    ) {
        dataStore.edit {
            it[visibleHomeMenuKey] = data.filter { it.second }.joinToString(",") { it.first.name }
        }
        dataStore.edit {
            it[homeMenuOrderKey] = data.joinToString(",") { it.first.name }
        }
    }
}

expect class AccountPreferencesFactory {
    fun create(accountKey: MicroBlogKey): AccountPreferences
}
