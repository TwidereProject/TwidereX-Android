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
package com.twidere.twiderex.paging.crud

import kotlin.test.Test
import kotlin.test.assertEquals

class PagingMemoryCacheTest {

    private val pagingMemoryCache = PagingMemoryCache<String>()

    @Test
    fun find() {
        pagingMemoryCache.insert(listOf("1", "2", "3", "4"))
        // check if
        assertEquals("3", pagingMemoryCache.find(2, 1)[0])

        // check if index out of bound
        assertEquals(4, pagingMemoryCache.find(0, 10).size)

        // check if size correct

        assertEquals(3, pagingMemoryCache.find(0, 3).size)
    }

    private fun PagingMemoryCache<String>.verifyInvalidate(times: Int, block: () -> Unit) {
        var invalidateCount = 0
        val observer = object : OnInvalidateObserver {
            override fun onInvalidate() {
                invalidateCount++
            }
        }
        addWeakObserver(observer)
        block.invoke()
        val start = System.currentTimeMillis()
        while (invalidateCount < times && System.currentTimeMillis() - start < 3000) {
            continue
        }
        unRegister(observer)
        assertEquals(times, invalidateCount)
    }

    @Test
    fun insert_NotifyObserverAfterInsertSuccess() {
        pagingMemoryCache.verifyInvalidate(1) {
            pagingMemoryCache.insert(listOf("1"))
        }
    }

    @Test
    fun insert_SilenceAfterInsertFailed() {
        pagingMemoryCache.verifyInvalidate(0) {
            pagingMemoryCache.insert(listOf())
        }
    }

    @Test
    fun update_NotifyObserverAfterDeleteSuccess() {
        pagingMemoryCache.verifyInvalidate(2) {
            pagingMemoryCache.insert(listOf("1"))
            pagingMemoryCache.update(
                "2",
                object : Comparable<String> {
                    override fun compareTo(other: String): Int {
                        return if (other == "1") 0 else 1
                    }
                }
            )
        }
    }

    @Test
    fun update_SilenceAfterUpdateFailed() {
        pagingMemoryCache.verifyInvalidate(0) {
            pagingMemoryCache.update(
                "listOf()",
                object : Comparable<String> {
                    override fun compareTo(other: String): Int {
                        return 1
                    }
                }
            )
        }
    }

    @Test
    fun delete_NotifyObserverAfterDeleteSuccess() {
        pagingMemoryCache.verifyInvalidate(2) {
            pagingMemoryCache.insert(listOf("1"))
            pagingMemoryCache.delete("1")
        }
    }

    @Test
    fun update_SilenceAfterDeleteFailed() {
        pagingMemoryCache.verifyInvalidate(0) {
            pagingMemoryCache.delete("")
        }
    }
}
