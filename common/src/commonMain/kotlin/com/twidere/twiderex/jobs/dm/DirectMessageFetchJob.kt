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
package com.twidere.twiderex.jobs.dm

import com.twidere.services.microblog.DirectMessageService
import com.twidere.services.microblog.LookupService
import com.twidere.twiderex.kmp.ResLoader
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.ui.UiDMConversationWithLatestMessage
import com.twidere.twiderex.navigation.RootDeepLinks
import com.twidere.twiderex.notification.AppNotification
import com.twidere.twiderex.notification.AppNotificationManager
import com.twidere.twiderex.notification.NotificationChannelSpec
import com.twidere.twiderex.notification.notificationChannelId
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.DirectMessageRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class DirectMessageFetchJob(
    private val repository: DirectMessageRepository,
    private val accountRepository: AccountRepository,
    private val notificationManager: AppNotificationManager,
    private val resLoader: ResLoader,
) {
    suspend fun execute() {
        accountRepository.activeAccount.firstOrNull()?.takeIf {
            accountRepository.getAccountPreferences(it.accountKey).isNotificationEnabled.first()
        }?.let { account ->
            val result = repository.checkNewMessages(
                accountKey = account.accountKey,
                service = account.service as DirectMessageService,
                lookupService = account.service as LookupService
            )
            result.forEach {
                notification(account = account, message = it)
            }
        }
    }

    private fun notification(account: AccountDetails, message: UiDMConversationWithLatestMessage) {
        val builder = AppNotification
            .Builder(
                account.accountKey.notificationChannelId(
                    NotificationChannelSpec.ContentMessages.id
                )
            )
            .setContentTitle(resLoader.getString(com.twidere.twiderex.MR.strings.common_notification_messages_title))
            .setContentText(
                resLoader.getString(
                    com.twidere.twiderex.MR.strings.common_notification_messages_content,
                    message.latestMessage.sender.displayName
                )
            )
            .setDeepLink(RootDeepLinks.Conversation(message.conversation.conversationKey))
        notificationManager.notify(message.latestMessage.messageKey.hashCode(), builder.build())
    }
}
