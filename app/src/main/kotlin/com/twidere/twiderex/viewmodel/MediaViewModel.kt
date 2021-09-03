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

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.twidere.services.microblog.LookupService
import com.twidere.twiderex.R
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.StatusRepository
import com.twidere.twiderex.utils.FileProviderHelper
import com.twidere.twiderex.utils.notify
import com.twidere.twiderex.worker.DownloadMediaWorker
import com.twidere.twiderex.worker.ShareMediaWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MediaViewModel @AssistedInject constructor(
    private val repository: StatusRepository,
    private val inAppNotification: InAppNotification,
    private val workManager: WorkManager,
    @Assisted private val account: AccountDetails,
    @Assisted private val statusKey: MicroBlogKey,
) : ViewModel() {

    fun saveFile(currentMedia: UiMedia, target: Uri) {
        currentMedia.mediaUrl?.let {
            DownloadMediaWorker.create(
                accountKey = account.accountKey,
                source = it,
                target = target
            )
        }?.let {
            workManager.enqueue(it)
        }
    }

    fun shareMedia(currentMedia: UiMedia, target: String, context: Context) {
        val uri = FileProviderHelper.getUriFromShares(target, context)
        inAppNotification.show(R.string.common_alerts_media_sharing_title)
        currentMedia.mediaUrl?.let {
            DownloadMediaWorker.create(
                accountKey = account.accountKey,
                source = it,
                target = uri
            )
        }?.let {
            workManager.beginWith(it)
                .then(
                    ShareMediaWorker.create(
                        target = uri
                    )
                ).enqueue()
        }
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(account: AccountDetails, statusKey: MicroBlogKey): MediaViewModel
    }

    val loading = MutableStateFlow(false)
    val status by lazy {
        repository.loadStatus(
            statusKey = statusKey,
            accountKey = account.accountKey,
        )
    }

    init {
        viewModelScope.launch {
            try {
                repository.loadTweetFromNetwork(
                    statusKey.id,
                    account.accountKey,
                    account.service as LookupService
                )
            } catch (e: Throwable) {
                e.notify(inAppNotification)
            }
        }
    }
}
