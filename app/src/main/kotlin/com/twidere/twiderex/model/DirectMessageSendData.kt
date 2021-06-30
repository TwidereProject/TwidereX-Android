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

data class DirectMessageSendData(
    val text: String?,
    val images: List<String>,
    val recipientUserKey: MicroBlogKey,
    val conversationKey: MicroBlogKey,
    val accountKey: MicroBlogKey,
    val draftMessageKey: MicroBlogKey,
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
