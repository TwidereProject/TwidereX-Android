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

import com.twidere.twiderex.dataprovider.db.CacheDatabaseImpl
import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.db.base.CacheDatabaseDaoTest
import com.twidere.twiderex.mock.model.mockIUser
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class UserDaoImplTest : CacheDatabaseDaoTest() {

    @Test
    fun findWithUserKey_ReturnUserMatchUserKey() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        val accountKey = MicroBlogKey.twitter("account")
        val user = mockIUser(id = "user").toUi(accountKey)
        cacheDatabase.userDao().insertAll(listOf(user))
        assertEquals(user.userKey, cacheDatabase.userDao().findWithUserKey(user.userKey)?.userKey)
        assertNull(cacheDatabase.userDao().findWithUserKey(MicroBlogKey.twitter("not exists"))?.userKey)
    }

    @Test
    fun findWithUserKeyFlow_ReturnUserFlowAndMatchUserKey() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        val accountKey = MicroBlogKey.twitter("account")
        val user = mockIUser(id = "user").toUi(accountKey)
        val userFlow = cacheDatabase.userDao().findWithUserKeyFlow(user.userKey)
        assertNull(userFlow.firstOrNull())

        cacheDatabase.userDao().insertAll(listOf(mockIUser("other user").toUi(accountKey)))
        assertNull(userFlow.firstOrNull())

        cacheDatabase.userDao().insertAll(listOf(user))
        assertEquals(user.userKey, userFlow.firstOrNull()?.userKey)
    }
}
