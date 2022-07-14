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
package com.twidere.twiderex.paging.lists

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import com.twidere.services.mastodon.model.Account
import com.twidere.services.microblog.model.IUser
import com.twidere.services.twitter.model.TwitterPaging
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.paging.crud.PagingMemoryCache
import com.twidere.twiderex.paging.mediator.list.ListsUserPagingMediator
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import kotlin.test.Test

/**
 * instead of testing pagination, we should focus on our code logic
 */

@OptIn(ExperimentalPagingApi::class)
class TestListsUserPagingMediator(
    userKey: MicroBlogKey,
    memoryCache: PagingMemoryCache<UiUser>
) : ListsUserPagingMediator(userKey, memoryCache) {
    override suspend fun loadUsers(key: String?, count: Int): List<IUser> {
        return TwitterPaging(
            listOf(Account(id = "1", displayName = "x", username = "x", acct = "x")),
            nextPage = "$key next"
        )
    }

    val nextKey get() = paging
}

class ListsUserPagingMediatorTest {
    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun load_nextKeyIsCorrect() {
        runBlocking {
            val mediator = TestListsUserPagingMediator(MicroBlogKey.twitter("123"), PagingMemoryCache())
            val pagingState = PagingState<Int, UiUser>(emptyList(), config = PagingConfig(20), anchorPosition = 0, leadingPlaceholderCount = 0)
            mediator.load(LoadType.REFRESH, pagingState)
            Assert.assertEquals("null next", mediator.nextKey)
            mediator.load(LoadType.APPEND, pagingState)
            Assert.assertEquals("null next next", mediator.nextKey)
        }
    }
}
