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

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

actual class AccountPreferencesFactory(
    private val context: Context,
) {
    actual fun create(accountKey: MicroBlogKey) = createAccountPreferences(context, accountKey)

    private fun createAccountPreferences(
        context: Context,
        accountKey: MicroBlogKey,
    ): AccountPreferences {
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        return AccountPreferences(
            dataStore = PreferenceDataStoreFactory.create(
                corruptionHandler = null,
                migrations = listOf(),
                scope = scope
            ) {
                context.applicationContext.preferencesDataStoreFile(accountKey.toString())
            },
            scope = scope
        )
    }
}
