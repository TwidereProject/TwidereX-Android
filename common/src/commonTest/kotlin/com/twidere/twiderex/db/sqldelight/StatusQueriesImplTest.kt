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
import com.twidere.twiderex.db.sqldelight.transform.toDbStatusWithAttachments
import com.twidere.twiderex.mock.model.mockIStatus
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class StatusQueriesImplTest : BaseCacheDatabaseTest() {
    @Test
    fun findWithStatusKey() = runBlocking {
        val accountKey = MicroBlogKey.twitter("account")
        val status = mockIStatus().toUi(accountKey = accountKey)
        database.statusQueries.insert(status.toDbStatusWithAttachments(accountKey).status)
        assertEquals(status.statusKey, database.statusQueries.findWithStatusKey(statusKey = status.statusKey).executeAsOneOrNull()?.statusKey)
    }

    @Test
    fun delete_WithGivenStatusKey() = runBlocking {
        val accountKey = MicroBlogKey.twitter("account")
        val status = mockIStatus().toUi(accountKey = accountKey)
        database.statusQueries.insert(status.toDbStatusWithAttachments(accountKey).status)
        assertEquals(status.statusKey, database.statusQueries.findWithStatusKey(statusKey = status.statusKey).executeAsOneOrNull()?.statusKey)
        database.statusQueries.delete(status.statusKey)
        assertNull(database.statusQueries.findWithStatusKey(statusKey = status.statusKey).executeAsOneOrNull())
    }
}
