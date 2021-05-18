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
import com.twidere.services.twitter.api.TimelineResources
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TwitterTimelineApiTest {
    private lateinit var timelineResources: TimelineResources

    @BeforeAll
    fun setUp() {
        timelineResources = mockRetrofit("https://api.twitter.com/", TwitterRequest2AssetPathConvertor())
    }

    @Test
    fun homeTimelineTest() = runBlocking {
        val timeline = timelineResources.homeTimeline()
        assert(timeline.isNotEmpty())
    }

    @Test
    fun mentionsTimelineTest() = runBlocking {
        val timeline = timelineResources.mentionsTimeline()
        assert(timeline.isNotEmpty())
    }

    @Test
    fun userTimelineTest() = runBlocking {
        val userId = "783214"
        val timeline = timelineResources.userTimeline(user_id = userId)
        assert(timeline.all { it.user?.idStr == userId })
    }

    @Test
    fun favoritesListTest() = runBlocking {
        val userId = "783214"
        val timeline = timelineResources.favoritesList(user_id = userId)
        assert(timeline.isNotEmpty())
    }

    @Test
    fun listTimelineTest() = runBlocking {
        val listId = "123456"
        val timeline = timelineResources.listTimeline(list_id = listId)
        assert(timeline.isNotEmpty())
    }
}
