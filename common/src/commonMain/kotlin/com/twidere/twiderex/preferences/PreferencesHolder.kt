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

import androidx.datastore.core.DataStore
import com.twidere.twiderex.preferences.model.AppearancePreferences
import com.twidere.twiderex.preferences.model.DisplayPreferences
import com.twidere.twiderex.preferences.model.MiscPreferences
import com.twidere.twiderex.preferences.model.NotificationPreferences
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.firstOrNull

data class PreferencesHolder(
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
