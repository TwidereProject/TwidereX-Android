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
package com.twidere.services.api.twitter

import com.twidere.services.api.common.mockRetrofit
import com.twidere.services.twitter.api.FriendshipResources
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TwitterFriendshipApiTest {
    private lateinit var friendshipResources: FriendshipResources

    @BeforeAll
    fun setUp() {
        friendshipResources = mockRetrofit("https://api.twitter.com/", TwitterRequest2AssetPathConvertor())
    }

    @Test
    fun followTest() = runBlocking {
        val userId = "783214"
        val result = friendshipResources.follow(user_id = userId)
        assertEquals(userId, result.idStr)
        // Twitter return false
        assertEquals(false, result.following)
    }

    @Test
    fun unfollowTest() = runBlocking {
        val userId = "783214"
        val result = friendshipResources.unfollow(user_id = userId)
        assertEquals(userId, result.idStr)
        // Twitter return true
        assertEquals(true, result.following)
    }

    @Test
    fun showFriendshipsTest() = runBlocking {
        val targetUserId = "783214"
        val result = friendshipResources.showFriendships(target_id = targetUserId)
        assertEquals(targetUserId, result.relationship?.target?.idStr)
    }
}
