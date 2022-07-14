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

import com.twidere.twiderex.dataprovider.db.CacheDatabaseImpl
import com.twidere.twiderex.db.base.CacheDatabaseDaoTest
import com.twidere.twiderex.mock.model.mockUiMedia
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.room.db.transform.toDbMedia
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

internal class MediaDaoImplTest : CacheDatabaseDaoTest() {

    @Test
    fun findMediaByBelongToKey_ReturnsResultMatchTheBelongKey() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        val belongKey = MicroBlogKey.twitter("test")
        roomDatabase.mediaDao().insertAll(listOf(mockUiMedia(belongToKey = belongKey)).toDbMedia())
        assertEquals(belongKey, cacheDatabase.mediaDao().findMediaByBelongToKey(belongKey).first().belongToKey)
    }
}
