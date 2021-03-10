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
package com.twidere.twiderex.model

import androidx.work.Data
import androidx.work.workDataOf
import com.twidere.services.mastodon.model.Visibility
import com.twidere.twiderex.extensions.getNullableBoolean
import com.twidere.twiderex.extensions.getNullableDouble
import com.twidere.twiderex.viewmodel.compose.ComposeType
import com.twidere.twiderex.viewmodel.compose.VoteExpired
import java.util.UUID

data class ComposeData(
    val content: String,
    val images: List<String>,
    val composeType: ComposeType,
    val statusKey: MicroBlogKey? = null,
    val lat: Double? = null,
    val long: Double? = null,
    val draftId: String = UUID.randomUUID().toString(),
    val excludedReplyUserIds: List<String>? = null,
    val voteOptions: List<String>? = null,
    val voteExpired: VoteExpired? = null,
    val voteMultiple: Boolean? = null,
    val visibility: Visibility? = null,
    val isSensitive: Boolean? = null,
    val contentWarningText: String? = null,
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
    visibility = getString("visibility")?.let { Visibility.valueOf(it) },
    isSensitive = getNullableBoolean("isSensitive"),
    contentWarningText = getString("contentWarningText"),
)
