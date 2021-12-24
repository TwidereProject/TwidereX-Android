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
import com.twidere.twiderex.db.sqldelight.transform.toDbMedia
import com.twidere.twiderex.mock.model.mockUiMedia
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

internal class MediaQueriesImplTest : BaseCacheDatabaseTest() {
    @Test
    fun insertMediaAndReturnResultWithGivenBelongToKey() = runBlocking {
        val belongToKey = MicroBlogKey.valueOf("test")
        assert(database.mediaQueries.findMediaByBelongToKey(belongToKey).executeAsList().isEmpty())
        database.mediaQueries.insert(mockUiMedia(belongToKey = belongToKey).toDbMedia())
        assert(database.mediaQueries.findMediaByBelongToKey(belongToKey).executeAsList().isNotEmpty())
        assert(database.mediaQueries.findMediaByBelongToKey(MicroBlogKey.valueOf("test_not_insert")).executeAsList().isEmpty())
    }

    @Test
    fun insert_ReplaceWhenBelongToKeyAndUrlAndOrderEquals() = runBlocking {
        val belongToKey = MicroBlogKey.valueOf("test")
        database.mediaQueries.insert(mockUiMedia(url = "url", belongToKey = belongToKey, order = 0).toDbMedia())
        database.mediaQueries.insert(mockUiMedia(url = "url", belongToKey = belongToKey, order = 0).toDbMedia())
        assertEquals(1, database.mediaQueries.findMediaByBelongToKey(belongToKey).executeAsList().size)
    }
}
