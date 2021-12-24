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
package com.twidere.twiderex.db.sqldelight

import com.twidere.twiderex.base.BaseCacheDatabaseTest
import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.db.sqldelight.transform.toDbUser
import com.twidere.twiderex.mock.model.mockIUser
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

internal class UserQueriesImplTest : BaseCacheDatabaseTest() {
    @Test
    fun insert_ReplaceWhenUserKeyEquals() = runBlocking {
        val queries = database.userQueries
        val accountKey = MicroBlogKey.twitter("account")
        val user = mockIUser(name = "insert").toUi(accountKey)
        queries.insert(user.toDbUser())
        assertEquals("insert", queries.findWithUserKey(user.userKey).executeAsOneOrNull()?.name)
        queries.insert(user.copy(name = "replace").toDbUser())
        assertEquals("replace", queries.findWithUserKey(user.userKey).executeAsOneOrNull()?.name)
    }
}
