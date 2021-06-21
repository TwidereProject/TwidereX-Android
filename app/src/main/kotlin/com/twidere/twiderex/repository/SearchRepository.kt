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
package com.twidere.twiderex.repository

import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.model.DbSearch
import com.twidere.twiderex.model.MicroBlogKey
import java.util.UUID

class SearchRepository(
    private val database: AppDatabase
) {
    fun searchHistory(accountKey: MicroBlogKey) = database.searchDao().getAllHistory(accountKey)

    fun savedSearch(accountKey: MicroBlogKey) = database.searchDao().getAllSaved(accountKey)

    suspend fun addOrUpgrade(
        content: String,
        accountKey: MicroBlogKey,
        saved: Boolean = false
    ) {
        val search = database.searchDao().get(content, accountKey)?.let {
            it.copy(
                lastActive = System.currentTimeMillis(),
                saved = if (it.saved) it.saved else saved
            )
        } ?: DbSearch(
            _id = UUID.randomUUID().toString(),
            content = content,
            lastActive = System.currentTimeMillis(),
            saved = false,
            accountKey = accountKey
        )
        database.searchDao().insertAll(
            listOf(search)
        )
    }

    suspend fun remove(item: DbSearch) {
        database.searchDao().remove(item)
    }

    suspend fun get(content: String, accountKey: MicroBlogKey): DbSearch? {
        return database.searchDao().get(content, accountKey)
    }
}
