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

/**
 * if deeplink has the same parameters with route in Root.kt,
 * make it's name the same to route parameters in Root.kt too
 */
const val twidereXSchema = "twiderex"

object RootDeepLinks {
  object Twitter {
    object User {
      const val route = "$twidereXSchema://RootDeepLinks/Twitter/User/{screenName}"
      operator fun invoke(screenName: String) = "$twidereXSchema://RootDeepLinks/Twitter/User/${java.net.URLEncoder.encode(screenName, "UTF-8")}"
    }
    object Status {
      const val route = "$twidereXSchema://RootDeepLinks/Twitter/Status/{statusId}"
      operator fun invoke(statusId: String) = "$twidereXSchema://RootDeepLinks/Twitter/Status/${java.net.URLEncoder.encode(statusId, "UTF-8")}"
    }
  }
  object Mastodon {
    object Hashtag {
      const val route = "$twidereXSchema://RootDeepLinks/Mastodon/Hashtag/{keyword}"
      operator fun invoke(keyword: String) = "$twidereXSchema://RootDeepLinks/Mastodon/Hashtag/${java.net.URLEncoder.encode(keyword, "UTF-8")}"
    }
  }
  object User {
    const val route = "$twidereXSchema://RootDeepLinks/User/{userKey}"
    operator fun invoke(userKey: com.twidere.twiderex.model.MicroBlogKey) = "$twidereXSchema://RootDeepLinks/User/$userKey"
  }
  object Status {
    const val route = "$twidereXSchema://RootDeepLinks/Status/{statusKey}"
    operator fun invoke(statusKey: com.twidere.twiderex.model.MicroBlogKey) = "$twidereXSchema://RootDeepLinks/Status/$statusKey"
  }
  object Search {
    const val route = "$twidereXSchema://RootDeepLinks/Search/{keyword}"
    operator fun invoke(keyword: String) = "$twidereXSchema://RootDeepLinks/Search/${java.net.URLEncoder.encode(keyword, "UTF-8")}"
  }
  const val SignIn = "$twidereXSchema://RootDeepLinks/SignIn"
  object Draft {
    const val route = "$twidereXSchema://RootDeepLinks/Draft/{draftId}"
    operator fun invoke(draftId: String) = "$twidereXSchema://RootDeepLinks/Draft/${java.net.URLEncoder.encode(draftId, "UTF-8")}"
  }
  object Compose {
    const val route = "$twidereXSchema://RootDeepLinks/Compose"
    operator fun invoke(composeType: com.twidere.twiderex.model.enums.ComposeType?, statusKey: com.twidere.twiderex.model.MicroBlogKey?) = "$twidereXSchema://RootDeepLinks/Compose?composeType=$composeType&statusKey=$statusKey"
  }
  object Conversation {
    const val route = "$twidereXSchema://RootDeepLinks/Conversation/{conversationKey}"
    operator fun invoke(conversationKey: com.twidere.twiderex.model.MicroBlogKey) = "$twidereXSchema://RootDeepLinks/Conversation/$conversationKey"
  }
  object Callback {
    object SignIn {
      const val Mastodon = "$twidereXSchema://RootDeepLinks/Callback/SignIn/Mastodon"
      const val Twitter = "$twidereXSchema://RootDeepLinks/Callback/SignIn/Twitter"
    }
  }
}
