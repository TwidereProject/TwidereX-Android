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
package com.twidere.twiderex.navigation

import com.twidere.route.processor.AppRoute
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.ComposeType
import com.twidere.twiderex.model.enums.MediaType

@AppRoute
expect object Root {
    val Home: String
    val HomeTimeline: String
    val Notification: String
    val Mentions: String
    val Me: String

    object Draft {
        val List: String

        object Compose : IRoute {
            operator fun invoke(draftId: String): String
        }
    }

    object SignIn {
        val General: String

        object Twitter : IRoute {
            operator fun invoke(consumerKey: String, consumerSecret: String): String
        }

        val Mastodon: String

        object Web {
            object Twitter : IRoute {
                operator fun invoke(target: String): String
            }
        }
    }

    object User : IRoute {
        operator fun invoke(userKey: MicroBlogKey): String
    }

    object Media {
        object Status : IRoute {
            operator fun invoke(statusKey: MicroBlogKey, selectedIndex: Int?): String
        }

        object Raw : IRoute {
            operator fun invoke(type: MediaType, url: String): String
        }

        object Pure : IRoute {
            operator fun invoke(belongToKey: MicroBlogKey, selectedIndex: Int?): String
        }
    }

    object Search {
        val Home: String

        object Result : IRoute {
            operator fun invoke(keyword: String): String
        }

        object Input : IRoute {
            operator fun invoke(keyword: String?): String
        }
    }

    object Compose {
        object Home : IRoute {
            operator fun invoke(composeType: ComposeType?, statusKey: MicroBlogKey?): String
        }

        object Search {
            val User: String
        }
    }

    object Following : IRoute {
        operator fun invoke(userKey: MicroBlogKey): String
    }

    object Followers : IRoute {
        operator fun invoke(userKey: MicroBlogKey): String
    }

    object Settings {
        val Home: String
        val Appearance: String
        val Display: String
        val Storage: String
        val About: String
        val AccountManagement: String
        val Misc: String
        val Notification: String
        val Layout: String

        object AccountNotification : IRoute {
            operator fun invoke(accountKey: MicroBlogKey): String
        }
    }

    object Status : IRoute {
        operator fun invoke(statusKey: MicroBlogKey): String
    }

    object Mastodon {
        object Hashtag : IRoute {
            operator fun invoke(keyword: String): String
        }

        val Notification: String
        val FederatedTimeline: String
        val LocalTimeline: String

        object Compose {
            val Hashtag: String
        }
    }

    object Lists {
        val Home: String
        val MastodonCreateDialog: String
        val TwitterCreate: String

        object TwitterEdit : IRoute {
            operator fun invoke(listKey: MicroBlogKey): String
        }

        object Timeline : IRoute {
            operator fun invoke(listKey: MicroBlogKey): String
        }

        object Members : IRoute {
            operator fun invoke(listKey: MicroBlogKey, owned: Boolean?): String
        }

        object Subscribers : IRoute {
            operator fun invoke(listKey: MicroBlogKey): String
        }

        object AddMembers : IRoute {
            operator fun invoke(listKey: MicroBlogKey): String
        }
    }

    object Messages {
        val Home: String

        object Conversation : IRoute {
            operator fun invoke(conversationKey: MicroBlogKey): String
        }

        val NewConversation: String
    }

    object Gif {
        val Home: String
    }
}
