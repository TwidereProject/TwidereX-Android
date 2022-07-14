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
package moe.tlaster.precompose.navigation

data class QueryString(
    private val rawInput: String,
) {
    val map by lazy {
        rawInput
            .split("?")
            .lastOrNull()
            .let {
                it ?: ""
            }
            .split("&")
            .asSequence()
            .map { it.split("=") }
            .filter { !it.firstOrNull().isNullOrEmpty() }
            .filter { it.size in 1..2 }
            .map { it[0] to it.elementAtOrNull(1) }
            .groupBy { it.first }
            .map { it.key to it.value.mapNotNull { it.second.takeIf { !it.isNullOrEmpty() } } }
            .toList()
            .toMap()
    }
}

inline fun <reified T> QueryString.query(name: String, default: T? = null): T? {
    val value = map[name]?.firstOrNull() ?: return default
    return convertValue(value)
}

inline fun <reified T> QueryString.queryList(name: String): List<T?> {
    val value = map[name] ?: return emptyList()
    return value.map { convertValue(it) }
}
