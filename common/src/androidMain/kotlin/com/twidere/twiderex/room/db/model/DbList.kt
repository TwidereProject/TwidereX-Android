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
package com.twidere.twiderex.room.db.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.twidere.twiderex.model.MicroBlogKey

@Entity(
    tableName = "lists",
    indices = [Index(value = ["accountKey", "listKey"], unique = true)],
)
internal data class DbList(
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
)
