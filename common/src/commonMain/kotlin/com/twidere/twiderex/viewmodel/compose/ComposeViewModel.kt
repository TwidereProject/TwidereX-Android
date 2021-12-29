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

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.twidere.services.microblog.LookupService
import com.twidere.twiderex.action.ComposeAction
import com.twidere.twiderex.action.DraftAction
import com.twidere.twiderex.component.media.MediaLibraryType
import com.twidere.twiderex.extensions.asStateIn
import com.twidere.twiderex.extensions.getTextAfterSelection
import com.twidere.twiderex.extensions.getTextBeforeSelection
import com.twidere.twiderex.kmp.LocationProvider
import com.twidere.twiderex.kmp.MediaInsertProvider
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.ComposeType
import com.twidere.twiderex.model.enums.MastodonVisibility
import com.twidere.twiderex.model.enums.MediaInsertType
import com.twidere.twiderex.model.enums.MediaType
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.job.ComposeData
import com.twidere.twiderex.model.ui.UiDraft
import com.twidere.twiderex.model.ui.UiEmoji
import com.twidere.twiderex.model.ui.UiMediaInsert
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.DraftRepository
import com.twidere.twiderex.repository.StatusRepository
import com.twidere.twiderex.repository.UserRepository
import com.twidere.twiderex.utils.MastodonEmojiCache
import com.twidere.twiderex.utils.notifyError
import com.twitter.twittertext.Extractor
import com.twitter.twittertext.TwitterTextConfiguration
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import java.util.UUID

class DraftItemViewModel(
    private val repository: DraftRepository,
    private val draftId: String,
) : ViewModel() {

    val draft = flow {
        repository.get(draftId)?.let {
            emit(it)
        }
    }
}

class DraftComposeViewModel(
    draftRepository: DraftRepository,
    composeAction: ComposeAction,
    repository: StatusRepository,
    userRepository: UserRepository,
    draftAction: DraftAction,
    mediaInsertProvider: MediaInsertProvider,
    inAppNotification: InAppNotification,
    accountRepository: AccountRepository,
    locationProvider: LocationProvider,
    draft: UiDraft,
) : ComposeViewModel(
    draftRepository = draftRepository,
    composeAction = composeAction,
    repository = repository,
    userRepository = userRepository,
    draftAction = draftAction,
    inAppNotification = inAppNotification,
    accountRepository = accountRepository,
    locationProvider = locationProvider,
    statusKey = draft.statusKey,
    composeType = draft.composeType,
) {

    override val draftId: String = draft.draftId

    init {
        setText(TextFieldValue(draft.content))
        viewModelScope.launch {
            putImages(
                draft.media.map {
                    mediaInsertProvider.provideUiMediaInsert(it)
                }
            )
        }
        excludedReplyUserIds.value = draft.excludedReplyUserIds ?: emptyList()
    }
}

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

