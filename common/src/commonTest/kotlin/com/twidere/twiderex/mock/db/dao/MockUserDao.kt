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
package com.twidere.twiderex.mock.db.dao

import com.twidere.twiderex.db.dao.UserDao
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jetbrains.annotations.TestOnly

internal class MockUserDao @TestOnly constructor() : UserDao {
    private val fakeDb = mutableMapOf<String, UiUser>()
    override suspend fun findWithUserKey(userKey: MicroBlogKey): UiUser? {
        return fakeDb[userKey.toString()]
    }

    override suspend fun insertAll(listOf: List<UiUser>) {
        listOf.map {
            fakeDb[it.userKey.toString()] = it
        }
    }

    override fun findWithUserKeyFlow(userKey: MicroBlogKey): Flow<UiUser?> {
        return flow {
            emit(findWithUserKey(userKey))
        }
    }

    val datas get() = fakeDb.values.toList()
}
