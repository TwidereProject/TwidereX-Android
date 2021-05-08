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
package com.twidere.twiderex.db.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.ListsMode

@Entity(
    tableName = "lists",
    indices = [Index(value = ["accountKey", "listKey"], unique = true)],
)
data class DbList(
    @PrimaryKey
    var _id: String,
    val listId: String,
    val ownerId: String,
    val accountKey: MicroBlogKey,
    val listKey: MicroBlogKey,
    val title: String,
    val description: String,
    val mode: String,
    val replyPolicy: String,
    val isFollowed: Boolean,
    val allowToSubscribe: Boolean
) {
    fun update(
        title: String? = null,
        description: String? = null,
        mode: String? = null,
        isFollowed: Boolean? = null,
        replyPolicy: String? = null
    ) = DbList(
        _id = _id,
        listId = listId,
        ownerId = ownerId,
        accountKey = accountKey,
        listKey = listKey,
        title = title ?: this.title,
        description = description ?: this.description,
        mode = mode ?: this.mode,
        replyPolicy = replyPolicy ?: this.replyPolicy,
        isFollowed = isFollowed ?: this.isFollowed,
        allowToSubscribe = this.allowToSubscribe && mode != ListsMode.PRIVATE.value
    )
}