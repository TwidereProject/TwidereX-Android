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
import com.twidere.twiderex.model.enums.MediaType
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
        fun Compose(draftId: String): String
    }

    interface SignIn {
        val General: String
        fun Twitter(consumerKey: String, consumerSecret: String): String
        val Mastodon: String
        interface Web {
            fun Twitter(target: String): String
        }
    }
    fun User(userKey: MicroBlogKey): String

    interface Media {
        fun Status(statusKey: MicroBlogKey, selectedIndex: Int?): String
        fun Raw(type: MediaType, url: String): String
        fun Pure(belongToKey: MicroBlogKey, selectedIndex: Int?): String
    }

    interface Search {
        val Home: String
        fun Result(keyword: String): String
        fun Input(keyword: String?): String
    }

    interface Compose {
        fun Home(composeType: ComposeType?, statusKey: MicroBlogKey?): String
        interface Search {
            val User: String
        }
    }

    fun Following(userKey: MicroBlogKey): String
    fun Followers(userKey: MicroBlogKey): String

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
        fun AccountNotification(accountKey: MicroBlogKey): String
    }

    fun Status(statusKey: MicroBlogKey): String

    interface Mastodon {
        fun Hashtag(keyword: String): String
        val Notification: String
        val FederatedTimeline: String
        val LocalTimeline: String

        interface Compose {
            val Hashtag: String
        }
    }

    interface Lists {
        val Home: String
        val MastodonCreateDialog: String
        val TwitterCreate: String
        fun TwitterEdit(listKey: MicroBlogKey): String
        fun Timeline(listKey: MicroBlogKey): String
        fun Members(listKey: MicroBlogKey, owned: Boolean?): String
        fun Subscribers(listKey: MicroBlogKey): String
        fun AddMembers(listKey: MicroBlogKey): String
    }

    interface Messages {
        val Home: String
        fun Conversation(conversationKey: MicroBlogKey): String
        val NewConversation: String
    }
}
