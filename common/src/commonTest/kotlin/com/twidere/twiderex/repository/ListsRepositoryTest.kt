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
import com.twidere.twiderex.mock.service.MockListsService
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class ListsRepositoryTest {

    @Test
    fun saveToDbAfterCreateListSuccess() = runBlocking {
        val accountKey = MicroBlogKey.twitter("test")
        val repo = ListsRepository(MockCacheDatabase())
        val listKey = repo.createLists(
            accountKey = accountKey,
            service = MockListsService(),
            title = "list title",
            description = "desc",
            mode = "private",
        ).listKey
        val list = repo.findListWithListKey(accountKey, listKey).first()
        assertNotNull(list)
        assertEquals(accountKey, list.accountKey)
        assertEquals("list title", list.title)
        assertEquals("desc", list.descriptions)
        assertEquals("private", list.mode)
    }

    @Test
    fun saveToDbAfterUpdateListSuccess() = runBlocking {
        val accountKey = MicroBlogKey.twitter("test")
        val repo = ListsRepository(MockCacheDatabase())
        val listId = repo.prepare(accountKey).id

        val listKey = repo.updateLists(
            accountKey = accountKey,
            service = MockListsService(),
            title = "upgrade title",
            description = "upgrade desc",
            mode = "public",
            listId = listId
        ).listKey

        val list = repo.findListWithListKey(accountKey, listKey).first()
        assertNotNull(list)
        assertEquals("upgrade title", list.title)
        assertEquals("upgrade desc", list.descriptions)
        assertEquals("public", list.mode)
    }

    @Test
    fun deleteFromDbAfterDeleteListSuccess() = runBlocking {
        val accountKey = MicroBlogKey.twitter("test")
        val repo = ListsRepository(MockCacheDatabase())
        val list = repo.prepare(accountKey)
        assertNotNull(
            repo.deleteLists(
                accountKey,
                service = MockListsService(),
                listId = list.id,
                listKey = list.listKey
            )
        )
        assertNull(repo.findListWithListKey(accountKey, list.listKey).first())
    }

    @Test
    fun updateStatusToDbAfterSubscribeOrUnsubscribe() = runBlocking {
        val accountKey = MicroBlogKey.twitter("test")
        val repo = ListsRepository(MockCacheDatabase())
        val list = repo.prepare(accountKey)
        assertEquals(true, list.isFollowed)

        repo.unsubscribeLists(accountKey, MockListsService(), list.listKey)
        assertEquals(false, repo.findListWithListKey(accountKey, list.listKey).first()?.isFollowed)

        repo.subscribeLists(accountKey, MockListsService(), list.listKey)
        assertEquals(true, repo.findListWithListKey(accountKey, list.listKey).first()?.isFollowed)
    }

    private suspend fun ListsRepository.prepare(accountKey: MicroBlogKey): UiList {
        return createLists(
            accountKey = accountKey,
            service = MockListsService(),
            title = "list title",
            description = "desc",
            mode = "private",
        )
    }
}
