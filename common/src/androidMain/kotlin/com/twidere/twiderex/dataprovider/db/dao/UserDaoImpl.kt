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
package com.twidere.twiderex.dataprovider.db.dao

import com.twidere.twiderex.db.dao.UserDao
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.room.db.dao.RoomUserDao
import com.twidere.twiderex.room.db.transform.toDbUser
import com.twidere.twiderex.room.db.transform.toUi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class UserDaoImpl(private val roomUserDao: RoomUserDao) : UserDao {
    override suspend fun findWithUserKey(userKey: MicroBlogKey): UiUser? {
        return roomUserDao.findWithUserKey(userKey)?.toUi()
    }

    override suspend fun insertAll(listOf: List<UiUser>) {
        roomUserDao.insertAll(listOf.map { it.toDbUser() })
    }

    override fun findWithUserKeyFlow(userKey: MicroBlogKey): Flow<UiUser?> {
        return roomUserDao.findWithUserKeyFlow(userKey).map { it?.toUi() }
    }
}
