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
package com.twidere.twiderex.worker.status

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.twidere.services.http.MicroBlogException
import com.twidere.services.microblog.StatusService
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.StatusRepository
import com.twidere.twiderex.utils.notify

class DeleteStatusWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val accountRepository: AccountRepository,
    private val statusRepository: StatusRepository,
    private val inAppNotification: InAppNotification,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val accountKey = inputData.getString("accountKey")?.let {
            MicroBlogKey.valueOf(it)
        } ?: return Result.failure()
        val status = inputData.getString("statusKey")?.let {
            MicroBlogKey.valueOf(it)
        }?.let {
            statusRepository.loadFromCache(it, accountKey = accountKey)
        } ?: return Result.failure()
        val service = accountRepository.findByAccountKey(accountKey)?.let {
            accountRepository.getAccountDetails(it)
        }?.let {
            it.service as? StatusService
        } ?: return Result.failure()
        return try {
            service.delete(status.statusId)
            Result.success()
        } catch (e: MicroBlogException) {
            e.notify(inAppNotification)
            Result.failure()
        } catch (e: Throwable) {
            e.notify(inAppNotification)
            Result.failure()
        }
    }
}
