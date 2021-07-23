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
package com.twidere.twiderex.navigation

import com.twidere.route.processor.AppRoute
import com.twidere.twiderex.model.MicroBlogKey

@AppRoute(
    prefix = "$twidereXSchema://"
)
interface RootDeepLinks {
    interface Twitter {
        fun User(screenName: String): String
        fun Status(id: String): String
    }

    interface Mastodon {
        fun Hashtag(keyword: String): String
    }

    fun User(userKey: MicroBlogKey): String
    fun Status(statusKey: MicroBlogKey): String
    fun Search(keyword: String): String
    val SignIn: String

    val Draft: String
    val Compose: String
    val Conversation: String

    interface Callback {
        interface SignIn {
            val Mastodon: String
            val Twitter: String
        }
    }
}
