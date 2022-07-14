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
package com.twidere.twiderex.mock.db.dao

import androidx.paging.PagingSource
import com.twidere.twiderex.db.dao.PagingTimelineDao
import com.twidere.twiderex.db.dao.StatusDao
import com.twidere.twiderex.mock.paging.MockPagingSource
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.paging.PagingTimeLine
import com.twidere.twiderex.model.paging.PagingTimeLineWithStatus
import kotlinx.coroutines.runBlocking
import org.jetbrains.annotations.TestOnly

internal class MockPagingTimelineDao @TestOnly constructor(private val statusDao: StatusDao) : PagingTimelineDao {
    private val fakeDb = mutableMapOf<String, MutableList<PagingTimeLine>>()

    override fun getPagingSource(
        pagingKey: String,
        accountKey: MicroBlogKey
    ): PagingSource<Int, PagingTimeLineWithStatus> {
        return MockPagingSource(
            fakeDb[pagingKey]?.mapNotNull {
                if (it.accountKey != accountKey) null else {
                    runBlocking {
                        statusDao.findWithStatusKey(it.statusKey, accountKey = accountKey)?.let { status ->
                            PagingTimeLineWithStatus(timeline = it, status = status.copy(isGap = it.isGap))
                        }
                    }
                }
            } ?: emptyList()
        )
    }

    override suspend fun clearAll(pagingKey: String, accountKey: MicroBlogKey) {
        fakeDb[pagingKey]?.removeAll {
            it.accountKey == accountKey
        }
    }

    override suspend fun getLatest(
        pagingKey: String,
        accountKey: MicroBlogKey
    ): PagingTimeLineWithStatus? {
        return fakeDb[pagingKey]?.maxByOrNull { it.timestamp }?.let {
            statusDao.findWithStatusKey(it.statusKey, accountKey = accountKey)?.let { status ->
                PagingTimeLineWithStatus(timeline = it, status = status.copy(isGap = it.isGap))
            }
        }
    }

    override suspend fun findWithStatusKey(
        maxStatusKey: MicroBlogKey,
        accountKey: MicroBlogKey
    ): PagingTimeLine? {
        return fakeDb.values.flatten().find {
            it.statusKey == maxStatusKey && it.accountKey == accountKey
        }
    }

    override suspend fun insertAll(listOf: List<PagingTimeLine>) {
        listOf.forEach { timeline ->
            fakeDb[timeline.pagingKey].let {
                if (it.isNullOrEmpty()) {
                    fakeDb[timeline.pagingKey] = mutableListOf(timeline)
                } else {
                    it.add(timeline)
                }
            }
        }
    }

    override suspend fun delete(statusKey: MicroBlogKey) {
        return fakeDb.values.forEach {
            it.removeAll { timeline -> timeline.statusKey == statusKey }
        }
    }
}
