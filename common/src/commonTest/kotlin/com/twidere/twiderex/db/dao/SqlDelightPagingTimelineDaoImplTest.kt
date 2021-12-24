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
package com.twidere.twiderex.db.dao

import androidx.paging.PagingSource
import com.twidere.twiderex.base.BaseCacheDatabaseTest
import com.twidere.twiderex.dataprovider.mapper.toPagingTimeline
import com.twidere.twiderex.db.sqldelight.dao.SqlDelightPagingTimelineDaoImpl
import com.twidere.twiderex.db.sqldelight.model.DbStatusWithAttachments.Companion.saveToDb
import com.twidere.twiderex.db.sqldelight.transform.toDbStatusWithAttachments
import com.twidere.twiderex.mock.model.mockIStatus
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

internal class SqlDelightPagingTimelineDaoImplTest : BaseCacheDatabaseTest() {
    private lateinit var dao: SqlDelightPagingTimelineDaoImpl
    private val accountKey = MicroBlogKey.twitter("account")
    override fun setUp() {
        super.setUp()
        dao = SqlDelightPagingTimelineDaoImpl(
            database = database
        )
    }

    @Test
    fun getPagingSource_PagingSourceGenerateCorrectKeyForNext() = runBlocking {
        val pagingKey = "pagingKey"
        val list = listOf(
            mockIStatus().toPagingTimeline(accountKey, pagingKey),
            mockIStatus().toPagingTimeline(accountKey, pagingKey),
            mockIStatus().toPagingTimeline(accountKey, pagingKey),
        )
        dao.insertAll(list.map { it.timeline })
        list.map { it.status.toDbStatusWithAttachments(accountKey = accountKey) }
            .saveToDb(database)
        val pagingSource = dao.getPagingSource(
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
    fun getPagingSource_pagingSourceInvalidateAfterDbUpdate() = runBlocking {
        val pagingKey = "pagingKey"
        val timeline = mockIStatus().toPagingTimeline(accountKey, pagingKey).timeline
        var invalidate = false
        dao.getPagingSource(
            accountKey = accountKey,
            pagingKey = pagingKey
        ).apply {
            registerInvalidatedCallback {
                invalidate = true
            }
            load(PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false))
        }
        dao.insertAll(listOf(timeline))
        val start = System.currentTimeMillis()
        while (!invalidate && System.currentTimeMillis() - start < 3000) {
            continue
        }
        assert(invalidate)
    }
}
