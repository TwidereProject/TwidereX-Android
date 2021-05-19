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
import com.twidere.services.twitter.api.LookupResources
import com.twidere.services.twitter.model.fields.Expansions
import com.twidere.services.twitter.model.fields.MediaFields
import com.twidere.services.twitter.model.fields.PlaceFields
import com.twidere.services.twitter.model.fields.PollFields
import com.twidere.services.twitter.model.fields.TweetFields
import com.twidere.services.twitter.model.fields.UserFields
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TwitterLookupApiTest {
    private lateinit var lookupResources: LookupResources

    @BeforeAll
    fun setUp() {
        lookupResources =
            mockRetrofit("https://api.twitter.com/", TwitterRequest2AssetPathConvertor())
    }

    @Test
    fun lookupUserTest() = runBlocking {
        val id = "583328497"
        val result = lookupResources.lookupUser(
            id = id,
            tweetFields = TweetFields.values().joinToString(",") { it.value },
            userFields = UserFields.values().joinToString(",") { it.value },
        )
        assertEquals(id, result.data?.id)
    }

    @Test
    fun lookupUserByNameTest() = runBlocking {
        val name = "TwidereProject"
        val result = lookupResources.lookupUserByName(
            name = name,
            tweetFields = TweetFields.values().joinToString(",") { it.value },
            userFields = UserFields.values().joinToString(",") { it.value },
        )
        assertEquals(name, result.data?.username)
    }

    // @Test
    // fun lookupUsersByNameTest() = runBlocking {
    //     val name = listOf("TwidereProject", "Twitter")
    //     val result = lookupResources.lookupUsersByName(
    //         names = name.joinToString(","),
    //         tweetFields = TweetFields.values().joinToString(",") { it.value },
    //         userFields = UserFields.values().joinToString(",") { it.value },
    //     )
    // }

    @Test
    fun lookupTweetTest() = runBlocking {
        val id = "1390725076996268038"
        val result = lookupResources.lookupTweet(
            id = id,
            userFields = UserFields.values().joinToString(",") { it.value },
            pollFields = PollFields.values().joinToString(",") { it.name },
            placeFields = PlaceFields.values().joinToString(",") { it.value },
            mediaFields = MediaFields.values()
                .joinToString(",") { it.name },
            expansions = Expansions.values().joinToString(",") { it.value },
            tweetFields = TweetFields.values().joinToString(",") { it.value },
        )
        assertEquals(id, result.data?.id)
    }
}
