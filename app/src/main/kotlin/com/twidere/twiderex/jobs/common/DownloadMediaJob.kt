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
package com.twidere.twiderex.jobs.common

import com.twidere.services.microblog.DownloadMediaService
import com.twidere.twiderex.R
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.AccountRepository
import java.io.OutputStream

class DownloadMediaJob(
    private val accountRepository: AccountRepository,
    private val inAppNotification: InAppNotification,
) {
    suspend fun execute(
        target: String,
        source: String,
        accountKey: MicroBlogKey,
        openOutputStream: (target: String) -> OutputStream?
    ): Boolean {
        val accountDetails = accountKey.let {
            accountRepository.findByAccountKey(accountKey = it)
        }?.let {
            accountRepository.getAccountDetails(it)
        } ?: return false
        val service = accountDetails.service
        if (service !is DownloadMediaService) {
            return false
        }
        openOutputStream(target)?.use {
            service.download(target = source).copyTo(it)
        } ?: return false

        inAppNotification.show(R.string.common_controls_actions_save)
        return true
    }
}
