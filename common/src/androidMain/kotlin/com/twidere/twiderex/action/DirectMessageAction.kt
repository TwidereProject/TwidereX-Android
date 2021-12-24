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

import androidx.work.WorkManager
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.job.DirectMessageDeleteData
import com.twidere.twiderex.model.job.DirectMessageSendData
import com.twidere.twiderex.worker.dm.DirectMessageDeleteWorker
import com.twidere.twiderex.worker.dm.TwitterDirectMessageSendWorker

actual class DirectMessageAction(
    private val workManager: WorkManager,
) {
    actual fun send(
        platformType: PlatformType,
        data: DirectMessageSendData,
    ) {
        if (platformType == PlatformType.Twitter) {
            val worker = TwitterDirectMessageSendWorker.create(
                data = data,
            )
            workManager
                .beginWith(worker)
                .enqueue()
        }
    }

    actual fun delete(
        data: DirectMessageDeleteData
    ) {
        val worker = DirectMessageDeleteWorker.createWorker(deleteData = data)
        workManager
            .beginWith(worker)
            .enqueue()
    }
}
