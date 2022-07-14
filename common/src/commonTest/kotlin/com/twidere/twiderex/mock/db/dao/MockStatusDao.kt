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

import com.twidere.twiderex.db.dao.StatusDao
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jetbrains.annotations.TestOnly

internal class MockStatusDao @TestOnly constructor() : StatusDao {
    private val fakeDb = mutableMapOf<MicroBlogKey, UiStatus>()
    override suspend fun findWithStatusKey(statusKey: MicroBlogKey, accountKey: MicroBlogKey): UiStatus? {
        return fakeDb[statusKey]
    }

    override suspend fun insertAll(listOf: List<UiStatus>, accountKey: MicroBlogKey) {
        listOf.forEach { status ->
            fakeDb[status.statusKey] = status
        }
    }

    override fun findWithStatusKeyWithFlow(
        statusKey: MicroBlogKey,
        accountKey: MicroBlogKey
    ): Flow<UiStatus?> {
        return flow {
            emit(findWithStatusKey(statusKey, accountKey))
        }
    }

    override suspend fun delete(statusKey: MicroBlogKey) {
        fakeDb.remove(statusKey)
    }

    override suspend fun updateAction(
        statusKey: MicroBlogKey,
        accountKey: MicroBlogKey,
        liked: Boolean?,
        retweet: Boolean?
    ) {
        fakeDb[statusKey]?.let {
            fakeDb[statusKey] = it.copy(liked = liked ?: it.liked, retweeted = retweet ?: it.retweeted)
        }
    }
}
