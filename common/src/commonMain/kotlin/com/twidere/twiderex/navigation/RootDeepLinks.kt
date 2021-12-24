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

/**
 * if deeplink has the same parameters with route in Root.kt,
 * make it's name the same to route parameters in Root.kt too
 */
const val twidereXSchema = "twiderex"

@AppRoute(
    schema = twidereXSchema
)
expect object RootDeepLinks {
    object Twitter {
        object User : IRoute {
            operator fun invoke(screenName: String): String
        }

        object Status : IRoute {
            operator fun invoke(statusId: String): String
        }
    }

    object Mastodon {
        object Hashtag : IRoute {
            operator fun invoke(keyword: String): String
        }
    }

    object User : IRoute {
        operator fun invoke(userKey: MicroBlogKey): String
    }

    object Status : IRoute {
        operator fun invoke(statusKey: MicroBlogKey): String
    }

    object Search : IRoute {
        operator fun invoke(keyword: String): String
    }

    val SignIn: String

    object Draft : IRoute {
        operator fun invoke(draftId: String): String
    }

    object Compose : IRoute {
        operator fun invoke(composeType: ComposeType?, statusKey: MicroBlogKey?): String
    }

    object Conversation : IRoute {
        operator fun invoke(conversationKey: MicroBlogKey): String
    }

    object Callback {
        object SignIn {
            val Mastodon: String
            val Twitter: String
        }
    }
}
