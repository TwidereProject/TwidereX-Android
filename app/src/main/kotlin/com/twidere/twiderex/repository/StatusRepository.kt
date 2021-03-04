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
import androidx.room.withTransaction
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.model.DbStatusV2
import com.twidere.twiderex.db.model.ReferenceType
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiStatus.Companion.toUi

class StatusRepository(
    private val database: CacheDatabase,
) {
    fun loadLiveDataFromCache(
        statusKey: MicroBlogKey,
        accountKey: MicroBlogKey
    ): LiveData<UiStatus?> {
        return database.statusDao().findWithStatusKeyWithReferenceLiveData(statusKey).map {
            it?.toUi(accountKey)
        }
    }

    suspend fun loadFromCache(statusKey: MicroBlogKey, accountKey: MicroBlogKey): UiStatus? {
        return database.statusDao().findWithStatusKeyWithReference(statusKey).let {
            it?.toUi(accountKey)
        }
    }

    suspend fun updateStatus(statusKey: MicroBlogKey, action: (DbStatusV2) -> Unit) {
        database.statusDao().findWithStatusKey(statusKey)?.let {
            action.invoke(it)
            database.statusDao().insertAll(listOf(it))
        }
    }

    suspend fun removeStatus(statusKey: MicroBlogKey) {
        database.withTransaction {
            val statusToRemove = listOfNotNull(
                database.statusDao().findWithStatusKey(statusKey),
            ) + database.statusReferenceDao().find(statusKey, ReferenceType.Reply)
                .map { it.status.data }
            val timelineToRemove =
                database.timelineDao().findAllWithStatusKey(statusToRemove.map { it.statusKey })
            val pagingTimelineToRemove =
                database.pagingTimelineDao()
                    .findAllWIthStatusKey(statusToRemove.map { it.statusKey })
            database.statusDao().delete(statusToRemove)
            database.timelineDao().delete(timelineToRemove)
            database.pagingTimelineDao().delete(pagingTimelineToRemove)
            database.statusReferenceDao().remove(statusToRemove.map { it.statusKey })
        }
    }
}
