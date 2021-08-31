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
package com.twidere.twiderex.viewmodel

import android.net.Uri
import com.twidere.twiderex.action.MediaAction
import com.twidere.twiderex.extensions.asStateIn
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.StatusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class MediaViewModel(
    private val repository: StatusRepository,
    private val accountRepository: AccountRepository,
    private val mediaAction: MediaAction,
    private val statusKey: MicroBlogKey,
) : ViewModel() {
    private val account by lazy {
        accountRepository.activeAccount.asStateIn(viewModelScope, null)
    }

    fun saveFile(currentMedia: UiMedia, target: Uri) = viewModelScope.launch {
        val account = account.lastOrNull() ?: return@launch
        currentMedia.mediaUrl?.let {
            mediaAction.download(
                accountKey = account.accountKey,
                source = it,
                target = target.toString()
            )
        }
    }

    fun shareMedia(currentMedia: UiMedia) = viewModelScope.launch {
        val account = account.lastOrNull() ?: return@launch
        currentMedia.mediaUrl?.let {
            mediaAction.share(
                source = it,
                accountKey = account.accountKey
            )
        }
    }

    val loading = MutableStateFlow(false)
    val status by lazy {
        account.flatMapLatest {
            if (it != null) {
                repository.loadStatus(
                    statusKey = statusKey,
                    accountKey = it.accountKey,
                )
            } else {
                emptyFlow()
            }
        }
    }

    // init {
    //     viewModelScope.launch {
    //         try {
    //             repository.loadTweetFromNetwork(
    //                 statusKey.id,
    //                 account.accountKey,
    //                 account.service as LookupService
    //             )
    //         } catch (e: Throwable) {
    //             e.notify(inAppNotification)
    //         }
    //     }
    // }
}