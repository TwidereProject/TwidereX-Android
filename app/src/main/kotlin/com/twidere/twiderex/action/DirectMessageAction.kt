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
package com.twidere.twiderex.action

import androidx.work.WorkManager
import com.twidere.twiderex.model.DirectMessageDeleteData
import com.twidere.twiderex.model.DirectMessageSendData
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.worker.dm.DirectMessageDeleteWorker
import com.twidere.twiderex.worker.dm.TwitterDirectMessageSendWorker

class DirectMessageAction(
    private val workManager: WorkManager,
) {
    fun send(
        platformType: PlatformType,
        data: DirectMessageSendData,
    ) {
        val worker = when (platformType) {
            PlatformType.Twitter -> TwitterDirectMessageSendWorker.create(
                data = data,
            )
            PlatformType.StatusNet -> TODO()
            PlatformType.Fanfou -> TODO()
            PlatformType.Mastodon -> TODO()
        }
        workManager
            .beginWith(worker)
            .enqueue()
    }

    fun delete(
        data: DirectMessageDeleteData
    ) {
        val worker = DirectMessageDeleteWorker.createWorker(deleteData = data)
        workManager
            .beginWith(worker)
            .enqueue()
    }
}
