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
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.work.WorkManager
import com.twidere.services.mastodon.model.Emoji
import com.twidere.services.mastodon.model.Visibility
import com.twidere.services.microblog.LookupService
import com.twidere.twiderex.R
import com.twidere.twiderex.action.ComposeAction
import com.twidere.twiderex.db.model.DbDraft
import com.twidere.twiderex.extensions.combineWith
import com.twidere.twiderex.extensions.getCachedLocation
import com.twidere.twiderex.extensions.getTextAfterSelection
import com.twidere.twiderex.extensions.getTextBeforeSelection
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.ComposeData
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.DraftRepository
import com.twidere.twiderex.repository.UserRepository
import com.twidere.twiderex.repository.twitter.TwitterTweetsRepository
import com.twidere.twiderex.utils.MastodonEmojiCache
import com.twidere.twiderex.utils.notify
import com.twidere.twiderex.worker.draft.SaveDraftWorker
import com.twitter.twittertext.Extractor
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.UUID

enum class ComposeType {
    New,
    Reply,
    Quote,
}

class DraftItemViewModel @AssistedInject constructor(
    private val repository: DraftRepository,
    @Assisted private val draftId: String,
) : ViewModel() {

    val draft = liveData {
        repository.get(draftId)?.let {
            emit(it)
        }
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(
            draftId: String,
        ): DraftItemViewModel
    }
}

class DraftComposeViewModel @AssistedInject constructor(
    draftRepository: DraftRepository,
    locationManager: LocationManager,
    composeAction: ComposeAction,
    factory: TwitterTweetsRepository.AssistedFactory,
    userRepositoryFactory: UserRepository.AssistedFactory,
    workManager: WorkManager,
    inAppNotification: InAppNotification,
    @Assisted account: AccountDetails,
    @Assisted private val draft: DbDraft,
) : ComposeViewModel(
    draftRepository,
    locationManager,
    composeAction,
    factory,
    userRepositoryFactory,
    workManager,
    inAppNotification,
    account,
    draft.statusKey,
    draft.composeType,
) {

    override val draftId: String = draft._id

    init {
        setText(TextFieldValue(draft.content))
        putImages(draft.media.map { Uri.parse(it) })
        excludedReplyUserIds.postValue(draft.excludedReplyUserIds ?: emptyList())
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(
            account: AccountDetails,
            draft: DbDraft,
        ): DraftComposeViewModel
    }
}

class VoteOption {
    val text = MutableLiveData("")
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
    Day_7(604800);

    @Composable
    fun stringName(): String {
        return when (this) {
            Min_5 -> stringResource(id = R.string.scene_compose_vote_expiration_5_Min)
            Min_30 -> stringResource(id = R.string.scene_compose_vote_expiration_30_Min)
            Hour_1 -> stringResource(id = R.string.scene_compose_vote_expiration_1_Hour)
            Hour_6 -> stringResource(id = R.string.scene_compose_vote_expiration_6_Hour)
            Day_1 -> stringResource(id = R.string.scene_compose_vote_expiration_1_Day)
            Day_3 -> stringResource(id = R.string.scene_compose_vote_expiration_3_Day)
            Day_7 -> stringResource(id = R.string.scene_compose_vote_expiration_7_Day)
        }
    }
}

class VoteState {
    val options = MutableLiveData(arrayListOf(VoteOption(), VoteOption()))
    val expired = MutableLiveData(VoteExpired.Day_1)
    val multiple = MutableLiveData(false)

    fun setMultiple(value: Boolean) {
        multiple.value = value
    }

    fun setExpired(value: VoteExpired) {
        expired.value = value
    }

