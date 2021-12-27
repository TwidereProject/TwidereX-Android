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
package com.twidere.twiderex.mock.service

import com.twidere.services.microblog.LookupService
import com.twidere.services.microblog.MicroBlogService
import com.twidere.services.microblog.model.IStatus
import com.twidere.services.microblog.model.IUser
import com.twidere.twiderex.mock.model.mockIStatus
import com.twidere.twiderex.mock.model.mockIUser
import org.jetbrains.annotations.TestOnly

internal class MockLookUpService @TestOnly constructor() : MicroBlogService, LookupService,
    ErrorService() {
    override suspend fun lookupStatus(id: String): IStatus {
        return mockIStatus(id = id)
    }

    override suspend fun lookupUser(id: String): IUser {
        return mockIUser(id = id)
    }

    override suspend fun lookupUserByName(name: String): IUser {
        return mockIUser(name = name)
    }

    override suspend fun lookupUsersByName(name: List<String>): List<IUser> {
        return name.map {
            lookupUserByName(it)
        }
    }

    override suspend fun userPinnedStatus(userId: String): List<IStatus> {
        return listOf(
            mockIStatus(
                authorId = userId
            )
        )
    }
}
