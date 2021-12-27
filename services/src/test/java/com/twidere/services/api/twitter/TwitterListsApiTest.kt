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
package com.twidere.services.api.twitter

import com.twidere.services.api.common.mockRetrofit
import com.twidere.services.twitter.api.ListsResources
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TwitterListsApiTest {
    private lateinit var listResources: ListsResources

    @BeforeAll
    fun setUp() {
        listResources = mockRetrofit("https://api.twitter.com/", TwitterRequest2AssetPathConvertor())
    }

    @Test
    fun fetchLists() {
        runBlocking {
            val lists = listResources.lists()
            assertEquals("meetup-20100301", lists[0].name)
        }
    }

    @Test
    fun createList() {
        runBlocking {
            val listModel = listResources.createList(name = "Goonies")
            assertEquals("Goonies", listModel.name)
        }
    }

    @Test
    fun updateList() {
        runBlocking {
            val listModel = listResources.updateList(name = "update", list_id = "58300198")
            assertEquals("update", listModel.name)
        }
    }

    @Test
    fun deleteList() {
        runBlocking {
            val listModel = listResources.destroyList(list_id = "58300198")
            assertEquals("destroy", listModel.name)
        }
    }

    @Test
    fun fetchSubscribers() {
        runBlocking {
            val response = listResources.listSubscribers("8044403")
            assertEquals("Almissen665", response.users?.get(0)?.name)
        }
    }

    @Test
    fun fetchMembers() {
        runBlocking {
            val response = listResources.listMembers("8044403")
            assertEquals("Sharon Ly", response.users?.get(0)?.name)
        }
    }

    @Test
    fun addMember() {
        runBlocking {
            val listModel = listResources.addMember("58300198", user_id = "14895163", screen_name = "onesnowclimber")
            assertEquals("58300198", listModel.idStr)
        }
    }

    @Test
    fun removeMember() {
        runBlocking {
            val listModel = listResources.removeMember("58300198", user_id = "14895163", screen_name = "onesnowclimber")
            assertEquals("58300198", listModel.idStr)
        }
    }

    @Test
    fun unsubscribeLists() {
        runBlocking {
            val listModel = listResources.unsubscribeLists("58300198")
            assertEquals("58300198", listModel.idStr)
        }
    }

    @Test
    fun subscribeLists() {
        runBlocking {
            val listModel = listResources.subscribeLists("58300198")
            assertEquals("58300198", listModel.idStr)
        }
    }
}
