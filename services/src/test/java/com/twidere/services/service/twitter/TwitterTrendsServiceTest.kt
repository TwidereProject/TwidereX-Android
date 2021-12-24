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
package com.twidere.services.service.twitter

import com.twidere.services.api.common.mockTwitterService
import com.twidere.services.microblog.TrendService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TwitterTrendsServiceTest {
    private lateinit var trendsService: TrendService

    @BeforeAll
    fun setUp() {
        trendsService = mockTwitterService()
    }

    @Test
    fun trends_limitCount(): Unit = runBlocking {
        var result = trendsService.trends("1", limit = 1)
        assertEquals(1, result.size)
        result = trendsService.trends("1", limit = 2)
        assertEquals(2, result.size)
        result = trendsService.trends("1", limit = 100)
        assertEquals(10, result.size)
    }
}
