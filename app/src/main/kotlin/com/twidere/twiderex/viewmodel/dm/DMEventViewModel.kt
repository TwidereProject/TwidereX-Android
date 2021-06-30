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
package com.twidere.twiderex.viewmodel.dm

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.twidere.twiderex.action.DirectMessageAction
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.DirectMessageSendData
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.model.ui.UiDMEvent
import com.twidere.twiderex.repository.DirectMessageRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.UUID

class DMEventViewModel @AssistedInject constructor(
    private val repository: DirectMessageRepository,
    private val sendAction: DirectMessageAction,
    @Assisted private val account: AccountDetails,
    @Assisted private val conversationKey: MicroBlogKey,
) : ViewModel() {

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(account: AccountDetails, conversationKey: MicroBlogKey): DMEventViewModel
    }

    val conversation by lazy {
        liveData {
            emitSource(repository.dmConversation(account, conversationKey))
        }
    }

    val source by lazy {
        repository.dmEventListSource(account, conversationKey).cachedIn(viewModelScope)
    }

    // input
    val input = MutableLiveData("")
    val inputImage = MutableLiveData<Uri?>()
    val firstEventKey = MutableLiveData<String>(null)

    fun sendMessage() {
        if (input.value.isNullOrEmpty() && inputImage.value == null) return
        conversation.value?.let {
            sendAction.send(
                account.type,
                data = DirectMessageSendData(
                    text = input.value,
                    images = inputImage.value?.toString()?.let { uri -> listOf(uri) } ?: emptyList(),
                    recipientUserKey = it.recipientKey,
                    draftMessageKey = when (account.type) {
                        PlatformType.Twitter -> MicroBlogKey.twitter(UUID.randomUUID().toString())
                        PlatformType.StatusNet -> TODO()
                        PlatformType.Fanfou -> TODO()
                        PlatformType.Mastodon -> TODO()
                    },
                    conversationKey = it.conversationKey,
                    accountKey = account.accountKey
                )
            )
            input.postValue("")
            inputImage.postValue(null)
        }
    }

    fun sendDraftMessage(event: UiDMEvent) {
        sendAction.send(
            account.type,
            data = DirectMessageSendData(
                text = event.originText,
                images = event.media.mapNotNull { it.url },
                recipientUserKey = event.recipientAccountKey,
                conversationKey = event.conversationKey,
                accountKey = account.accountKey,
                draftMessageKey = event.messageKey
            )
        )
    }
}
