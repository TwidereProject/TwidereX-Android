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

import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

class PagingMemoryCache<Value> : InvalidateTracker {
    private val cacheList = CopyOnWriteArrayList<Value>()
    private var observer: OnInvalidateObserver? = null

    fun size(): Int {
        return cacheList.size
    }

    fun insert(list: List<Value>, pos: Int = -1) {
        if (list.isEmpty()) return
        if (pos < 0) cacheList.addAll(list) else cacheList.addAll(pos, list)
        dispatchInvalidate()
    }

    private fun dispatchInvalidate() {
        observer?.onInvalidate()
    }

    fun update(item: Value, comparable: Comparable<Value>) {
        val origin = cacheList.find {
            comparable.compareTo(it) == 0
        }
        origin?.let {
            cacheList.indexOf(it)
        }?.let {
            cacheList[it] = item
            dispatchInvalidate()
        }
    }

    fun delete(item: Value) {
        if (cacheList.remove(item)) {
            dispatchInvalidate()
        }
    }

    fun deleteAll(items: List<Value>) {
        if (cacheList.removeAll(items)) {
            dispatchInvalidate()
        }
    }

    fun clear() {
        cacheList.clear()
    }

    fun find(startIndex: Int, limit: Int): List<Value> {
        val endIndex = startIndex + limit // exclusive
        return when {
            endIndex <= cacheList.size -> {
                cacheList.subList(startIndex, endIndex)
            }
            cacheList.size in (startIndex + 1) until endIndex -> {
                cacheList.subList(startIndex, cacheList.size)
            }
            else -> {
                emptyList()
            }
        }.toList()
    }

    fun addWeakObserver(observer: OnInvalidateObserver) {
        register(WeakObserver(this, observer))
    }

    override fun register(observer: OnInvalidateObserver) {
        this.observer = observer
    }

    override fun unRegister(observer: OnInvalidateObserver) {
        if (observer == this.observer) this.observer = null
    }

    class WeakObserver(private val tracker: InvalidateTracker, delegate: OnInvalidateObserver) : OnInvalidateObserver {
        private val delegateRef by lazy {
            WeakReference(delegate)
        }

        init {
            tracker.register(this)
        }

        override fun onInvalidate() {
            delegateRef.get()?.onInvalidate() ?: tracker.unRegister(this)
        }
    }
}

interface OnInvalidateObserver {
    fun onInvalidate()
}

interface InvalidateTracker {
    fun register(observer: OnInvalidateObserver)

    fun unRegister(observer: OnInvalidateObserver)
}
