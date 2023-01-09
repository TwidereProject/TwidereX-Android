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
package com.twidere.twiderex.viewmodel.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.twidere.services.microblog.LookupService
import com.twidere.twiderex.action.ComposeAction
import com.twidere.twiderex.action.DraftAction
import com.twidere.twiderex.component.media.MediaLibraryType
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.extensions.getTextAfterSelection
import com.twidere.twiderex.extensions.getTextBeforeSelection
import com.twidere.twiderex.kmp.LocationProvider
import com.twidere.twiderex.kmp.MediaInsertProvider
import com.twidere.twiderex.kmp.Platform
import com.twidere.twiderex.kmp.currentPlatform
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.ComposeType
import com.twidere.twiderex.model.enums.MastodonVisibility
import com.twidere.twiderex.model.enums.MediaInsertType
import com.twidere.twiderex.model.enums.MediaType
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.job.ComposeData
import com.twidere.twiderex.model.kmp.Location
import com.twidere.twiderex.model.ui.UiDraft
import com.twidere.twiderex.model.ui.UiEmoji
import com.twidere.twiderex.model.ui.UiEmojiCategory
import com.twidere.twiderex.model.ui.UiMediaInsert
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.DraftRepository
import com.twidere.twiderex.repository.StatusRepository
import com.twidere.twiderex.repository.UserRepository
import com.twidere.twiderex.scenes.CurrentAccountPresenter
import com.twidere.twiderex.scenes.CurrentAccountState
import com.twidere.twiderex.utils.notifyError
import com.twitter.twittertext.Extractor
import com.twitter.twittertext.TwitterTextConfiguration
import com.twitter.twittertext.TwitterTextParser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import java.util.UUID

class VoteOption {
  val text = MutableStateFlow("")
  fun setText(value: String) {
    text.value = value
  }
}

enum class VoteExpired(val value: Long) {
  Min_5(300),
  Min_30(1800),
  Hour_1(3600),
  Hour_6(21600),
  Day_1(86400),
  Day_3(259200),
  Day_7(604800),
}

class VoteState {
  val options = MutableStateFlow(arrayListOf(VoteOption(), VoteOption()))
  val expired = MutableStateFlow(VoteExpired.Day_1)
  val multiple = MutableStateFlow(false)

  fun setMultiple(value: Boolean) {
    multiple.value = value
  }

  fun setExpired(value: VoteExpired) {
    expired.value = value
  }

  fun setOption(value: String, index: Int) {
    options.value.let {
      it[index].setText(value)
      if (index == it.lastIndex && it.size < 4 && value.isNotEmpty()) {
        it.add(VoteOption())
      } else if (value.isEmpty() && it.size > 2) {
        it.removeAt(index)
      }
      options.value = it
    }
  }
}