    fun setOption(value: String, index: Int) {
        options.value?.let {
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

open class ComposeViewModel @AssistedInject constructor(
    protected val draftRepository: DraftRepository,
    private val locationManager: LocationManager,
    protected val composeAction: ComposeAction,
    protected val factory: TwitterTweetsRepository.AssistedFactory,
    private val userRepositoryFactory: UserRepository.AssistedFactory,
    private val workManager: WorkManager,
    private val inAppNotification: InAppNotification,
    @Assisted protected val account: AccountDetails,
    @Assisted protected val statusKey: MicroBlogKey?,
    @Assisted val composeType: ComposeType,
) : ViewModel(), LocationListener {
    open val draftId: String = UUID.randomUUID().toString()

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(
            account: AccountDetails,
            statusKey: MicroBlogKey?,
            composeType: ComposeType
        ): ComposeViewModel
    }

    val emojis by lazy {
        if (account.type == PlatformType.Mastodon) {
            MastodonEmojiCache.get(account)
        } else {
            null
        }
    }

    protected val repository by lazy {
        factory.create(
            account.accountKey,
            account.service as LookupService,
        )
    }

    protected val userRepository by lazy {
        userRepositoryFactory.create(
            accountKey = account.accountKey,
            account.service as LookupService,
        )
    }

    val location = MutableLiveData<Location?>()
    val excludedReplyUserIds = MutableLiveData<List<String>>(emptyList())

    val replyToUserName = liveData {
        if (account.type == PlatformType.Twitter && composeType == ComposeType.Reply && statusKey != null) {
            emitSource(
                status.map {
                    it?.let { status ->
                        Extractor().extractMentionedScreennames(
                            status.htmlText
                        ).filter { it != account.user.screenName && it != status.user.screenName }
                    } ?: run {
                        emptyList<String>()
                    }
                },
            )
        } else {
            emit(emptyList<String>())
        }
    }

    val loadingReplyUser = MutableLiveData(false)

    val replyToUser = liveData {
        emitSource(
            replyToUserName.switchMap {
                liveData {
                    if (it.isNotEmpty()) {
                        loadingReplyUser.postValue(true)
                        runCatching {
                            userRepository.lookupUsersByName(it)
                        }.onFailure {
                            it.notify(inAppNotification)
                        }.onSuccess {
                            emit(it)
                        }
                        loadingReplyUser.postValue(false)
                    }
                }
            },
        )
    }

    val voteState = MutableLiveData<VoteState?>(null)
    val isInVoteMode = MutableLiveData(false)
    val visibility = MutableLiveData(Visibility.Public)
    val isImageSensitive = MutableLiveData(false)
    val isContentWarningEnabled = MutableLiveData(false)
    val contentWarningTextFieldValue = MutableLiveData(TextFieldValue())
    val textFieldValue = MutableLiveData(TextFieldValue())
    val images = MutableLiveData<List<Uri>>(emptyList())
    val canSaveDraft =
        textFieldValue.combineWith(images) { text, imgs -> !text?.text.isNullOrEmpty() || !imgs.isNullOrEmpty() }
    val locationEnabled = MutableLiveData(false)
    val status = liveData {
        statusKey?.let { statusKey ->
            emitSource(
                repository.loadTweetFromCache(statusKey).map { status ->
                    if (status != null &&
                        textFieldValue.value?.text.isNullOrEmpty() &&
                        status.platformType == PlatformType.Mastodon &&
                        status.mastodonExtra?.mentions != null &&
                        composeType == ComposeType.Reply
                    ) {
                        val mentions =
                            status.mastodonExtra.mentions.mapNotNull { it.acct }
                                .filter { it != account.user.screenName }.map { "@$it" }.let {
                                    if (status.user.userKey != account.user.userKey) {
                                        listOf(status.user.getDisplayScreenName(account)) + it
                                    } else {
                                        it
                                    }
                                }.distinctBy { it }.joinToString(" ", postfix = " ") { it }
                        setText(TextFieldValue(mentions, selection = TextRange(mentions.length)))
                    }
                    status
                }
            )
        } ?: run {
            emit(null)
        }
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

    fun setVisibility(value: Visibility) {
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
        textFieldValue.value?.text?.let {
            composeAction.commit(
                account.accountKey,
                account.type,
                buildComposeData(it)
            )
        }
    }

    fun saveDraft() {
        textFieldValue.value?.text?.let { text ->
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
        images = images.value?.map { it.toString() } ?: emptyList(),
        composeType = composeType,
        statusKey = statusKey,
        lat = location.value?.latitude,
        long = location.value?.longitude,
        excludedReplyUserIds = excludedReplyUserIds.value,
        voteOptions = voteState.value?.options?.value?.map { it.text.value.toString() },
        voteExpired = voteState.value?.expired?.value,
        voteMultiple = voteState.value?.multiple?.value,
        visibility = visibility.value,
        isSensitive = isImageSensitive.value,
        contentWarningText = contentWarningTextFieldValue.value?.text,
    )

    fun putImages(value: List<Uri>) {
        images.value?.let {
            value + it
        }?.let {
            it.take(4)
        }?.let {
            images.postValue(it)
        }
    }

    @RequiresPermission(anyOf = [permission.ACCESS_COARSE_LOCATION, permission.ACCESS_FINE_LOCATION])
    fun trackingLocation() {
        locationEnabled.postValue(true)
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        val provider = locationManager.getBestProvider(criteria, true) ?: return
        locationManager.requestLocationUpdates(provider, 0, 0f, this)
        locationManager.getCachedLocation()?.let {
            location.postValue(it)
        }
    }

    fun disableLocation() {
        location.postValue(null)
        locationEnabled.postValue(false)
        locationManager.removeUpdates(this)
    }

    override fun onLocationChanged(location: Location) {
        this.location.postValue(location)
    }

    // compatibility fix for Api < 22
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onCleared() {
        if (locationEnabled.value == true) {
            locationManager.removeUpdates(this)
        }
    }

    fun removeImage(item: Uri) {
        images.value?.let {
            it - item
        }?.let {
            images.postValue(it)
        }
    }

    fun excludeReplyUser(user: UiUser) {
        excludedReplyUserIds.value?.let {
            excludedReplyUserIds.postValue(it + user.id)
        }
    }

    fun includeReplyUser(user: UiUser) {
        excludedReplyUserIds.value?.let {
            excludedReplyUserIds.postValue(it - user.id)
        }
    }

    fun insertText(result: String) {
        textFieldValue.value?.let {
            setText(
                it.copy(
                    text = "${it.getTextBeforeSelection()}${result}${it.getTextAfterSelection()}",
                    selection = TextRange(it.selection.min + result.length)
                )
            )
        }
    }

    fun insertEmoji(emoji: Emoji) {
        textFieldValue.value?.let { textFieldValue ->
            insertText("${if (textFieldValue.selection.start != 0) " " else ""}:${emoji.shortcode}: ")
        }
    }
}
