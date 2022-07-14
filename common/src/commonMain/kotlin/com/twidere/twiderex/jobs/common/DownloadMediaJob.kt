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
package com.twidere.twiderex.jobs.common

import com.twidere.services.microblog.DownloadMediaService
import com.twidere.twiderex.kmp.FileResolver
import com.twidere.twiderex.kmp.ResLoader
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.notification.StringNotificationEvent.Companion.show
import com.twidere.twiderex.repository.AccountRepository

class DownloadMediaJob(
    private val accountRepository: AccountRepository,
    private val inAppNotification: InAppNotification,
    private val fileResolver: FileResolver,
    private val resLoader: ResLoader,
) {
    suspend fun execute(
        target: String,
        source: String,
        accountKey: MicroBlogKey,
    ) {
        val accountDetails = accountKey.let {
            accountRepository.findByAccountKey(accountKey = it)
        } ?: throw Error("Can't find any account matches:$$accountKey")
        val service = accountDetails.service
        if (service !is DownloadMediaService) {
            throw Error("Service must be DownloadMediaService")
        }
        fileResolver.openOutputStream(target)?.use {
            service.download(target = source).copyTo(it)
        } ?: throw Error("Download failed")
        inAppNotification.show(resLoader.getString(com.twidere.twiderex.MR.strings.common_controls_actions_save))
    }
}
