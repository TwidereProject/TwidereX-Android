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
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

internal class TrendHistoryQueriesImplTest : BaseCacheDatabaseTest() {
    private val accountKey = MicroBlogKey.twitter("account")
    @Test
    fun insert_ReplaceWhenUniqueKeyEquals() = runBlocking {
        val insert = mockITrend().toUi(accountKey).toDbTrendWithHistory()
        database.trendHistoryQueries.insert(insert.history.first().copy(uses = 10))
        assertEquals(10, database.trendHistoryQueries.findWithTrendKey(accountKey = accountKey, trendKey = insert.trend.trendKey).executeAsOne().uses)
        database.trendHistoryQueries.insert(insert.history.first().copy(uses = 20))
        assertEquals(20, database.trendHistoryQueries.findWithTrendKey(accountKey = accountKey, trendKey = insert.trend.trendKey).executeAsOne().uses)
        database.trendHistoryQueries.insert(insert.history.first().copy(day = 20))
        assertEquals(2, database.trendHistoryQueries.findWithTrendKey(accountKey = accountKey, trendKey = insert.trend.trendKey).executeAsList().size)
    }
}
