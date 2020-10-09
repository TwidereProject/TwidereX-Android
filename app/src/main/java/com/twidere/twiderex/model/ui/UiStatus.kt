package com.twidere.twiderex.model.ui

import android.os.Parcelable
import com.twidere.twiderex.db.model.DbStatusWithMediaAndUser
import com.twidere.twiderex.db.model.DbTimelineWithStatus
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.model.UserKey
import com.twidere.twiderex.model.ui.UiMedia.Companion.toUi
import com.twidere.twiderex.model.ui.UiUser.Companion.toUi
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UiStatus(
    val statusId: String,
    val userKey: UserKey,
    val platformType: PlatformType,
    val text: String,
    val timestamp: Long,
    val retweetCount: Long,
    val likeCount: Long,
    val replyCount: Long,
    val retweeted: Boolean,
    val liked: Boolean,
    val extra: String,
    val placeString: String?,
    val hasMedia: Boolean,
    val user: UiUser,
    val media: List<UiMedia>,
    val retweet: UiStatus?,
    val quote: UiStatus?,
    val isGap: Boolean,
) : Parcelable {
    companion object {
        fun DbTimelineWithStatus.toUi() = UiStatus(
            statusId = status.status.statusId,
            userKey = status.status.userKey,
            platformType = status.status.platformType,
            text = status.status.text,
            timestamp = status.status.timestamp,
            retweetCount = status.status.retweetCount,
            likeCount = status.status.likeCount,
            replyCount = status.status.replyCount,
            retweeted = status.status.retweeted,
            liked = status.status.liked,
            extra = status.status.extra,
            placeString = status.status.placeString,
            hasMedia = status.status.hasMedia,
            user = status.user.toUi(),
            media = status.media.toUi(),
            retweet = retweet?.toUi(),
            quote = quote?.toUi(),
            isGap = timeline.isGap,
        )

        fun DbStatusWithMediaAndUser.toUi() = UiStatus(
            statusId = status.statusId,
            userKey = status.userKey,
            platformType = status.platformType,
            text = status.text,
            timestamp = status.timestamp,
            retweetCount = status.retweetCount,
            likeCount = status.likeCount,
            replyCount = status.replyCount,
            retweeted = status.retweeted,
            liked = status.liked,
            extra = status.extra,
            placeString = status.placeString,
            hasMedia = status.hasMedia,
            user = user.toUi(),
            media = media.toUi(),
            retweet = null,
            quote = null,
            isGap = false,
        )
    }
}

