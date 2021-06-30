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
package com.twidere.twiderex.worker.dm

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.twidere.twiderex.R
import com.twidere.twiderex.model.ui.UiDMConversationWithLatestMessage
import com.twidere.twiderex.navigation.Route
import com.twidere.twiderex.notification.NotificationChannelSpec
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.DirectMessageRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class DirectMessageFetchWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: DirectMessageRepository,
    private val accountRepository: AccountRepository,
    private val notificationManagerCompat: NotificationManagerCompat,
) : CoroutineWorker(
    context,
    workerParams
) {
    companion object {
        fun createRepeatableWorker() = PeriodicWorkRequestBuilder<DirectMessageFetchWorker>(15, TimeUnit.MINUTES)
            .build()
    }

    override suspend fun doWork(): Result {
        return try {
            accountRepository.activeAccount.value?.let { account ->
                val result = repository.checkNewMessages(account)
                // TODO DM localize
                result.forEach {
                    notification(it)
                }
            } ?: throw Error()
            Result.success()
        } catch (e: Throwable) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun notification(it: UiDMConversationWithLatestMessage) {
        val intent =
            Intent(Intent.ACTION_VIEW, Uri.parse(Route.DeepLink.Conversation(it.conversation.conversationKey)))
        val pendingIntent =
            PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_MUTABLE
            )
        // TODO DM localize
        val builder = NotificationCompat
            .Builder(applicationContext, NotificationChannelSpec.BackgroundProgresses.id)
            .setContentTitle("New direct message")
            .setSmallIcon(R.drawable.ic_notification)
            .setCategory(NotificationCompat.CATEGORY_SOCIAL)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(false)
            .setSilent(false)
            .setAutoCancel(true)
            .setContentText("${it.latestMessage.sender.displayName} send you a message")
            .setContentIntent(pendingIntent)
        notificationManagerCompat.notify(it.latestMessage.messageKey.hashCode(), builder.build())
    }
}
