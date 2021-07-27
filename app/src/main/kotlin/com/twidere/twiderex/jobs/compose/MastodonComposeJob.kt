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
package com.twidere.twiderex.jobs.compose

import android.content.Context
import com.twidere.services.mastodon.MastodonService
import com.twidere.services.mastodon.model.PostPoll
import com.twidere.services.mastodon.model.PostStatus
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.mapper.toDbStatusWithReference
import com.twidere.twiderex.db.model.saveToDb
import com.twidere.twiderex.kmp.ExifScrambler
import com.twidere.twiderex.kmp.FileResolver
import com.twidere.twiderex.kmp.RemoteNavigator
import com.twidere.twiderex.model.ComposeData
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiStatus.Companion.toUi
import com.twidere.twiderex.notification.AppNotificationManager
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.viewmodel.compose.ComposeType
import java.io.File
import java.net.URI

class MastodonComposeJob(
    context: Context,
    accountRepository: AccountRepository,
    notificationManager: AppNotificationManager,
    exifScrambler: ExifScrambler,
    remoteNavigator: RemoteNavigator,
    private val fileResolver: FileResolver,
    private val cacheDatabase: CacheDatabase,
) : ComposeJob<MastodonService>(
    context,
    accountRepository,
    notificationManager,
    exifScrambler,
    remoteNavigator
) {
    override suspend fun compose(
        service: MastodonService,
        composeData: ComposeData,
        accountKey: MicroBlogKey,
        mediaIds: ArrayList<String>
    ): UiStatus {
        val result = service.compose(
            PostStatus(
                status = composeData.content,
                inReplyToID = if (composeData.composeType == ComposeType.Reply || composeData.composeType == ComposeType.Thread) composeData.statusKey?.id else null,
                mediaIDS = mediaIds,
                sensitive = composeData.isSensitive,
                spoilerText = composeData.contentWarningText,
                visibility = composeData.visibility,
                poll = composeData.voteOptions?.let {
                    PostPoll(
                        options = composeData.voteOptions,
                        expiresIn = composeData.voteExpired?.value,
                        multiple = composeData.voteMultiple
                    )
                }
            )
        ).toDbStatusWithReference(accountKey)
        listOf(result).saveToDb(cacheDatabase)
        return result.toUi(accountKey)
    }

    override suspend fun uploadImage(
        originUri: String,
        scramblerUri: String,
        service: MastodonService
    ): String? {
        val id = fileResolver.openInputStream(scramblerUri)?.use { input ->
            service.upload(
                input,
                URI.create(originUri).path?.let { File(it).name }?.takeIf { it.isNotEmpty() } ?: "file"
            )
        } ?: throw Error()
        return id.id
    }
}
