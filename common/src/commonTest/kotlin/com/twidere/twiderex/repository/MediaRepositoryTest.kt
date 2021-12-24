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
package com.twidere.twiderex.repository

import com.twidere.twiderex.mock.db.MockCacheDatabase
import com.twidere.twiderex.mock.db.dao.MockMediaDao
import com.twidere.twiderex.mock.model.mockUiMedia
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

internal class MediaRepositoryTest {
    @Test
    fun findMediasWithBelongToKey() = runBlocking {
        val repository = MediaRepository(
            MockCacheDatabase().apply { (mediaDao() as MockMediaDao).initData(listOf(mockUiMedia("test", MicroBlogKey.twitter("account")))) }
        )
        val result = repository.findMediaByBelongToKey(MicroBlogKey.twitter("account"))
        assert(result.isNotEmpty())
    }
}
