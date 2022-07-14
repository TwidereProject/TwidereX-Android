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
package com.twidere.twiderex.db

import androidx.paging.PagingSource
import com.twidere.twiderex.dataprovider.db.CacheDatabaseImpl
import com.twidere.twiderex.dataprovider.mapper.toPagingTimeline
import com.twidere.twiderex.db.base.CacheDatabaseDaoTest
import com.twidere.twiderex.mock.model.mockIStatus
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class PagingTimelineDaoImplTest : CacheDatabaseDaoTest() {
    private val accountKey = MicroBlogKey.twitter("account")
    private val pagingKey = "pagingKey"

    @Test
    fun getPagingListCount_ReturnsCountMatchesQuery() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        val originData = listOf(
            mockIStatus().toPagingTimeline(accountKey, pagingKey),
            mockIStatus().toPagingTimeline(accountKey, pagingKey),
            mockIStatus().toPagingTimeline(accountKey, "not included"),
        )
        cacheDatabase.pagingTimelineDao().insertAll(
            originData.map { it.timeline }
        )
        cacheDatabase.statusDao().insertAll(listOf = originData.map { it.status }, accountKey = accountKey)
        assertEquals(2, roomDatabase.pagingTimelineDao().getPagingListCount(accountKey = accountKey, pagingKey = pagingKey))
        assertEquals(2, roomDatabase.pagingTimelineDao().getPagingList(accountKey = accountKey, pagingKey = pagingKey, limit = 10, offset = 0).size)
    }

    @Test
    fun getPagingSource_PagingSourceGenerateCorrectKeyForNext() = runBlocking {

        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        val originData = listOf(
            mockIStatus().toPagingTimeline(accountKey, pagingKey),
            mockIStatus().toPagingTimeline(accountKey, pagingKey),
            mockIStatus().toPagingTimeline(accountKey, pagingKey),
        )
        cacheDatabase.pagingTimelineDao().insertAll(
            originData.map { it.timeline }
        )
        cacheDatabase.statusDao().insertAll(listOf = originData.map { it.status }, accountKey = accountKey)
        val pagingSource = cacheDatabase.pagingTimelineDao().getPagingSource(
            accountKey = accountKey,
            pagingKey = pagingKey
        )
        val limit = 2
        val result = pagingSource.load(params = PagingSource.LoadParams.Refresh(0, limit, false))
        assert(result is PagingSource.LoadResult.Page)
        assertEquals(limit, (result as PagingSource.LoadResult.Page).nextKey)
        assertEquals(limit, result.data.size)

        val loadMoreResult = pagingSource.load(params = PagingSource.LoadParams.Append(result.nextKey ?: 0, limit, false))
        assert(loadMoreResult is PagingSource.LoadResult.Page)
        assertEquals(null, (loadMoreResult as PagingSource.LoadResult.Page).nextKey)
    }

    @Test
    fun getPagingSource_pagingSourceInvalidateAfterDbUpDate() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        var invalidate = false
        cacheDatabase.pagingTimelineDao().getPagingSource(
            accountKey = accountKey,
            pagingKey = pagingKey
        ).apply {
            registerInvalidatedCallback {
                invalidate = true
            }
            load(PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false))
        }
        cacheDatabase.pagingTimelineDao().insertAll(
            listOf(
                mockIStatus().toPagingTimeline(accountKey, pagingKey).timeline,
            )
        )
        val start = System.currentTimeMillis()
        while (!invalidate && System.currentTimeMillis() - start < 3000) {
            continue
        }
        assert(invalidate)
    }
    @Test
    fun getLatest_ReturnsResultWithStatusAndReference() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        val originData = listOf(
            mockIStatus(hasReference = true, hasMedia = true).toPagingTimeline(accountKey, pagingKey),
        )
        cacheDatabase.pagingTimelineDao().insertAll(
            originData.map { it.timeline }
        )
        cacheDatabase.statusDao().insertAll(listOf = originData.map { it.status }, accountKey = accountKey)
        assertEquals(
            originData.first().status.referenceStatus.values.first().statusKey,
            cacheDatabase.pagingTimelineDao().getLatest(
                pagingKey = pagingKey,
                accountKey = accountKey
            )?.status?.referenceStatus?.values?.first()?.statusKey,
        )
    }

    @Test
    fun getLatest_ReturnsResultWithMaxValueOfSortId() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        val originData = listOf(
            mockIStatus().toPagingTimeline(accountKey, pagingKey),
            mockIStatus().toPagingTimeline(accountKey, pagingKey),
            mockIStatus().toPagingTimeline(accountKey, pagingKey),
        )
        cacheDatabase.pagingTimelineDao().insertAll(
            originData.map { it.timeline }
        )
        cacheDatabase.statusDao().insertAll(listOf = originData.map { it.status }, accountKey = accountKey)
        assertEquals(
            originData.maxOf { it.timeline.sortId },
            cacheDatabase.pagingTimelineDao().getLatest(
                pagingKey = pagingKey,
                accountKey = accountKey
            )?.timeline?.sortId
        )
    }

    @Test
    fun delete_DeletePagingTimelineThatMatchesStatusKey(): Unit = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        val originData = listOf(
            mockIStatus().toPagingTimeline(accountKey, pagingKey).timeline,
            mockIStatus().toPagingTimeline(accountKey, pagingKey).timeline,
        )
        cacheDatabase.pagingTimelineDao().insertAll(
            originData
        )
        cacheDatabase.pagingTimelineDao().delete(originData.first().statusKey)
        assertNull(
            cacheDatabase.pagingTimelineDao().findWithStatusKey(
                maxStatusKey = originData.first().statusKey,
                accountKey = accountKey
            )
        )

        assertNotNull(
            cacheDatabase.pagingTimelineDao().findWithStatusKey(
                maxStatusKey = originData[1].statusKey,
                accountKey = accountKey
            )
        )
    }

    @Test
    fun clearAll_ClearAllPagingTimelineThatMatchesPagingKey(): Unit = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        val originData = listOf(
            mockIStatus().toPagingTimeline(accountKey, "pagingKey1").timeline,
            mockIStatus().toPagingTimeline(accountKey, "pagingKey2").timeline,
        )
        cacheDatabase.pagingTimelineDao().insertAll(
            originData
        )
        cacheDatabase.pagingTimelineDao().clearAll(pagingKey = originData.first().pagingKey, accountKey = accountKey)
        assertNull(
            cacheDatabase.pagingTimelineDao().findWithStatusKey(
                maxStatusKey = originData.first().statusKey,
                accountKey = accountKey
            )
        )

        assertNotNull(
            cacheDatabase.pagingTimelineDao().findWithStatusKey(
                maxStatusKey = originData[1].statusKey,
                accountKey = accountKey
            )
        )
    }
}
