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

import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.mock.db.MockCacheDatabase
import com.twidere.twiderex.mock.model.mockIStatus
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ReactionRepositoryTest {
    @Test
    fun updateStatusAfterSuccess() = runBlocking {
        val database = MockCacheDatabase()
        val accountKey = MicroBlogKey.twitter("test")
        val repo = ReactionRepository(database)
        val status = mockIStatus().toUi(accountKey = accountKey)
        database.statusDao().insertAll(listOf(status), accountKey)
        assert(!status.liked)
        assert(!status.retweeted)
        repo.updateReaction(
            accountKey = accountKey,
            statusKey = status.statusKey,
            liked = true,
            retweet = true
        )
        val updateStatus = database.statusDao().findWithStatusKey(status.statusKey, accountKey)
        assertEquals(true, updateStatus?.liked)
        assertEquals(true, updateStatus?.retweeted)
    }
}
