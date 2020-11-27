/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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
package com.twidere.twiderex.utils

import android.content.Context
import android.net.Uri
import com.twidere.services.twitter.TwitterService
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.repository.DraftRepository
import com.twidere.twiderex.scenes.ComposeType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.UUID

class ComposeQueue(
    private val context: Context,
    private val repository: DraftRepository,
) {
    fun commit(
        service: TwitterService,
        content: String,
        images: List<Uri>,
        composeType: ComposeType = ComposeType.New,
        statusKey: MicroBlogKey? = null,
        lat: Double? = null,
        long: Double? = null,
        draftId: String = UUID.randomUUID().toString(),
        excludedReplyUserIds: List<String>? = null,
    ) {
        GlobalScope.launch {
            runCatching {
                val mediaIds = arrayListOf<String>()
                images.forEach { uri ->
                    val contentResolver = context.contentResolver
                    val type = contentResolver.getType(uri)
                    val size = contentResolver.openFileDescriptor(uri, "r")?.statSize
                    val id = contentResolver.openInputStream(uri)?.use {
                        service.uploadFile(it, type ?: "image/*", size ?: it.available().toLong())
                    } ?: throw Error()
                    mediaIds.add(id)
                }
                service.update(
                    content,
                    media_ids = mediaIds,
                    in_reply_to_status_id = if (composeType == ComposeType.Reply) statusKey?.id else null,
                    repost_status_id = if (composeType == ComposeType.Quote) statusKey?.id else null,
                    lat = lat,
                    long = long,
                    exclude_reply_user_ids = excludedReplyUserIds
                )
            }.onSuccess {
                repository.remove(draftId)
            }.onFailure {
                repository.addOrUpgrade(
                    content,
                    images.map { it.toString() },
                    composeType = composeType,
                    statusKey = statusKey,
                    draftId = draftId,
                    excludedReplyUserIds = excludedReplyUserIds,
                )
            }
        }
    }
}
