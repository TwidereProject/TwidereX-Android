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
package com.twidere.services.api.mastodon

import com.twidere.services.api.common.mockRetrofit
import com.twidere.services.mastodon.api.ListsResources
import com.twidere.services.mastodon.model.PostAccounts
import com.twidere.services.mastodon.model.PostList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MastodonListsApiTest {
    private lateinit var listResources: ListsResources

    @BeforeAll
    fun setUp() {
        listResources = mockRetrofit("https://test.mastodon.com/", MastodonRequest2AssetPathConvertor())
    }

    @Test
    fun fetchLists() {
        runBlocking {
            val lists = listResources.lists()
            assertEquals("Friends", lists[0].title)
        }
    }

    @Test
    fun createList() {
        runBlocking {
            val listModel = listResources.createList(PostList("test"))
            assertEquals("test", listModel.title)
        }
    }

    @Test
    fun updateList() {
        runBlocking {
            val listModel = listResources.updateList(id = "13585", PostList("testing"))
            assertEquals("testing", listModel.title)
        }
    }

    @Test
    fun deleteList() {
        runBlocking {
            val response = listResources.deleteList(id = "13585")
            assertEquals("{}", response.body())
        }
    }

    @Test
    fun fetchMembers() {
        runBlocking {
            val accounts = listResources.listMembers("13585")
            assertEquals("ikea shark fan account", accounts.body()!![0].displayName)
        }
    }

    @Test
    fun addMember() {
        runBlocking {
            val response = listResources.addMember("13585", PostAccounts(listOf("23634")))
            assertEquals("{}", response.body())
        }
    }

    @Test
    fun removeMember() {
        runBlocking {
            val response = listResources.removeMember("13585", PostAccounts(listOf("23634")))
            assertEquals("{}", response.body())
        }
    }
}
