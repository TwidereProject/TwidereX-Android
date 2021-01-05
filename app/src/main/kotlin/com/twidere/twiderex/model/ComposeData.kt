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
import com.twidere.twiderex.extensions.getNullableDouble
import com.twidere.twiderex.scenes.ComposeType
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
)

fun ComposeData.toWorkData() = workDataOf(
    "content" to content,
    "images" to images.toTypedArray(),
    "composeType" to composeType.name,
    "statusKey" to statusKey?.toString(),
    "lat" to lat,
    "long" to long,
    "draftId" to draftId,
    "excludedReplyUserIds" to excludedReplyUserIds?.toTypedArray()
)

fun Data.toComposeData() = ComposeData(
    content = getString("content") ?: "",
    images = getStringArray("images")?.toList() ?: emptyList(),
    composeType = ComposeType.valueOf(getString("composeType") ?: ComposeType.New.name),
    statusKey = getString("statusKey")?.let { MicroBlogKey.valueOf(it) },
    lat = getNullableDouble("lat"),
    long = getNullableDouble("long"),
    draftId = getString("draftId") ?: "",
    excludedReplyUserIds = getStringArray("excludedReplyUserIds")?.toList()
)
