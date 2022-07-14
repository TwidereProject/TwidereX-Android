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
package com.twidere.twiderex.di.modules

import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.twidere.twiderex.kmp.StorageProvider
import com.twidere.twiderex.kmp.appFiles
import com.twidere.twiderex.preferences.PreferencesHolder
import com.twidere.twiderex.preferences.serializer.AppearancePreferencesSerializer
import com.twidere.twiderex.preferences.serializer.DisplayPreferencesSerializer
import com.twidere.twiderex.preferences.serializer.MiscPreferencesSerializer
import com.twidere.twiderex.preferences.serializer.NotificationPreferencesSerializer
import org.koin.core.scope.Scope
import org.koin.dsl.module
import java.io.File

internal val preferencesModule = module {
    single {
        PreferencesHolder(
            appearancePreferences = createDataStore(
                "appearances.pb",
                AppearancePreferencesSerializer
            ),
            displayPreferences = createDataStore("display.pb", DisplayPreferencesSerializer),
            miscPreferences = createDataStore("misc.pb", MiscPreferencesSerializer),
            notificationPreferences = createDataStore(
                "notification.pb",
                NotificationPreferencesSerializer
            ),
        )
    }
    single {
        PreferenceDataStoreFactory.create {
            createDataStoreFile("perferences.preferences_pb")
        }
    }
}

internal inline fun <reified T : Any> Scope.createDataStore(
    name: String,
    serializer: Serializer<T>,
) = DataStoreFactory.create(
    serializer,
    produceFile = {
        createDataStoreFile(name)
    },
)

private fun Scope.createDataStoreFile(name: String): File {
    return File(get<StorageProvider>().appFiles.dataStoreFile(name))
}
