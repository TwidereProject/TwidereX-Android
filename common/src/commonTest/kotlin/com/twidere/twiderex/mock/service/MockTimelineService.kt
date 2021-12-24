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
import com.twidere.services.microblog.TimelineService
import com.twidere.services.microblog.model.IStatus
import com.twidere.twiderex.mock.model.mockIStatus
import com.twidere.twiderex.mock.model.toIPaging
import kotlinx.coroutines.delay
import org.jetbrains.annotations.TestOnly

internal class MockTimelineService @TestOnly constructor() : TimelineService, MicroBlogService, ErrorService() {
    override suspend fun favorites(
        user_id: String,
        count: Int,
        since_id: String?,
        max_id: String?
    ): List<IStatus> {
        checkError()
        return generateData(count)
    }

    override suspend fun homeTimeline(
        count: Int,
        since_id: String?,
        max_id: String?
    ): List<IStatus> {
        checkError()
        return generateData(count)
    }

    override suspend fun listTimeline(
        list_id: String,
        count: Int,
        max_id: String?,
        since_id: String?
    ): List<IStatus> {
        checkError()
        return generateData(count)
    }

    override suspend fun mentionsTimeline(
        count: Int,
        since_id: String?,
        max_id: String?
    ): List<IStatus> {
        checkError()
        return generateData(count)
    }

    override suspend fun userTimeline(
        user_id: String,
        count: Int,
        since_id: String?,
        max_id: String?,
        exclude_replies: Boolean
    ): List<IStatus> {
        checkError()
        return generateData(count) {
            mockIStatus(hasMedia = true, authorId = user_id)
        }
    }

    private suspend fun generateData(count: Int, create: () -> IStatus = { mockIStatus() }): List<IStatus> {
        val list = mutableListOf<IStatus>()
        for (i in 0 until count) {
            delay(1)
            list.add(create())
        }
        return list.reversed().toIPaging()
    }
}