@Composable
fun ComposePresenter(
  event: Flow<ComposeEvent>,
  draftRepository: DraftRepository = get(),
  composeAction: ComposeAction = get(),
  repository: StatusRepository = get(),
  userRepository: UserRepository = get(),
  draftAction: DraftAction = get(),
  mediaInsertProvider: MediaInsertProvider = get(),
  inAppNotification: InAppNotification = get(),
  locationProvider: LocationProvider = get(),
  type: ComposeType? = null,
  statusKey: MicroBlogKey? = null,
  id: String? = null,
): ComposeState {
  val accountState = CurrentAccountPresenter()
  if (accountState !is CurrentAccountState.Account) {
    return ComposeState.NoAccount
  }
  var composeType by remember {
    mutableStateOf(type)
  }
  var draft by remember {
    mutableStateOf<UiDraft?>(null)
  }
  LaunchedEffect(accountState) {
    id?.let {
      draft = draftRepository.get(id)?.apply {
        composeType = this.composeType
      }
    }
  }
  if (composeType == null) {
    return ComposeState.NoAccount
  }
  val draftId: String = remember(id) {
    id ?: UUID.randomUUID().toString()
  }
  val emojis = remember {
    mutableStateListOf<UiEmojiCategory>()
  }
  val location by locationProvider.location.collectAsState(null)
  val excludedReplyUserIds = remember {
    mutableStateListOf<String>()
  }
  var status: UiStatus? by remember {
    mutableStateOf(null)
  }
  val replyToUserName = remember {
    mutableStateListOf<String>()
  }
  var loadingReplyUser by remember {
    mutableStateOf(false)
  }
  val replyToUser = remember {
    mutableStateListOf<UiUser>()
  }
  var voteState by remember {
    mutableStateOf<VoteState?>(null)
  }
  var isInVoteMode by remember {
    mutableStateOf(false)
  }
  var visibility by remember {
    mutableStateOf(MastodonVisibility.Public)
  }
  var isImageSensitive by remember {
    mutableStateOf(false)
  }
  var isContentWarningEnabled by remember {
    mutableStateOf(false)
  }
  var contentWarningTextFieldValue by remember {
    mutableStateOf(TextFieldValue())
  }
  var textFieldValue by remember {
    mutableStateOf(TextFieldValue())
  }
  val parsedTextLength = remember(textFieldValue) {
    TwitterTextParser.parseTweet(textFieldValue.text).weightedLength
  }
  val draftCount by draftRepository.sourceCount.collectAsState(0)
  val maxContentLength = remember(accountState) {
    when (accountState.account.type) {
      PlatformType.Twitter ->
        TwitterTextConfiguration.getDefaultConfig().maxWeightedTweetLength - (
          status?.generateShareLink()?.let {
            it.length + 1 // for space
          } ?: 0
          )
      PlatformType.StatusNet -> TODO()
      PlatformType.Fanfou -> TODO()
      PlatformType.Mastodon -> 500
    }
  }
  val images = remember {
    mutableStateListOf<UiMediaInsert>()
  }
  var enableThreadMode by remember {
    mutableStateOf(composeType == ComposeType.Thread)
  }
  val imageLimit = remember(accountState) {
    when (accountState.account.type) {
      PlatformType.Twitter -> 4
      PlatformType.StatusNet -> TODO()
      PlatformType.Fanfou -> TODO()
      PlatformType.Mastodon -> 4
    }
  }
  var mediaInsertMode by remember {
    mutableStateOf(MediaInsertMode.All)
  }
  val canSend = remember(textFieldValue, images, maxContentLength, parsedTextLength) {
    (textFieldValue.text.isNotEmpty() || !images.isEmpty()) &&
      parsedTextLength <= maxContentLength
  }
  val canSaveDraft = remember(textFieldValue, images) {
    textFieldValue.text.isNotEmpty() || !images.isEmpty()
  }

  val allowImage by remember {
    derivedStateOf {
      accountState.account.type == PlatformType.Twitter ||
        (accountState.account.type == PlatformType.Mastodon && !isInVoteMode)
    }
  }

  val allowVote by remember {
    derivedStateOf {
      accountState.account.type == PlatformType.Mastodon && !images.any()
    }
  }

  val allowLocation by remember {
    derivedStateOf {
      accountState.account.type != PlatformType.Mastodon && currentPlatform == Platform.Android
    }
  }

  val locationEnabled by locationProvider.locationEnabled.collectAsState(false)

  fun insertText(result: String) {
    textFieldValue = textFieldValue.copy(
      text = textFieldValue.getTextBeforeSelection() +
        result +
        textFieldValue.getTextAfterSelection(),
      selection = TextRange(
        textFieldValue.selection.min + result.length
      )
    )
  }

  fun buildComposeData(text: String) = ComposeData(
    content = text,
    draftId = draftId,
    images = images.map { it.filePath },
    composeType = composeType!!,
    statusKey = statusKey,
    lat = location?.latitude,
    long = location?.longitude,
    excludedReplyUserIds = excludedReplyUserIds,
    voteOptions = voteState?.options?.value?.map { it.text.value },
    voteExpired = voteState?.expired?.value,
    voteMultiple = voteState?.multiple?.value,
    visibility = visibility,
    isSensitive = isImageSensitive,
    contentWarningText = contentWarningTextFieldValue.text,
    isThreadMode = enableThreadMode,
  )

  fun putImages(it: ComposeEvent.PutImages) {
    val allowType = images.firstOrNull()?.type
      ?: it.value.firstOrNull()?.type
      ?: MediaType.photo
    images.run {
      addAll(it.value)
      removeAll { media -> media.type != allowType }
      val limit = if (allowType == MediaType.photo) imageLimit else 1
      while (images.size > limit) { images.removeLast() }
    }
    mediaInsertMode = when (allowType) {
      MediaType.video, MediaType.animated_gif ->
        MediaInsertMode.Disabled

      else -> {
        if (images.size == imageLimit) {
          MediaInsertMode.Disabled
        } else {
          MediaInsertMode.ImageOnly
        }
      }
    }
  }

  LaunchedEffect(draft) {
    draft?.let {
      textFieldValue = TextFieldValue(it.content)
      it.media.map {
        mediaInsertProvider.provideUiMediaInsert(it)
      }.let {
        putImages(ComposeEvent.PutImages(value = it))
      }
      it.excludedReplyUserIds?.let { ids ->
        excludedReplyUserIds.addAll(ids)
      }
    }
  }
  LaunchedEffect(accountState) {
    if (statusKey != null) {
      repository.loadStatus(
        statusKey,
        accountKey = accountState.account.accountKey
      ).map { status ->
        if (
          status != null &&
          textFieldValue.text.isEmpty() &&
          status.platformType == PlatformType.Mastodon &&
          status.mastodonExtra?.mentions != null &&
          composeType == ComposeType.Reply
        ) {
          val mentions =
            status.mastodonExtra.mentions.mapNotNull {
              it.acct
            }.filter {
              it != accountState.account.user.screenName
            }.map {
              "@$it"
            }.let {
              if (status.user.userKey != accountState.account.user.userKey) {
                listOf(status.user.getDisplayScreenName(accountState.account.accountKey.host)) + it
              } else {
                it
              }
            }.distinctBy {
              it
            }.takeIf {
              it.any()
            }?.joinToString(" ", postfix = " ") {
              it
            }
          if (mentions != null) {
            textFieldValue = TextFieldValue(
              mentions,
              selection = TextRange(mentions.length)
            )
          }
        }
        status
      }.collectLatest {
        status = it
      }
    } else {
      status = null
    }
  }

  LaunchedEffect(accountState, status) {
    status?.let { status ->
      if (
        accountState.account.type == PlatformType.Twitter &&
        composeType == ComposeType.Reply &&
        statusKey != null
      ) {
        Extractor().extractMentionedScreennames(
          status.htmlText
        ).filter {
          it != accountState.account.user.screenName &&
            it != status.user.screenName
        }
      } else {
        emptyList()
      }
    }?.let {
      replyToUserName.clear()
      replyToUserName.addAll(it)
    }
  }

  LaunchedEffect(accountState, replyToUserName) {
    val list = if (replyToUserName.isNotEmpty()) {
      loadingReplyUser = true
      try {
        userRepository.lookupUsersByName(
          replyToUserName,
          accountKey = accountState.account.accountKey,
          lookupService = accountState.account.service as LookupService,
        )
      } catch (e: Throwable) {
        inAppNotification.notifyError(e)
        emptyList()
      } finally {
        loadingReplyUser = false
      }
    } else {
      emptyList()
    }
    replyToUser.clear()
    replyToUser.addAll(list)
  }

  LaunchedEffect(Unit) {
    event.collectLatest {
      when (it) {
        is ComposeEvent.SetText -> {
          textFieldValue = it.value
        }
        is ComposeEvent.ChangeVisibility -> {
          visibility = it.value
        }
        ComposeEvent.Compose -> {
          composeAction.commit(
            buildComposeData(textFieldValue.text)
          )
        }
        ComposeEvent.DisableLocation -> {
          locationProvider.disable()
        }
        is ComposeEvent.EnableSensitive -> {
          isImageSensitive = !isImageSensitive
        }
        is ComposeEvent.EnableThreadMode -> {
          enableThreadMode = it.value
        }
        is ComposeEvent.EnableWarning -> {
          isContentWarningEnabled = !isContentWarningEnabled
        }
        is ComposeEvent.ExcludeReplyUser -> {
          excludedReplyUserIds.add(it.user.id)
        }
        is ComposeEvent.IncludeReplyUser -> {
          excludedReplyUserIds.remove(it.user.id)
        }
        is ComposeEvent.InsertEmoji -> {
          val emojiText =
            "${if (textFieldValue.selection.start != 0) " " else ""}:${it.emoji.shortcode}: "
          insertText(emojiText)
        }
        is ComposeEvent.InsertText -> {
          insertText(it.result)
        }
        is ComposeEvent.PutImages -> {
          putImages(it)
        }
        is ComposeEvent.RemoveImage -> {
          images.apply {
            removeAll { media -> media.filePath == it.item }
            if (isEmpty()) {
              mediaInsertMode =
                MediaInsertMode.All
            } else if (first().type == MediaType.photo) {
              mediaInsertMode =
                MediaInsertMode.ImageOnly
            }
          }
        }
        ComposeEvent.SaveDraft -> {
          draftAction.save(
            buildComposeData(textFieldValue.text)
          )
        }
        ComposeEvent.TrackLocation -> {
          locationProvider.enable()
        }
        is ComposeEvent.VoteMode -> {
          voteState = if (it.value) {
            VoteState()
          } else {
            null
          }
          isInVoteMode = it.value
        }
        is ComposeEvent.WarmingText -> {
          contentWarningTextFieldValue = it.value
        }
      }
    }
  }

  DisposableEffect(Unit) {
    onDispose {
      locationProvider.disable()
    }
  }

  return ComposeState.Data(
    account = accountState.account,
    voteState = voteState,
    draftCount = draftCount,
    canSaveDraft = canSaveDraft,
    canSend = canSend,
    contentWarningTextFieldValue = contentWarningTextFieldValue,
    enableThreadMode = enableThreadMode,
    locationEnabled = locationEnabled,
    isContentWarning = isContentWarningEnabled,
    isImageSensitive = isImageSensitive,
    isContentWarningEnabled = isContentWarningEnabled,
    loadingReplyUser = loadingReplyUser,
    allowImage = allowImage,
    allowVote = allowVote,
    isInVoteMode = isInVoteMode,
    allowLocation = allowLocation,
    location = location,
    images = images,
    mediaInsertMode = mediaInsertMode,
    replyToUser = replyToUser,
    status = status,
    textFieldValue = textFieldValue,
    maxLength = maxContentLength,
    composeType = composeType,
    visibility = visibility,
    emojis = emojis,
    excludedReplyUserIds = excludedReplyUserIds,
    parsedTextLength = parsedTextLength,
  )
}

