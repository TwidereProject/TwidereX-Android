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
package com.twidere.twiderex.mock.db

import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.dao.DraftDao
import com.twidere.twiderex.db.dao.SearchDao
import com.twidere.twiderex.mock.db.dao.MockSearchDao
import org.jetbrains.annotations.TestOnly

internal class MockAppDatabase @TestOnly constructor() : AppDatabase {
    override fun draftDao(): DraftDao {
        TODO("Not yet implemented")
    }

    private val searchDao = MockSearchDao()
    override fun searchDao(): SearchDao {
        return searchDao
    }

    private var cleared = false
    override suspend fun clearAllTables() {
        cleared = true
    }

    fun isAllTablesCleared(): Boolean {
        return cleared
    }

    override suspend fun <R> withTransaction(block: suspend () -> R): R {
        return block.invoke()
    }
}
