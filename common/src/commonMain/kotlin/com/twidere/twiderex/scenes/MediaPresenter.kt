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
package com.twidere.twiderex.scenes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import com.twidere.twiderex.action.MediaAction
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.repository.StatusRepository
import kotlinx.coroutines.flow.Flow
import moe.tlaster.kfilepicker.FilePicker

@Composable
internal fun MediaPresenter(
  event: Flow<MediaEvent>,
  statusKey: MicroBlogKey,
  userKey: MicroBlogKey?,
  statusRepository: StatusRepository = get(),
  mediaAction: MediaAction = get(),
): MediaState {
  val accountState = CurrentAccountPresenter()
  if (accountState !is CurrentAccountState.Account) {
    return MediaState.Loading
  }
  val account = accountState.account

  LaunchedEffect(Unit) {
    event.collect { event ->
      when (event) {
        is MediaEvent.SaveMedia -> {
          val fileName = event.currentMedia.fileName ?: return@collect
          val path = FilePicker.createFile(fileName)?.path ?: return@collect
          event.currentMedia.mediaUrl?.let {
            mediaAction.download(
              accountKey = account.accountKey,
              source = it,
              target = path
            )
          }
        }
        is MediaEvent.ShareMedia -> {
          val fileName = event.currentMedia.fileName ?: return@collect
          val mediaUrl = event.currentMedia.mediaUrl ?: return@collect
          mediaAction.share(
            source = mediaUrl,
            fileName = fileName,
            accountKey = account.accountKey,
            extraText = event.extraText()
          )
        }
      }
    }
  }
  val status by produceState<MediaState>(
    initialValue = MediaState.Loading,
  ) {
    statusRepository.loadStatus(
      statusKey = statusKey,
      accountKey = account.accountKey,
    ).collect { status ->
      if (status != null) {
        value = MediaState.Data(status)
      }
    }
  }
  return status
}

internal sealed interface MediaEvent {
  data class SaveMedia(
    val currentMedia: UiMedia,
  ) : MediaEvent

  data class ShareMedia(
    val currentMedia: UiMedia,
    val extraText: () -> String,
  ) : MediaEvent
}

internal sealed interface MediaState {
  object Loading : MediaState

  @Immutable
  data class Data(
    val status: UiStatus
  ) : MediaState
}
