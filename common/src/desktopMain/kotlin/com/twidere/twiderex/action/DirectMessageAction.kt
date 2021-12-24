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
package com.twidere.twiderex.action

import com.twidere.twiderex.extensions.launchCatching
import com.twidere.twiderex.jobs.dm.DirectMessageDeleteJob
import com.twidere.twiderex.jobs.dm.TwitterDirectMessageSendJob
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.job.DirectMessageDeleteData
import com.twidere.twiderex.model.job.DirectMessageSendData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

actual class DirectMessageAction(
    private val twitterDirectMessageSendJob: TwitterDirectMessageSendJob,
    private val directMessageDeleteJob: DirectMessageDeleteJob,
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    actual fun send(
        platformType: PlatformType,
        data: DirectMessageSendData,
    ) {
        scope.launchCatching {
            if (platformType == PlatformType.Twitter) {
                twitterDirectMessageSendJob.execute(data, data.accountKey)
            }
        }
    }

    actual fun delete(
        data: DirectMessageDeleteData
    ) {
        scope.launchCatching {
            directMessageDeleteJob.execute(data, data.accountKey)
        }
    }
}
