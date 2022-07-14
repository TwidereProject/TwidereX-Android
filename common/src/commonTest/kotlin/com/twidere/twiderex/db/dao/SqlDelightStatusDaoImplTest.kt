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
package com.twidere.twiderex.db.dao

import com.twidere.twiderex.base.BaseCacheDatabaseTest
import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.db.sqldelight.dao.SqlDelightStatusDaoImpl
import com.twidere.twiderex.mock.model.mockIStatus
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class SqlDelightStatusDaoImplTest : BaseCacheDatabaseTest() {
    private val accountKey = MicroBlogKey.twitter("account")
    @Test
    fun findWithStatusKey_ReturnStatusWithAttachments() = runBlocking {
        val dao = SqlDelightStatusDaoImpl(database)
        val status = mockIStatus().toUi(accountKey = accountKey)
        dao.insertAll(listOf = listOf(status), accountKey = accountKey)
        assertEquals(status, dao.findWithStatusKey(statusKey = status.statusKey, accountKey = accountKey))
    }

    @Test
    fun findWithStatusKeyFlow_FlowUpdatesWhenDbChanged() = runBlocking {
        val dao = SqlDelightStatusDaoImpl(database)
        val status = mockIStatus().toUi(accountKey = accountKey)
        val flow = dao.findWithStatusKeyWithFlow(statusKey = status.statusKey, accountKey = accountKey)
        assertNull(flow.firstOrNull())
        dao.insertAll(listOf = listOf(status), accountKey = accountKey)
        assertEquals(status, flow.firstOrNull())
    }

    @Test
    fun updateReaction() = runBlocking {
        val dao = SqlDelightStatusDaoImpl(database)
        val status = mockIStatus().toUi(accountKey = accountKey)
        dao.insertAll(listOf = listOf(status), accountKey = accountKey)
        val flow = dao.findWithStatusKeyWithFlow(statusKey = status.statusKey, accountKey = accountKey)
        assertEquals(false, status.liked)
        assertEquals(false, status.retweeted)
        dao.updateAction(
            statusKey = status.statusKey,
            accountKey = accountKey,
            liked = true,
            retweet = null
        )
        assertEquals(true, flow.firstOrNull()?.liked)
        assertEquals(false, flow.firstOrNull()?.retweeted)
        dao.updateAction(
            statusKey = status.statusKey,
            accountKey = accountKey,
            liked = null,
            retweet = true
        )
        assertEquals(true, flow.firstOrNull()?.liked)
        assertEquals(true, flow.firstOrNull()?.retweeted)
    }
}
