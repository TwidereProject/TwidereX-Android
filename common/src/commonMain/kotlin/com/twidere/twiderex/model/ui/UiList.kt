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
package com.twidere.twiderex.model.ui
import androidx.compose.runtime.Immutable
import com.twidere.twiderex.model.MicroBlogKey

@Immutable
data class UiList(
    val id: String,
    val ownerId: String,
    val title: String,
    val descriptions: String,
    val mode: String,
    val replyPolicy: String,
    val accountKey: MicroBlogKey,
    val listKey: MicroBlogKey,
    val isFollowed: Boolean,
    val allowToSubscribe: Boolean
) {

    fun isOwner(userId: String): Boolean {
        return ownerId == userId
    }

    val isPrivate: Boolean
        get() = mode == ListsMode.PRIVATE.value

    companion object {

        fun sample(isFollowed: Boolean = true) = UiList(
            id = "1",
            ownerId = "1",
            title = "Sample List",
            descriptions = "Sample List",
            mode = "private",
            replyPolicy = "",
            accountKey = MicroBlogKey.Empty,
            listKey = MicroBlogKey.Empty,
            isFollowed = isFollowed,
            allowToSubscribe = true,
        )
    }
}

enum class ListsMode(val value: String) {
    PRIVATE("private"),
    PUBLIC("public"),
    DEFAULT("")
}
