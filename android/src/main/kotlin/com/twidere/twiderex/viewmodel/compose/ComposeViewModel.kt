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
package com.twidere.twiderex.viewmodel.compose

import android.Manifest.permission
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import androidx.annotation.RequiresPermission
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.work.WorkManager
import com.twidere.services.microblog.LookupService
import com.twidere.twiderex.action.ComposeAction
import com.twidere.twiderex.db.model.DbDraft
import com.twidere.twiderex.ext.asStateIn
import com.twidere.twiderex.extensions.getCachedLocation
import com.twidere.twiderex.extensions.getTextAfterSelection
import com.twidere.twiderex.extensions.getTextBeforeSelection
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.ComposeType
import com.twidere.twiderex.model.enums.MastodonVisibility
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.job.ComposeData
import com.twidere.twiderex.model.ui.UiDraft
import com.twidere.twiderex.model.ui.UiEmoji
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.DraftRepository
import com.twidere.twiderex.repository.StatusRepository
import com.twidere.twiderex.repository.UserRepository
import com.twidere.twiderex.utils.MastodonEmojiCache
import com.twidere.twiderex.utils.notify
import com.twidere.twiderex.worker.draft.SaveDraftWorker
import com.twitter.twittertext.Extractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.map
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
    locationManager: LocationManager,
    composeAction: ComposeAction,
    repository: StatusRepository,
    userRepository: UserRepository,
    workManager: WorkManager,
    inAppNotification: InAppNotification,
    accountRepository: AccountRepository,
    draft: UiDraft,
) : ComposeViewModel(
    draftRepository,
    locationManager,
    composeAction,
    repository,
    userRepository,
    workManager,
    inAppNotification,
    accountRepository,
    draft.statusKey,
    draft.composeType,
) {

    override val draftId: String = draft.draftId

    init {
        setText(TextFieldValue(draft.content))
        putImages(draft.media.map { Uri.parse(it) })
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
    private val locationManager: LocationManager,
    protected val composeAction: ComposeAction,
    protected val repository: StatusRepository,
    private val userRepository: UserRepository,
    private val workManager: WorkManager,
    private val inAppNotification: InAppNotification,
    private val accountRepository: AccountRepository,
    protected val statusKey: MicroBlogKey?,
    val composeType: ComposeType,
) : ViewModel(), LocationListener {
    private val account by lazy {
        accountRepository.activeAccount.asStateIn(viewModelScope, null)
    }

    open val draftId: String = UUID.randomUUID().toString()

    val emojis by lazy {
        account.flatMapLatest {
            it?.let { account ->
                if (account.type == PlatformType.Mastodon) {
                    MastodonEmojiCache.get(account)
                } else {
                    emptyFlow()
                }
            } ?: emptyFlow()
        }.asStateIn(viewModelScope, emptyList())
    }

    val draftCount by lazy {
        draftRepository.sourceCount
    }

    val location = MutableStateFlow<Location?>(null)
    val excludedReplyUserIds = MutableStateFlow<List<String>>(emptyList())
    val replyToUserName by lazy {
        combine(account, status) { account, status ->
            if (account != null && status != null) {
                if (account.type == PlatformType.Twitter && composeType == ComposeType.Reply && statusKey != null) {
                    Extractor().extractMentionedScreennames(
                        status.htmlText
                    ).filter { it != account.user.screenName && it != status.user.screenName }
                } else {
                    emptyList()
                }
            } else {
                emptyList()
            }
        }.asStateIn(viewModelScope, emptyList())
    }

    val loadingReplyUser = MutableStateFlow(false)

    val replyToUser by lazy {
        combine(account, replyToUserName) { account, list ->
            if (account != null) {
                if (list.isNotEmpty()) {
                    loadingReplyUser.value = true
                    try {
                        userRepository.lookupUsersByName(
                            list,
                            accountKey = account.accountKey,
                            lookupService = account.service as LookupService,
                        )
                    } catch (e: Throwable) {
                        e.notify(inAppNotification)
                        emptyList()
                    } finally {
                        loadingReplyUser.value = false
                    }
                } else {
                    emptyList()
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
    val images = MutableStateFlow<List<Uri>>(emptyList())
    val canSend = textFieldValue
        .combine(images) { text, imgs -> text.text.isNotEmpty() || !imgs.isNullOrEmpty() }
        .asStateIn(viewModelScope, false)
    val canSaveDraft = textFieldValue
        .combine(images) { text, imgs -> text.text.isNotEmpty() || !imgs.isNullOrEmpty() }
        .asStateIn(viewModelScope, false)
    val locationEnabled = MutableStateFlow(false)
    val enableThreadMode = MutableStateFlow(composeType == ComposeType.Thread)
    val status by lazy {
        account.flatMapLatest {
            if (statusKey != null) {
                it?.let { account ->
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
                                        .filter { it != account.user.screenName }.map { "@$it" }
                                        .let {
                                            if (status.user.userKey != account.user.userKey) {
                                                listOf(status.user.getDisplayScreenName(account.accountKey.host)) + it
                                            } else {
                                                it
                                            }
                                        }.distinctBy { it }.takeIf { it.any() }
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
                } ?: flowOf(null)
            } else {
                flowOf(null)
            }
        }.asStateIn(viewModelScope, null)
    }

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

    fun compose() = viewModelScope.launch {
        val account = account.lastOrNull()
        if (account != null) {
            textFieldValue.value.text.let {
                composeAction.commit(
                    account.accountKey,
                    account.type,
                    buildComposeData(it)
                )
            }
        }
    }

    fun saveDraft() {
        textFieldValue.value.text.let { text ->
            workManager
                .beginWith(
                    SaveDraftWorker.create(
                        buildComposeData(text)
                    )
                )
                .enqueue()
        }
    }

    private fun buildComposeData(text: String) = ComposeData(
        content = text,
        draftId = draftId,
        images = images.value.map { it.toString() },
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

    fun putImages(value: List<Uri>) = viewModelScope.launch {
        val imageLimit = imageLimit.lastOrNull() ?: 4
        images.value.let {
            value + it
        }.take(imageLimit).let {
            images.value = it
        }
    }

    private val imageLimit by lazy {
        account.map {
            when (it?.type) {
                PlatformType.Twitter -> 4
                PlatformType.StatusNet -> TODO()
                PlatformType.Fanfou -> TODO()
                PlatformType.Mastodon -> 4
                else -> 4
            }
        }.asStateIn(viewModelScope, 4)
    }

    @RequiresPermission(anyOf = [permission.ACCESS_COARSE_LOCATION, permission.ACCESS_FINE_LOCATION])
    fun trackingLocation() {
        locationEnabled.value = true
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        val provider = locationManager.getBestProvider(criteria, true) ?: return
        locationManager.requestLocationUpdates(provider, 0, 0f, this)
        locationManager.getCachedLocation()?.let {
            location.value = it
        }
    }

    fun disableLocation() {
        location.value = null
        locationEnabled.value = false
        locationManager.removeUpdates(this)
    }

    override fun onLocationChanged(location: Location) {
        this.location.value = location
    }

    // compatibility fix for Api < 22
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onCleared() {
        if (locationEnabled.value) {
            locationManager.removeUpdates(this)
        }
    }

    fun removeImage(item: Uri) {
        images.value.let {
            it - item
        }.let {
            images.value = it
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
}
