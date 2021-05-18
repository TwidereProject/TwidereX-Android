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
import com.twidere.services.twitter.api.StatusResources
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TwitterStatusApiTest {
    private lateinit var statusResources: StatusResources

    @BeforeAll
    fun setUp() {
        statusResources = mockRetrofit("https://api.twitter.com/", TwitterRequest2AssetPathConvertor())
    }

    @Test
    fun retweetTest() = runBlocking {
        val id = "1390725076996268038"
        val result = statusResources.retweet(id = id)
        assertEquals(id, result.retweetedStatus?.idStr)
        assertEquals(true, result.retweeted)
    }

    @Test
    fun unretweetTest() = runBlocking {
        val id = "1390725076996268038"
        val result = statusResources.unretweet(id = id)
        assertEquals(id, result.idStr)
        // Twitter returns true
        assertEquals(true, result.retweeted)
    }

    @Test
    fun likeTest() = runBlocking {
        val id = "1390725076996268038"
        val result = statusResources.like(id = id)
        assertEquals(id, result.idStr)
        assertEquals(true, result.favorited)
    }

    @Test
    fun unlikeTest() = runBlocking {
        val id = "1390725076996268038"
        val result = statusResources.unlike(id = id)
        assertEquals(id, result.idStr)
        assertEquals(false, result.favorited)
    }

    @Test
    fun updateTest() = runBlocking {
        val content = "test"
        val result = statusResources.update(status = content)
        assertEquals(content, result.text)
    }

    @Test
    fun destroyTest() = runBlocking {
        val id = "1394562214812479489"
        val result = statusResources.destroy(id = id)
        assertEquals(id, result.idStr)
    }
}
