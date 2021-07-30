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
package com.twidere.twiderex.mock

import com.twidere.services.microblog.LookupService
import com.twidere.services.microblog.MicroBlogService
import com.twidere.services.microblog.model.IStatus
import com.twidere.services.microblog.model.IUser
import com.twidere.services.twitter.model.User
import kotlinx.coroutines.delay

class MockLookUpService : LookupService, MicroBlogService {
    var errorMsg: String? = null

    override suspend fun lookupUserByName(name: String): IUser {
        val id = System.currentTimeMillis()
        return if (!errorMsg.isNullOrEmpty()) throw Error("Test error") else User(
            id = id,
            idStr = id.toString(),
            name = name
        )
    }

    override suspend fun lookupUsersByName(name: List<String>): List<IUser> {
        return if (!errorMsg.isNullOrEmpty()) throw Error("Test error") else name.map {
            delay(1)
            val id = System.currentTimeMillis()
            User(
                id = id,
                idStr = id.toString(),
                name = it
            )
        }
    }

    override suspend fun lookupUser(id: String): IUser {
        return if (!errorMsg.isNullOrEmpty()) throw Error("Test error") else User(
            id = id.toLong(),
            idStr = id,
            name = "name"
        )
    }

    override suspend fun lookupStatus(id: String): IStatus {
        TODO("Not yet implemented")
    }

    override suspend fun userPinnedStatus(userId: String): List<IStatus> {
        TODO("Not yet implemented")
    }
}
