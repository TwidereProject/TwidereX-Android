/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
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
package com.twidere.twiderex.paging.crud

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import kotlin.test.assertEquals

class PagingMemoryCacheTest {

    private lateinit var mocks: AutoCloseable

    private val pagingMemoryCache = PagingMemoryCache<String>()

    @Mock
    private lateinit var mockObserver: OnInvalidateObserver

    @Before
    fun setUp() {
        mocks = MockitoAnnotations.openMocks(this)
        pagingMemoryCache.addWeakObserver(mockObserver)
    }

    @After
    fun tearDown() {
        mocks.close()
    }

    @Test
    fun find() {
        pagingMemoryCache.insert(listOf("1", "2", "3", "4"))
        // check if
        assertEquals("3", pagingMemoryCache.find(2, 3)[0])

        // check if index out of bound
        assertEquals(4, pagingMemoryCache.find(0, 10).size)

        // check if size correct

        assertEquals(3, pagingMemoryCache.find(0, 3).size)
    }

    @Test
    fun insert_NotifyObserverAfterInsertSuccess() {
        pagingMemoryCache.insert(listOf("1"))
        verify(mockObserver, times(1)).onInvalidate()
    }

    @Test
    fun insert_SilenceAfterInsertFailed() {
        pagingMemoryCache.insert(listOf())
        verify(mockObserver, times(0)).onInvalidate()
    }

    @Test
    fun update_NotifyObserverAfterDeleteSuccess() {
        pagingMemoryCache.insert(listOf("1"))
        pagingMemoryCache.update(
            "2",
            object : Comparable<String> {
                override fun compareTo(other: String): Int {
                    return if (other == "1") 0 else 1
                }
            }
        )
        verify(mockObserver, times(2)).onInvalidate()
    }

    @Test
    fun update_SilenceAfterUpdateFailed() {
        pagingMemoryCache.update(
            "listOf()",
            object : Comparable<String> {
                override fun compareTo(other: String): Int {
                    return 1
                }
            }
        )
        verify(mockObserver, times(0)).onInvalidate()
    }

    @Test
    fun delete_NotifyObserverAfterDeleteSuccess() {
        pagingMemoryCache.insert(listOf("1"))
        pagingMemoryCache.delete("1")
        verify(mockObserver, times(2)).onInvalidate()
    }

    @Test
    fun update_SilenceAfterDeleteFailed() {
        pagingMemoryCache.delete("")
        verify(mockObserver, times(0)).onInvalidate()
    }
}
