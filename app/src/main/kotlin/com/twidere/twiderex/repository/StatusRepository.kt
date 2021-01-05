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
package com.twidere.twiderex.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.model.DbStatusV2
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiStatus.Companion.toUi

class StatusRepository(
    private val database: AppDatabase,
) {
    fun loadLiveDataFromCache(statusKey: MicroBlogKey, accountKey: MicroBlogKey): LiveData<UiStatus?> {
        return database.statusDao().findWithStatusIdWithReferenceLiveData(statusKey).map {
            it?.toUi(accountKey)
        }
    }

    suspend fun loadFromCache(statusKey: MicroBlogKey, accountKey: MicroBlogKey): UiStatus? {
        return database.statusDao().findWithStatusIdWithReference(statusKey).let {
            it?.toUi(accountKey)
        }
    }

    suspend fun updateStatus(statusKey: MicroBlogKey, action: (DbStatusV2) -> Unit) {
        database.statusDao().findWithStatusId(statusKey)?.let {
            action.invoke(it)
            database.statusDao().insertAll(listOf(it))
        }
    }
}
