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

import io.github.seiko.precompose.annotation.GeneratedRoute
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.RouteBuilder

@Suppress("NO_ACTUAL_FOR_EXPECT")
@GeneratedRoute
expect fun RouteBuilder.twidereRoute(navigator: Navigator)

object Root {
  const val Home = "/Root/Home"
  const val HomeTimeline = "/Root/HomeTimeline"
  const val Notification = "/Root/Notification"
  const val Mentions = "/Root/Mentions"
  const val Me = "/Root/Me"
  const val Empty = ""
  object Draft {
    const val List = "/Root/Draft/List"
    object Compose {
      const val route = "/Root/Draft/Compose/{draftId}"
      operator fun invoke(draftId: String) = "/Root/Draft/Compose/${java.net.URLEncoder.encode(draftId, "UTF-8")}"
    }
  }
  object SignIn {
    const val General = "/Root/SignIn/General"
    object Twitter {
      const val route = "/Root/SignIn/Twitter/{consumerKey}/{consumerSecret}"
      operator fun invoke(consumerKey: String, consumerSecret: String) = "/Root/SignIn/Twitter/${java.net.URLEncoder.encode(consumerKey, "UTF-8")}/${java.net.URLEncoder.encode(consumerSecret, "UTF-8")}"
    }
    const val Mastodon = "/Root/SignIn/Mastodon"
    object Web {
      object Twitter {
        const val route = "/Root/SignIn/Web/Twitter/{target}"
        operator fun invoke(target: String) = "/Root/SignIn/Web/Twitter/${java.net.URLEncoder.encode(target, "UTF-8")}"
      }
    }
  }
  object User {
    const val route = "/Root/User/{userKey}"
    operator fun invoke(userKey: com.twidere.twiderex.model.MicroBlogKey) = "/Root/User/$userKey"
  }
  object Media {
    object Status {
      const val route = "/Root/Media/Status/{statusKey}"
      operator fun invoke(statusKey: com.twidere.twiderex.model.MicroBlogKey, selectedIndex: Int?) = "/Root/Media/Status/$statusKey?selectedIndex=$selectedIndex"
    }
    object Raw {
      const val route = "/Root/Media/Raw/{type}/{url}"
      operator fun invoke(type: com.twidere.twiderex.model.enums.MediaType, url: String) = "/Root/Media/Raw/$type/${java.net.URLEncoder.encode(url, "UTF-8")}"
    }
    object Pure {
      const val route = "/Root/Media/Pure/{belongToKey}"
      operator fun invoke(belongToKey: com.twidere.twiderex.model.MicroBlogKey, selectedIndex: Int?) = "/Root/Media/Pure/$belongToKey?selectedIndex=$selectedIndex"
    }
  }
  object Search {
    const val Home = "/Root/Search/Home"
    object Result {
      const val route = "/Root/Search/Result/{keyword}"
      operator fun invoke(keyword: String) = "/Root/Search/Result/${java.net.URLEncoder.encode(keyword, "UTF-8")}"
    }
    object Input {
      const val route = "/Root/Search/Input"
      operator fun invoke(keyword: String?) = "/Root/Search/Input?keyword=${java.net.URLEncoder.encode(if (keyword == null) "" else keyword, "UTF-8")}"
    }
  }
  object Compose {
    object Home {
      const val route = "/Root/Compose/Home"
      operator fun invoke(composeType: com.twidere.twiderex.model.enums.ComposeType?, statusKey: com.twidere.twiderex.model.MicroBlogKey?) = "/Root/Compose/Home?composeType=$composeType&statusKey=$statusKey"
    }
    object Search {
      const val User = "/Root/Compose/Search/User"
    }
  }
  object Following {
    const val route = "/Root/Following/{userKey}"
    operator fun invoke(userKey: com.twidere.twiderex.model.MicroBlogKey) = "/Root/Following/$userKey"
  }
  object Followers {
    const val route = "/Root/Followers/{userKey}"
    operator fun invoke(userKey: com.twidere.twiderex.model.MicroBlogKey) = "/Root/Followers/$userKey"
  }
  object Settings {
    const val Home = "/Root/Settings/Home"
    const val Appearance = "/Root/Settings/Appearance"
    const val Display = "/Root/Settings/Display"
    const val Storage = "/Root/Settings/Storage"
    const val About = "/Root/Settings/About"
    const val AccountManagement = "/Root/Settings/AccountManagement"
    const val Misc = "/Root/Settings/Misc"
    const val Notification = "/Root/Settings/Notification"
    const val Layout = "/Root/Settings/Layout"
    object AccountNotification {
      const val route = "/Root/Settings/AccountNotification/{accountKey}"
      operator fun invoke(accountKey: com.twidere.twiderex.model.MicroBlogKey) = "/Root/Settings/AccountNotification/$accountKey"
    }
  }
  object Status {
    const val route = "/Root/Status/{statusKey}"
    operator fun invoke(statusKey: com.twidere.twiderex.model.MicroBlogKey) = "/Root/Status/$statusKey"
  }
  object Mastodon {
    object Hashtag {
      const val route = "/Root/Mastodon/Hashtag/{keyword}"
      operator fun invoke(keyword: String) = "/Root/Mastodon/Hashtag/${java.net.URLEncoder.encode(keyword, "UTF-8")}"
    }
    const val Notification = "/Root/Mastodon/Notification"
    const val FederatedTimeline = "/Root/Mastodon/FederatedTimeline"
    const val LocalTimeline = "/Root/Mastodon/LocalTimeline"
    object Compose {
      const val Hashtag = "/Root/Mastodon/Compose/Hashtag"
    }
  }
  object Lists {
    const val Home = "/Root/Lists/Home"
    const val MastodonCreateDialog = "/Root/Lists/MastodonCreateDialog"
    const val TwitterCreate = "/Root/Lists/TwitterCreate"
    object TwitterEdit {
      const val route = "/Root/Lists/TwitterEdit/{listKey}"
      operator fun invoke(listKey: com.twidere.twiderex.model.MicroBlogKey) = "/Root/Lists/TwitterEdit/$listKey"
    }
    object Timeline {
      const val route = "/Root/Lists/Timeline/{listKey}"
      operator fun invoke(listKey: com.twidere.twiderex.model.MicroBlogKey) = "/Root/Lists/Timeline/$listKey"
    }
    object Members {
      const val route = "/Root/Lists/Members/{listKey}"
      operator fun invoke(listKey: com.twidere.twiderex.model.MicroBlogKey, owned: Boolean?) = "/Root/Lists/Members/$listKey?owned=$owned"
    }
    object Subscribers {
      const val route = "/Root/Lists/Subscribers/{listKey}"
      operator fun invoke(listKey: com.twidere.twiderex.model.MicroBlogKey) = "/Root/Lists/Subscribers/$listKey"
    }
    object AddMembers {
      const val route = "/Root/Lists/AddMembers/{listKey}"
      operator fun invoke(listKey: com.twidere.twiderex.model.MicroBlogKey) = "/Root/Lists/AddMembers/$listKey"
    }
  }
  object Messages {
    const val Home = "/Root/Messages/Home"
    object Conversation {
      const val route = "/Root/Messages/Conversation/{conversationKey}"
      operator fun invoke(conversationKey: com.twidere.twiderex.model.MicroBlogKey) = "/Root/Messages/Conversation/$conversationKey"
    }
    const val NewConversation = "/Root/Messages/NewConversation"
  }
  object Gif {
    const val Home = "/Root/Gif/Home"
  }
}
