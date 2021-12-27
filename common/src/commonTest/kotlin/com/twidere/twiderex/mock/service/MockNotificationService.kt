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
import com.twidere.services.microblog.NotificationService
import com.twidere.services.microblog.model.INotification
import com.twidere.twiderex.mock.model.mockINotification
import com.twidere.twiderex.mock.model.toIPaging
import kotlinx.coroutines.delay
import org.jetbrains.annotations.TestOnly

internal class MockNotificationService @TestOnly constructor() : NotificationService, ErrorService(), MicroBlogService {
    override suspend fun notificationTimeline(
        count: Int,
        since_id: String?,
        max_id: String?
    ): List<INotification> {
        checkError()
        val list = mutableListOf<INotification>()
        for (i in 0 until count) {
            delay(1)
            list.add(mockINotification())
        }
        return list.reversed().toIPaging()
    }
}
