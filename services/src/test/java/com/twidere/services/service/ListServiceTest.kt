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
package com.twidere.services.service

import com.twidere.services.microblog.ListsService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertNotNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class ListServiceTest {
    private lateinit var listService: ListsService

    @BeforeAll
    fun setUp() {
        listService = createService()
    }

    abstract fun createService(): ListsService

    abstract fun testListId(): String

    abstract fun testUserId(): String

    @Test
    fun fetchList() {
        runBlocking {
            val result = listService.lists()
            assertNotNull(result[0])
        }
    }

    @Test
    fun createList() {
        runBlocking {
            val result = listService.createList("test")
            assertNotNull(result)
        }
    }

    @Test
    fun updateList() {
        runBlocking {
            val result = listService.updateList(testListId())
            assertNotNull(result)
        }
    }

    @Test
    fun destroyList() {
        runBlocking {
            listService.destroyList(testListId())
            assert(true)
        }
    }

    @Test
    fun listMembers() {
        runBlocking {
            val result = listService.listMembers(testListId())
            assertNotNull(result[0])
        }
    }

    @Test
    fun addMember() {
        runBlocking {
            listService.addMember(testListId(), testUserId(), "test")
            assert(true)
        }
    }

    @Test
    fun removeMember() {
        runBlocking {
            listService.removeMember(testListId(), testUserId(), "test")
            assert(true)
        }
    }

    @Test
    fun listSubscribers() {
        runBlocking {
            val result = listService.listMembers(testListId())
            assertNotNull(result[0])
        }
    }
}
