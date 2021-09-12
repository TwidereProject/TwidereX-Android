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
package com.twidere.twiderex.utils.video

import androidx.compose.ui.geometry.Rect
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs

object VideoPool {

    const val DEBOUNCE_DELAY = 500L

    private val pool = ConcurrentHashMap<String, Long>()

    fun get(url: String): Long {
        return pool[url] ?: 1L
    }

    fun set(url: String, position: Long) {
        pool[url] = position
    }

    private val positionPool = ConcurrentHashMap<String, Rect>()

    fun setRect(url: String, rect: Rect) {
        if (rect.top <= 0.0f && rect.bottom <= 0.0f) {
            removeRect(url)
        } else {
            positionPool[url] = rect
        }
    }

    fun removeRect(url: String) {
        positionPool.remove(url)
    }

    fun containsMiddleLine(url: String, middle: Float): Boolean {
        positionPool[url]?.let {
            return it.top <= middle && it.bottom >= middle
        }
        return false
    }

    fun isMostCenter(url: String, middle: Float): Boolean {
        if (positionPool.size == 0) {
            return false
        }
        if (positionPool.size == 1) {
            return true
        }
        var centerUrl = url
        var minGap = Float.MAX_VALUE
        positionPool.forEach {
            abs((it.value.top + it.value.bottom) / 2 - middle).let { curGap ->
                if (curGap < minGap) {
                    minGap = curGap
                    centerUrl = it.key
                }
            }
        }
        return url == centerUrl
    }
}
