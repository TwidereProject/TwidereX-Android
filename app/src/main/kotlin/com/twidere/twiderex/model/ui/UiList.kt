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
package com.twidere.twiderex.model.ui
import androidx.compose.runtime.Immutable
import com.twidere.twiderex.db.model.DbList
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
) {

    fun isOwner(userId: String): Boolean {
        return ownerId == userId
    }

    val isPrivate: Boolean
        get() = mode == ListsMode.PRIVATE.value

    companion object {

        fun sample() = UiList(
            id = "1",
            ownerId = "1",
            title = "Sample List",
            descriptions = "Sample List",
            mode = "public",
            replyPolicy = "",
            accountKey = MicroBlogKey.Empty,
            listKey = MicroBlogKey.Empty
        )

        fun DbList.toUi() =
            UiList(
                id = listId,
                ownerId = ownerId,
                listKey = listKey,
                accountKey = accountKey,
                title = title,
                descriptions = description,
                mode = mode,
                replyPolicy = replyPolicy,
            )
    }
}

enum class ListsMode(val value: String) {
    PRIVATE("private"),
    PUBLIC("public"),
    DEFAULT("")
}
