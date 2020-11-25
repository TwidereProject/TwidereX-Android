/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.UUID

class SearchRepository(
    private val database: AppDatabase
) {
    val source by lazy {
        database.searchDao().getAll()
    }

    fun addOrUpgrade(
        content: String,
    ) {
        GlobalScope.launch {
            database.searchDao().insertAll(
                DbSearch(
                    _id = UUID.randomUUID().toString(),
                    content = content,
                    lastActive = System.currentTimeMillis()
                )
            )
        }
    }

    fun remove(item: DbSearch) {
        GlobalScope.launch {
            database.searchDao().remove(item)
        }
    }
}
