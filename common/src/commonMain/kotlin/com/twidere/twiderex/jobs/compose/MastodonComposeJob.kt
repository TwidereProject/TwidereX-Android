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
package com.twidere.twiderex.jobs.compose

import com.twidere.services.mastodon.MastodonService
import com.twidere.services.mastodon.model.PostPoll
import com.twidere.services.mastodon.model.PostStatus
import com.twidere.services.mastodon.model.Visibility
import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.kmp.ExifScrambler
import com.twidere.twiderex.kmp.FileResolver
import com.twidere.twiderex.kmp.RemoteNavigator
import com.twidere.twiderex.kmp.ResLoader
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.ComposeType
import com.twidere.twiderex.model.enums.MastodonVisibility
import com.twidere.twiderex.model.job.ComposeData
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.notification.AppNotificationManager
import com.twidere.twiderex.repository.AccountRepository
import java.io.File
import java.net.URI

class MastodonComposeJob(
    accountRepository: AccountRepository,
    notificationManager: AppNotificationManager,
    exifScrambler: ExifScrambler,
    remoteNavigator: RemoteNavigator,
    resLoader: ResLoader,
    private val fileResolver: FileResolver,
    private val cacheDatabase: CacheDatabase,
) : ComposeJob<MastodonService>(
    accountRepository,
    notificationManager,
    exifScrambler,
    remoteNavigator,
    resLoader,
) {
    override suspend fun compose(
        service: MastodonService,
        composeData: ComposeData,
        accountKey: MicroBlogKey,
        mediaIds: ArrayList<String>
    ): UiStatus {
        return service.compose(
            PostStatus(
                status = composeData.content,
                inReplyToID = if (composeData.composeType == ComposeType.Reply || composeData.composeType == ComposeType.Thread) composeData.statusKey?.id else null,
                mediaIDS = mediaIds,
                sensitive = composeData.isSensitive,
                spoilerText = composeData.contentWarningText,
                visibility = when (composeData.visibility) {
                    MastodonVisibility.Public, null -> Visibility.Public
                    MastodonVisibility.Unlisted -> Visibility.Unlisted
                    MastodonVisibility.Private -> Visibility.Private
                    MastodonVisibility.Direct -> Visibility.Direct
                },
                poll = composeData.voteOptions?.let {
                    PostPoll(
                        options = composeData.voteOptions,
                        expiresIn = composeData.voteExpired?.value,
                        multiple = composeData.voteMultiple
                    )
                }
            )
        ).toUi(accountKey).also {
            cacheDatabase.statusDao().insertAll(listOf = listOf(it), accountKey = accountKey)
        }
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

    override val imageMaxSize: Long
        get() = 100 * 1024 * 1024
}
