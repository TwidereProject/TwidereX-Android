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

        fun DbTimelineWithStatus.toUi(
            userKey: UserKey,
        ) = with(status.status) {
                val reaction = reactions.firstOrNull { it.userKey == userKey }
                UiStatus(
                    statusId = data.statusId,
                    text = data.text,
                    timestamp = data.timestamp,
                    retweetCount = data.retweetCount,
                    likeCount = data.likeCount,
                    replyCount = data.replyCount,
                    retweeted = reaction?.retweeted ?: false,
                    liked = reaction?.liked ?: false,
                    placeString = data.placeString,
                    hasMedia = data.hasMedia,
                    user = user.toUi(),
                    media = media.toUi(),
                    retweet = status.retweet?.toUi(userKey)?.copy(quote = status.quote?.toUi(userKey)),
                    quote = status.quote?.toUi(userKey),
                    isGap = timeline.isGap,
                    source = data.source
                )
            }

        fun DbStatusWithMediaAndUser.toUi(
            userKey: UserKey,
        ): UiStatus {
            val reaction = reactions.firstOrNull { it.userKey == userKey }
            return UiStatus(
                statusId = data.statusId,
                text = data.text,
                timestamp = data.timestamp,
                retweetCount = data.retweetCount,
                likeCount = data.likeCount,
                replyCount = data.replyCount,
                retweeted = reaction?.retweeted ?: false,
                liked = reaction?.liked ?: false,
                placeString = data.placeString,
                hasMedia = data.hasMedia,
                user = user.toUi(),
                media = media.toUi(),
                retweet = null,
                quote = null,
                isGap = false,
                source = data.source,
            )
        }

        fun DbStatusWithReference.toUi(
            userKey: UserKey,
        ) = with(status) {
            val reaction = reactions.firstOrNull { it.userKey == userKey }
            UiStatus(
                statusId = data.statusId,
                text = data.text,
                timestamp = data.timestamp,
                retweetCount = data.retweetCount,
                likeCount = data.likeCount,
                replyCount = data.replyCount,
                retweeted = reaction?.retweeted ?: false,
                liked = reaction?.liked ?: false,
                placeString = data.placeString,
                hasMedia = data.hasMedia,
                user = user.toUi(),
                media = media.toUi(),
                retweet = retweet?.toUi(userKey)?.copy(quote = quote?.toUi(userKey)),
                quote = quote?.toUi(userKey),
                isGap = false,
                source = data.source
            )
        }
    }
}
