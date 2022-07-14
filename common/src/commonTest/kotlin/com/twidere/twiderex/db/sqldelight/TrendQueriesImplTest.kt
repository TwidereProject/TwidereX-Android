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
package com.twidere.twiderex.db.sqldelight

import com.twidere.twiderex.base.BaseCacheDatabaseTest
import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.db.sqldelight.transform.toDbTrendWithHistory
import com.twidere.twiderex.mock.model.mockITrend
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.sqldelight.table.DbTrend
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

internal class TrendQueriesImplTest : BaseCacheDatabaseTest() {
    private val accountKey = MicroBlogKey.twitter("account")
    @Test
    fun insert_ReplaceWhenUniqueKeyEquals() = runBlocking {
        val insert = mockITrend().toUi(accountKey).toDbTrendWithHistory()
        database.trendQueries.insert(insert.trend.copy(displayName = "insert"))
        assertEquals("insert", database.trendQueries.getTrendPagingList(accountKey = accountKey, limit = 10, offset = 0).executeAsOne().displayName)
        database.trendQueries.insert(insert.trend.copy(displayName = "replace"))
        assertEquals("replace", database.trendQueries.getTrendPagingList(accountKey = accountKey, limit = 10, offset = 0).executeAsOne().displayName)
    }

    @Test
    fun getTrendPagingList_ReturnResultsWithGiveOffsetAndLimit() = runBlocking {
        val list = mutableListOf<DbTrend>()
        for (i in 0 until 10) {
            list.add(mockITrend().toUi(accountKey).toDbTrendWithHistory().trend.copy(trendKey = MicroBlogKey.valueOf(i.toString()), displayName = i.toString()))
        }
        database.trendQueries.transaction {
            list.forEach { database.trendQueries.insert(it) }
        }
        assertEquals(10, database.trendQueries.getTrendPagingCount(accountKey = accountKey).executeAsOne())
        val result = database.trendQueries.getTrendPagingList(accountKey = accountKey, limit = 4, offset = 3).executeAsList()
        assertEquals(4, result.size)
        assertEquals(3, result.first().displayName.toInt())
    }
}