open class ComposeViewModel(
    protected val draftRepository: DraftRepository,
    private val composeAction: ComposeAction,
    protected val repository: StatusRepository,
    private val userRepository: UserRepository,
    private val draftAction: DraftAction,
    private val inAppNotification: InAppNotification,
    private val accountRepository: AccountRepository,
    private val locationProvider: LocationProvider,
    protected val statusKey: MicroBlogKey?,
    val composeType: ComposeType,
) : ViewModel() {
    private val account by lazy {
        accountRepository.activeAccount.mapNotNull { it }
    }

    open val draftId: String = UUID.randomUUID().toString()

    @OptIn(ExperimentalCoroutinesApi::class)
    val emojis by lazy {
        account
            .filter { it.type == PlatformType.Mastodon }
            .flatMapLatest { MastodonEmojiCache.get(it) }
            .asStateIn(viewModelScope, emptyList())
    }

    val draftCount by lazy {
        draftRepository.sourceCount
    }

    val location by lazy {
        locationProvider.location.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )
    }
    val excludedReplyUserIds = MutableStateFlow<List<String>>(emptyList())
    val replyToUserName by lazy {
        combine(account.mapNotNull { it }, status.mapNotNull { it }) { account, status ->
            if (account.type == PlatformType.Twitter && composeType == ComposeType.Reply && statusKey != null) {
                Extractor().extractMentionedScreennames(
                    status.htmlText
                ).filter { it != account.user.screenName && it != status.user.screenName }
            } else {
                emptyList()
            }
        }.asStateIn(viewModelScope, emptyList())
    }

    val loadingReplyUser = MutableStateFlow(false)

    val replyToUser by lazy {
        combine(account.mapNotNull { it }, replyToUserName) { account, list ->
            if (list.isNotEmpty()) {
                loadingReplyUser.value = true
                try {
                    userRepository.lookupUsersByName(
                        list,
                        accountKey = account.accountKey,
                        lookupService = account.service as LookupService,
                    )
                } catch (e: Throwable) {
                    inAppNotification.notifyError(e)
                    emptyList()
                } finally {
                    loadingReplyUser.value = false
                }
            } else {
                emptyList()
            }
        }.asStateIn(viewModelScope, emptyList())
    }

    val voteState = MutableStateFlow<VoteState?>(null)
    val isInVoteMode = MutableStateFlow(false)
    val visibility = MutableStateFlow(MastodonVisibility.Public)
    val isImageSensitive = MutableStateFlow(false)
    val isContentWarningEnabled = MutableStateFlow(false)
    val contentWarningTextFieldValue = MutableStateFlow(TextFieldValue())
    val textFieldValue = MutableStateFlow(TextFieldValue())
    val images = MutableStateFlow<List<UiMediaInsert>>(emptyList())
    val canSend by lazy {
        combine(textFieldValue, images, maxContentLength) { text, imgs, maxLength ->
            (text.text.isNotEmpty() || !imgs.isNullOrEmpty()) && text.text.length <= maxLength
        }.asStateIn(viewModelScope, false)
    }
    val canSaveDraft = textFieldValue
        .combine(images) { text, imgs -> text.text.isNotEmpty() || !imgs.isNullOrEmpty() }
        .asStateIn(viewModelScope, false)
    val locationEnabled by lazy {
        locationProvider.locationEnabled.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )
    }
    val enableThreadMode = MutableStateFlow(composeType == ComposeType.Thread)

    @OptIn(ExperimentalCoroutinesApi::class)
    val status by lazy {
        account.mapNotNull { it }.flatMapLatest { account ->
            if (statusKey != null) {
                repository.loadStatus(statusKey, accountKey = account.accountKey)
                    .map { status ->
                        if (status != null &&
                            textFieldValue.value.text.isEmpty() &&
                            status.platformType == PlatformType.Mastodon &&
                            status.mastodonExtra?.mentions != null &&
                            composeType == ComposeType.Reply
                        ) {
                            val mentions =
                                status.mastodonExtra.mentions.mapNotNull { it.acct }
                                    .filter { it != account.user.screenName }
                                    .map { "@$it" }
                                    .let {
                                        if (status.user.userKey != account.user.userKey) {
                                            listOf(status.user.getDisplayScreenName(account.accountKey.host)) + it
                                        } else {
                                            it
                                        }
                                    }
                                    .distinctBy { it }
                                    .takeIf { it.any() }
                                    ?.joinToString(" ", postfix = " ") { it }
                            if (mentions != null) {
                                setText(
                                    TextFieldValue(
                                        mentions,
                                        selection = TextRange(mentions.length)
                                    )
                                )
                            }
                        }
                        status
                    }
            } else {
                flowOf(null)
            }
        }.asStateIn(viewModelScope, null)
    }

    val maxContentLength = account.combine(status) { account, status ->
        when (account.type) {
            PlatformType.Twitter -> TwitterTextConfiguration.getDefaultConfig().maxWeightedTweetLength -
                (
                    status?.generateShareLink()?.let {
                        it.length + 1 // for space
                    } ?: 0
                    )
            PlatformType.StatusNet -> TODO()
            PlatformType.Fanfou -> TODO()
            PlatformType.Mastodon -> 500
        }
    }.asStateIn(viewModelScope, 1)

    val mediaInsertMode = MutableStateFlow(MediaInsertMode.All)

    fun setText(value: TextFieldValue) {
        textFieldValue.value = value
    }

    fun setContentWarningText(value: TextFieldValue) {
        contentWarningTextFieldValue.value = value
    }

    fun setContentWarningEnabled(value: Boolean) {
        isContentWarningEnabled.value = value
    }

    fun setImageSensitive(value: Boolean) {
        isImageSensitive.value = value
    }

    fun setEnableThreadMode(value: Boolean) {
        enableThreadMode.value = value
    }

    fun setVisibility(value: MastodonVisibility) {
        visibility.value = value
    }

    fun setInVoteMode(value: Boolean) {
        if (value) {
            voteState.value = VoteState()
        } else {
            voteState.value = null
        }
        isInVoteMode.value = value
    }

    fun compose() {
        textFieldValue.value.text.let {
            composeAction.commit(
                buildComposeData(it)
            )
        }
    }

    fun saveDraft() {
        textFieldValue.value.text.let { text ->
            draftAction.save(buildComposeData(text))
        }
    }

    private fun buildComposeData(text: String) = ComposeData(
        content = text,
        draftId = draftId,
        images = images.value.map { it.filePath },
        composeType = composeType,
        statusKey = statusKey,
        lat = location.value?.latitude,
        long = location.value?.longitude,
        excludedReplyUserIds = excludedReplyUserIds.value,
        voteOptions = voteState.value?.options?.value?.map { it.text.value },
        voteExpired = voteState.value?.expired?.value,
        voteMultiple = voteState.value?.multiple?.value,
        visibility = visibility.value,
        isSensitive = isImageSensitive.value,
        contentWarningText = contentWarningTextFieldValue.value.text,
        isThreadMode = enableThreadMode.value,
    )

    fun putImages(value: List<UiMediaInsert>) = viewModelScope.launch {
        val allowType =
            images.value.firstOrNull()?.type ?: value.firstOrNull()?.type ?: MediaType.photo
        images.value.let {
            value + it.filter { media -> media.type == allowType }
        }.take(if (allowType == MediaType.photo) imageLimit.first() else 1).let {
            images.value = it
        }
        when (allowType) {
            MediaType.video, MediaType.animated_gif ->
                mediaInsertMode.value =
                    MediaInsertMode.Disabled
            else -> {
                mediaInsertMode.value = if (images.value.size == imageLimit.first())
                    MediaInsertMode.Disabled
                else
                    MediaInsertMode.ImageOnly
            }
        }
    }

    private val imageLimit by lazy {
        account.map {
            when (it.type) {
                PlatformType.Twitter -> 4
                PlatformType.StatusNet -> TODO()
                PlatformType.Fanfou -> TODO()
                PlatformType.Mastodon -> 4
            }
        }.asStateIn(viewModelScope, 4)
    }

    fun trackingLocation() {
        locationProvider.enable()
    }

    fun disableLocation() {
        locationProvider.disable()
    }

    fun removeImage(item: String) {
        images.value.let {
            it.toMutableList().apply {
                removeAll { media -> media.filePath == item }
            }
        }.let {
            images.value = it
        }
        if (images.value.isEmpty()) {
            mediaInsertMode.value = MediaInsertMode.All
        } else if (images.value.first().type == MediaType.photo) {
            mediaInsertMode.value = MediaInsertMode.ImageOnly
        }
    }

    fun excludeReplyUser(user: UiUser) {
        excludedReplyUserIds.value.let {
            excludedReplyUserIds.value = it + user.id
        }
    }

    fun includeReplyUser(user: UiUser) {
        excludedReplyUserIds.value.let {
            excludedReplyUserIds.value = it - user.id
        }
    }

    fun insertText(result: String) {
        textFieldValue.value.let {
            setText(
                it.copy(
                    text = "${it.getTextBeforeSelection()}${result}${it.getTextAfterSelection()}",
                    selection = TextRange(it.selection.min + result.length)
                )
            )
        }
    }

    fun insertEmoji(emoji: UiEmoji) {
        insertText("${if (textFieldValue.value.selection.start != 0) " " else ""}:${emoji.shortcode}: ")
    }

    override fun onCleared() {
        locationProvider.disable()
    }

    data class MediaInsertMode(
        val disabledInsertType: List<MediaInsertType>,
        val multiSelect: Boolean,
        val librarySupportedType: List<MediaLibraryType>
    ) {
        companion object {
            val All = MediaInsertMode(emptyList(), false, MediaLibraryType.values().toList())
            val ImageOnly = MediaInsertMode(
                listOf(
                    MediaInsertType.GIF,
                    MediaInsertType.RECORD_VIDEO
                ),
                true, listOf(MediaLibraryType.Image)
            )
            val Disabled = MediaInsertMode(
                MediaInsertType.values().toList(),
                true,
                listOf(MediaLibraryType.Image)
            )
        }
    }
}
