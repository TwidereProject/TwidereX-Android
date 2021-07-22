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
import com.twidere.twiderex.viewmodel.compose.ComposeType

@AppRoute
interface Root {
    val Home: String
    val HomeTimeline: String
    val Notification: String
    val Mentions: String
    val Me: String

    interface Draft {
        val List: String
        fun Compose(draftId: String)
    }

    interface SignIn {
        val General: String
        fun Twitter(consumerKey: String, consumerSecret: String)
        val Mastodon: String
        interface Web {
            fun Twitter(url: String)
        }
    }
    fun User(userKey: MicroBlogKey)

    interface Media {
        fun Status(statusKey: MicroBlogKey, selectedIndex: Int?)
        fun Raw(url: String)
        fun Pure(belongToKey: MicroBlogKey, selectedIndex: Int?)
    }

    interface Search {
        val Home: String
        fun Result(keyword: String)
        fun Input(keyword: String?)
    }

    interface Compose {
        fun Home(composeType: ComposeType?, statusKey: MicroBlogKey?)
        interface Search {
            val User: String
        }
    }

    fun Following(userKey: MicroBlogKey)
    fun Followers(userKey: MicroBlogKey)

    interface Settings {
        val Home: String
        val Appearance: String
        val Display: String
        val Storage: String
        val About: String
        val AccountManagement: String
        val Misc: String
        val Notification: String
        val Layout: String
        fun AccountNotification(accountKey: MicroBlogKey)
    }

    interface DeepLink {
        interface Twitter {
            val User: String
            val Status: String
        }
        fun Draft(id: String): String
        fun Compose(composeType: ComposeType, statusKey: MicroBlogKey?)
        fun Conversation(conversationKey: MicroBlogKey)
    }

    fun Status(statusKey: MicroBlogKey)

    interface Mastodon {
        fun Hashtag(keyword: String)
        val Notification: String

        interface Compose {
            val Hashtag: String
        }
    }

    interface Lists {
        val Home: String
        val MastodonCreateDialog: String
        val TwitterCreate: String
        fun TwitterEdit(listKey: MicroBlogKey)
        fun Timeline(listKey: MicroBlogKey)
        fun Members(listKey: MicroBlogKey, owned: Boolean?)
        fun Subscribers(listKey: MicroBlogKey)
        fun AddMembers(listKey: MicroBlogKey)
    }

    interface Messages {
        val Home: String
        fun Conversation(conversationKey: MicroBlogKey)
        val NewConversation: String
    }
}
