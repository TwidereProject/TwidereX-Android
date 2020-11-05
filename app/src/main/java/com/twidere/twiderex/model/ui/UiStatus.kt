/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.model.ui

import android.os.Parcelable
import com.twidere.twiderex.db.model.DbStatusWithMediaAndUser
import com.twidere.twiderex.db.model.DbStatusWithReference
import com.twidere.twiderex.db.model.DbTimelineWithStatus
import com.twidere.twiderex.model.UserKey
import com.twidere.twiderex.model.ui.UiMedia.Companion.toUi
import com.twidere.twiderex.model.ui.UiUser.Companion.toUi
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UiStatus(
    val statusId: String,
    val userKey: UserKey,
    val text: String,
    val timestamp: Long,
    val retweetCount: Long,
    val likeCount: Long,
    val replyCount: Long,
    var retweeted: Boolean,
    var liked: Boolean,
    val placeString: String?,
    val hasMedia: Boolean,
    val user: UiUser,
    val media: List<UiMedia>,
    var retweet: UiStatus?,
    val source: String,
    val quote: UiStatus?,
    val isGap: Boolean,
) : Parcelable {

    companion object {

        fun DbTimelineWithStatus.toUi() = UiStatus(
            statusId = status.status.data.statusId,
            userKey = status.status.data.userKey,
            text = status.status.data.text,
            timestamp = status.status.data.timestamp,
            retweetCount = status.status.data.retweetCount,
            likeCount = status.status.data.likeCount,
            replyCount = status.status.data.replyCount,
            retweeted = status.status.data.retweeted,
            liked = status.status.data.liked,
            placeString = status.status.data.placeString,
            hasMedia = status.status.data.hasMedia,
            user = status.status.user.toUi(),
            media = status.status.media.toUi(),
            retweet = status.retweet?.toUi(),
            quote = status.quote?.toUi(),
            isGap = timeline.isGap,
            source = status.status.data.source
        )

        fun DbStatusWithMediaAndUser.toUi() = UiStatus(
            statusId = data.statusId,
            userKey = data.userKey,
            text = data.text,
            timestamp = data.timestamp,
            retweetCount = data.retweetCount,
            likeCount = data.likeCount,
            replyCount = data.replyCount,
            retweeted = data.retweeted,
            liked = data.liked,
            placeString = data.placeString,
            hasMedia = data.hasMedia,
            user = user.toUi(),
            media = media.toUi(),
            retweet = null,
            quote = null,
            isGap = false,
            source = data.source,
        )
        fun DbStatusWithReference.toUi() = UiStatus(
            statusId = status.data.statusId,
            userKey = status.data.userKey,
            text = status.data.text,
            timestamp = status.data.timestamp,
            retweetCount = status.data.retweetCount,
            likeCount = status.data.likeCount,
            replyCount = status.data.replyCount,
            retweeted = status.data.retweeted,
            liked = status.data.liked,
            placeString = status.data.placeString,
            hasMedia = status.data.hasMedia,
            user = status.user.toUi(),
            media = status.media.toUi(),
            retweet = retweet?.toUi(),
            quote = quote?.toUi(),
            isGap = false,
            source = status.data.source
        )
    }
}
