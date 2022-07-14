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
package com.twidere.twiderex.db

import com.twidere.twiderex.base.BaseAppDatabaseTest
import com.twidere.twiderex.db.sqldelight.SqlDelightAppDatabaseImpl
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.ComposeType
import com.twidere.twiderex.sqldelight.table.Draft
import com.twidere.twiderex.sqldelight.table.Search
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.UUID

internal class SqlDelightAppDatabaseImplTest : BaseAppDatabaseTest() {
    @Test
    fun clearAllTables() = runBlocking {
        val appDatabase = SqlDelightAppDatabaseImpl(database)
        val accountKey = MicroBlogKey.twitter("test")
        database.searchQueries.insert(Search(content = "test", lastActive = System.currentTimeMillis(), saved = false, accountKey = accountKey))
        database.draftQueries.insert(Draft(content = "test", id = UUID.randomUUID().toString(), media = emptyList(), createAt = System.currentTimeMillis(), composeType = ComposeType.New, statusKey = null, excludedReplyUserIds = emptyList()))
        assert(database.draftQueries.getAll().executeAsList().isNotEmpty())
        assert(database.searchQueries.getAll(accountKey).executeAsList().isNotEmpty())
        appDatabase.clearAllTables()
        assert(database.draftQueries.getAll().executeAsList().isEmpty())
        assert(database.searchQueries.getAll(accountKey).executeAsList().isEmpty())
    }
}
