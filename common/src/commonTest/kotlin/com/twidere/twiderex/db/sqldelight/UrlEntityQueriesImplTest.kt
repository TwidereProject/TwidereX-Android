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
import com.twidere.twiderex.db.sqldelight.transform.toDbUrlEntity
import com.twidere.twiderex.mock.model.mockUiUrlEntity
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

internal class UrlEntityQueriesImplTest : BaseCacheDatabaseTest() {
    @Test
    fun insertUrlAndReturnResultWithGivenBelongToKey() = runBlocking {
        val belongToKey = MicroBlogKey.valueOf("test")
        assert(database.urlEntityQueries.findByBelongToKey(belongToKey).executeAsList().isEmpty())
        database.urlEntityQueries.insert(mockUiUrlEntity().toDbUrlEntity(belongToKey = belongToKey))
        assert(database.urlEntityQueries.findByBelongToKey(belongToKey).executeAsList().isNotEmpty())
        assert(database.urlEntityQueries.findByBelongToKey(MicroBlogKey.valueOf("test_not_insert")).executeAsList().isEmpty())
    }

    @Test
    fun insert_ReplaceWhenBelongToKeyAndUrlEquals() = runBlocking {
        val belongToKey = MicroBlogKey.valueOf("test")
        database.urlEntityQueries.insert(mockUiUrlEntity(url = "url").toDbUrlEntity(belongToKey = belongToKey))
        database.urlEntityQueries.insert(mockUiUrlEntity(url = "url").toDbUrlEntity(belongToKey = belongToKey))
        assertEquals(1, database.urlEntityQueries.findByBelongToKey(belongToKey).executeAsList().size)
    }
}