interface ComposeEvent {
  data class SetText(
    val value: TextFieldValue
  ) : ComposeEvent
  data class WarmingText(
    val value: TextFieldValue
  ) : ComposeEvent
  object EnableWarning : ComposeEvent
  object EnableSensitive : ComposeEvent
  data class EnableThreadMode(
    val value: Boolean
  ) : ComposeEvent
  data class ChangeVisibility(
    val value: MastodonVisibility
  ) : ComposeEvent
  data class VoteMode(
    val value: Boolean
  ) : ComposeEvent
  object Compose : ComposeEvent
  object SaveDraft : ComposeEvent
  data class PutImages(
    val value: List<UiMediaInsert>
  ) : ComposeEvent
  object TrackLocation : ComposeEvent
  object DisableLocation : ComposeEvent
  data class RemoveImage(
    val item: String
  ) : ComposeEvent
  data class ExcludeReplyUser(
    val user: UiUser
  ) : ComposeEvent
  data class IncludeReplyUser(
    val user: UiUser
  ) : ComposeEvent
  data class InsertText(
    val result: String
  ) : ComposeEvent
  data class InsertEmoji(
    val emoji: UiEmoji
  ) : ComposeEvent
}

interface ComposeState {
  data class Data(
    val account: AccountDetails,
    val voteState: VoteState?,
    val draftCount: Long,
    val canSaveDraft: Boolean,
    val canSend: Boolean,
    val enableThreadMode: Boolean,
    val locationEnabled: Boolean,
    val isContentWarning: Boolean,
    val isImageSensitive: Boolean,
    val isContentWarningEnabled: Boolean,
    val loadingReplyUser: Boolean,
    val allowImage: Boolean,
    val allowVote: Boolean,
    val isInVoteMode: Boolean,
    val allowLocation: Boolean,
    val location: Location?,
    val status: UiStatus?,
    val textFieldValue: TextFieldValue,
    val contentWarningTextFieldValue: TextFieldValue,
    val maxLength: Int,
    val composeType: ComposeType?,
    val mediaInsertMode: MediaInsertMode,
    val visibility: MastodonVisibility,
    val emojis: MutableList<UiEmojiCategory>,
    val images: MutableList<UiMediaInsert>,
    val excludedReplyUserIds: MutableList<String>,
    val replyToUser: List<UiUser>,
    val parsedTextLength: Int,
  ) : ComposeState
  object NoAccount : ComposeState
}

data class MediaInsertMode(
  val disabledInsertType: List<MediaInsertType>,
  val multiSelect: Boolean,
  val librarySupportedType: List<MediaLibraryType>
) {
  companion object {
    val All = MediaInsertMode(
      emptyList(),
      false,
      MediaLibraryType.values().toList()
    )
    val ImageOnly = MediaInsertMode(
      listOf(
        MediaInsertType.GIF,
        MediaInsertType.RECORD_VIDEO
      ),
      true,
      listOf(MediaLibraryType.Image)
    )
    val Disabled = MediaInsertMode(
      MediaInsertType.values().toList(),
      true,
      listOf(MediaLibraryType.Image)
    )
  }
}
