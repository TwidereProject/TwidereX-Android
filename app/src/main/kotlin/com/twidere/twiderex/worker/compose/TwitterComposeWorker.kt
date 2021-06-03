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
package com.twidere.twiderex.worker.compose

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.hasKeyWithValueOfType
import com.twidere.services.twitter.TwitterService
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.mapper.toDbStatusWithReference
import com.twidere.twiderex.db.model.saveToDb
import com.twidere.twiderex.model.ComposeData
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.toWorkData
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiStatus.Companion.toUi
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.StatusRepository
import com.twidere.twiderex.viewmodel.compose.ComposeType
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class TwitterComposeWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    accountRepository: AccountRepository,
    notificationManagerCompat: NotificationManagerCompat,
    private val statusRepository: StatusRepository,
    private val contentResolver: ContentResolver,
    private val cacheDatabase: CacheDatabase,
) : ComposeWorker<TwitterService>(
    context,
    workerParams,
    accountRepository,
    notificationManagerCompat
) {
    companion object {
        fun create(
            accountKey: MicroBlogKey,
            data: ComposeData,
        ) = OneTimeWorkRequestBuilder<TwitterComposeWorker>()
            .setInputData(
                Data.Builder()
                    .putAll(data.toWorkData())
                    .putString("accountKey", accountKey.toString())
                    .let { data.lat?.let { it1 -> it.putDouble("lat", it1) } ?: it }
                    .let { data.long?.let { it1 -> it.putDouble("long", it1) } ?: it }
                    .build()
            )
            .build()
    }

    override suspend fun compose(
        service: TwitterService,
        composeData: ComposeData,
        mediaIds: ArrayList<String>
    ): UiStatus {
        val accountKey = inputData.getString("accountKey")?.let {
            MicroBlogKey.valueOf(it)
        } ?: throw Error()
        val lat = inputData.takeIf {
            it.hasKeyWithValueOfType<Double>("lat")
        }?.getDouble("lat", 0.0)
        val long = inputData.takeIf {
            it.hasKeyWithValueOfType<Double>("long")
        }?.getDouble("long", 0.0)
        val content = composeData.content.let {
            if (composeData.composeType == ComposeType.Quote && composeData.statusKey != null) {
                val status = statusRepository.loadFromCache(
                    composeData.statusKey,
                    accountKey = accountKey
                )
                it + " ${status?.generateShareLink()}"
            } else {
                it
            }
        }
        val result = service.update(
            content,
            media_ids = mediaIds,
            in_reply_to_status_id = if (composeData.composeType == ComposeType.Reply || composeData.composeType == ComposeType.Thread) composeData.statusKey?.id else null,
            repost_status_id = if (composeData.composeType == ComposeType.Quote) composeData.statusKey?.id else null,
            lat = lat,
            long = long,
            exclude_reply_user_ids = composeData.excludedReplyUserIds
        ).toDbStatusWithReference(accountKey)
        listOf(result).saveToDb(cacheDatabase)
        return result.toUi(accountKey)
    }

    override suspend fun uploadImage(
        originUri: Uri,
        scramblerUri: Uri,
        service: TwitterService
    ): String {
        val type = contentResolver.getType(originUri)
        val size = contentResolver.openFileDescriptor(scramblerUri, "r")?.statSize
        return contentResolver.openInputStream(scramblerUri)?.use {
            service.uploadFile(
                it,
                type ?: "image/*",
                size ?: it.available().toLong()
            )
        } ?: throw Error()
    }
}
