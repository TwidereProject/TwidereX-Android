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
package com.twidere.twiderex.db.transform

import androidx.work.Data
import androidx.work.workDataOf
import com.twidere.twiderex.extensions.getNullableBoolean
import com.twidere.twiderex.extensions.getNullableDouble
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.ComposeType
import com.twidere.twiderex.model.enums.MastodonVisibility
import com.twidere.twiderex.model.job.ComposeData
import com.twidere.twiderex.model.job.DirectMessageDeleteData
import com.twidere.twiderex.model.job.DirectMessageSendData
import com.twidere.twiderex.model.job.StatusResult
import com.twidere.twiderex.viewmodel.compose.VoteExpired

fun StatusResult.toWorkData() = workDataOf(
    "statusKey" to statusKey.toString(),
    "accountKey" to accountKey.toString(),
    "liked" to liked,
    "retweeted" to retweeted,
    "retweetCount" to retweetCount,
    "likeCount" to likeCount,
)

fun ComposeData.toWorkData() = workDataOf(
    "content" to content,
    "images" to images.toTypedArray(),
    "composeType" to composeType.name,
    "statusKey" to statusKey?.toString(),
    "lat" to lat,
    "long" to long,
    "draftId" to draftId,
    "excludedReplyUserIds" to excludedReplyUserIds?.toTypedArray(),
    "voteOptions" to voteOptions?.toTypedArray(),
    "voteExpired" to voteExpired?.name,
    "voteMultiple" to voteMultiple,
    "visibility" to visibility?.name,
    "isSensitive" to isSensitive,
    "contentWarningText" to contentWarningText,
    "isThreadMode" to isThreadMode,
)

fun Data.toComposeData() = ComposeData(
    content = getString("content") ?: "",
    images = getStringArray("images")?.toList() ?: emptyList(),
    composeType = ComposeType.valueOf(getString("composeType") ?: ComposeType.New.name),
    statusKey = getString("statusKey")?.let { MicroBlogKey.valueOf(it) },
    lat = getNullableDouble("lat"),
    long = getNullableDouble("long"),
    draftId = getString("draftId") ?: "",
    excludedReplyUserIds = getStringArray("excludedReplyUserIds")?.toList(),
    voteOptions = getStringArray("voteOptions")?.toList(),
    voteExpired = getString("voteExpired")?.let { VoteExpired.valueOf(it) },
    voteMultiple = getNullableBoolean("voteMultiple"),
    visibility = getString("visibility")?.let { MastodonVisibility.valueOf(it) },
    isSensitive = getNullableBoolean("isSensitive"),
    contentWarningText = getString("contentWarningText"),
    isThreadMode = getBoolean("isThreadMode", false),
)

fun DirectMessageDeleteData.toWorkData() = workDataOf(
    "accountKey" to accountKey.toString(),
    "conversationKey" to conversationKey.toString(),
    "messageKey" to messageKey.toString(),
    "messageId" to messageId,
)

fun Data.toDirectMessageDeleteData() = DirectMessageDeleteData(
    messageId = getString("messageId") ?: "",
    accountKey = MicroBlogKey.valueOf(getString("accountKey") ?: ""),
    conversationKey = MicroBlogKey.valueOf(getString("conversationKey") ?: ""),
    messageKey = MicroBlogKey.valueOf(getString("messageKey") ?: ""),
)

fun DirectMessageSendData.toWorkData() = workDataOf(
    "text" to text,
    "images" to images.toTypedArray(),
    "recipientUserKey" to recipientUserKey.toString(),
    "dratMessageKey" to draftMessageKey.toString(),
    "conversationKey" to conversationKey.toString(),
    "accountKey" to accountKey.toString(),
)

fun Data.toDirectMessageSendData() = DirectMessageSendData(
    text = getString("text") ?: "",
    images = getStringArray("images")?.toList() ?: emptyList(),
    recipientUserKey = MicroBlogKey.valueOf(getString("recipientUserKey") ?: ""),
    draftMessageKey = MicroBlogKey.valueOf(getString("dratMessageKey") ?: ""),
    conversationKey = MicroBlogKey.valueOf(getString("conversationKey") ?: ""),
    accountKey = MicroBlogKey.valueOf(getString("accountKey") ?: ""),
)
