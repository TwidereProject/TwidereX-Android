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

import com.twidere.services.microblog.MicroBlogService
import com.twidere.services.microblog.RelationshipService
import com.twidere.services.microblog.model.IRelationship
import com.twidere.services.microblog.model.IUser
import com.twidere.services.microblog.model.Relationship
import com.twidere.twiderex.mock.model.mockIUser
import com.twidere.twiderex.mock.model.toIPaging
import org.jetbrains.annotations.TestOnly

internal class MockRelationshipService @TestOnly constructor() : MicroBlogService,
    RelationshipService,
    ErrorService() {
    private val followings = mutableListOf<String>()
    private val followers = mutableListOf<String>()
    override suspend fun block(id: String): IRelationship {
        TODO("Not yet implemented")
    }

    override suspend fun follow(user_id: String) {
        checkError()
        followings.add(user_id)
    }

    override suspend fun followers(user_id: String, nextPage: String?): List<IUser> {
        checkError()
        val id = nextPage ?: System.currentTimeMillis().toString()
        return listOf(
            mockIUser(id = id).also {
                followers.add(id)
            }
        ).toIPaging()
    }

    override suspend fun following(user_id: String, nextPage: String?): List<IUser> {
        checkError()
        val id = nextPage ?: System.currentTimeMillis().toString()
        return listOf(
            mockIUser(id = id).also {
                followings.add(id)
            }
        ).toIPaging()
    }

    override suspend fun showRelationship(target_id: String): IRelationship {
        checkError()
        return Relationship(
            followedBy = followers.contains(target_id),
            following = followings.contains(target_id),
            blockedBy = false,
            blocking = false
        )
    }

    override suspend fun unblock(id: String): IRelationship {
        TODO("Not yet implemented")
    }

    override suspend fun unfollow(user_id: String) {
        checkError()
        followings.remove(user_id)
    }
}
