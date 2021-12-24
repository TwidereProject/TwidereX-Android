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

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.kmp.StorageProvider
import com.twidere.twiderex.kmp.appFiles
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.io.File

actual class AccountPreferencesFactory {
    actual fun create(accountKey: MicroBlogKey): AccountPreferences {
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        return AccountPreferences(
            dataStore = PreferenceDataStoreFactory.create(
                corruptionHandler = null,
                migrations = listOf(),
                scope = scope
            ) {
                File(
                    get<StorageProvider>().appFiles.dataStoreFile("$accountKey.preferences_pb"),
                )
            },
            scope = scope
        )
    }
}
