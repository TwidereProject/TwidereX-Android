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
package com.twidere.twiderex.di

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import com.twidere.twiderex.preferences.AppearancePreferences
import com.twidere.twiderex.preferences.DisplayPreferences
import com.twidere.twiderex.preferences.MiscPreferences
import com.twidere.twiderex.preferences.NotificationPreferences
import com.twidere.twiderex.preferences.serializer.AppearancePreferencesSerializer
import com.twidere.twiderex.preferences.serializer.DisplayPreferencesSerializer
import com.twidere.twiderex.preferences.serializer.MiscPreferencesSerializer
import com.twidere.twiderex.preferences.serializer.NotificationPreferencesSerializer
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.firstOrNull
import org.koin.dsl.module
import java.io.File

internal fun preferencesModule() = module {
    single {
        PreferencesHolder(
            appearancePreferences = createDataStore("appearances.pb", AppearancePreferencesSerializer),
            displayPreferences = createDataStore("display.pb", DisplayPreferencesSerializer),
            miscPreferences = createDataStore("misc.pb", MiscPreferencesSerializer),
            notificationPreferences = createDataStore("notification.pb", NotificationPreferencesSerializer),
        )
    }
}

internal data class PreferencesHolder(
    val appearancePreferences: DataStore<AppearancePreferences>,
    val displayPreferences: DataStore<DisplayPreferences>,
    val miscPreferences: DataStore<MiscPreferences>,
    val notificationPreferences: DataStore<NotificationPreferences>,
) {
    suspend fun warmup() = coroutineScope {
        awaitAll(
            async { appearancePreferences.data.firstOrNull() },
            async { displayPreferences.data.firstOrNull() },
            async { miscPreferences.data.firstOrNull() },
            async { notificationPreferences.data.firstOrNull() },
        )
    }
}

internal inline fun <reified T : Any> createDataStore(
    name: String,
    serializer: Serializer<T>,
) = DataStoreFactory.create(
    serializer,
    produceFile = {
        createDataStoreFile(name)
    },
)

internal expect fun createDataStoreFile(name: String): File
