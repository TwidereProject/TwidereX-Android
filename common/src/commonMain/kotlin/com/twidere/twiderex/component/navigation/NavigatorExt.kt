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
package com.twidere.twiderex.component.navigation

import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.kmp.RemoteNavigator
import com.twidere.twiderex.kmp.clearCookie
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.ComposeType
import com.twidere.twiderex.model.enums.MastodonStatusType
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.enums.ReferenceType
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.navigation.Root
import com.twidere.twiderex.navigation.twidereXSchema
import com.twidere.twiderex.twitterHosts
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.Navigator

suspend fun Navigator.twitterSignInWeb(target: String): String {
  clearCookie()
  return navigateForResult(
    Root.SignIn.Web.Twitter(target)
  ).toString()
}

fun Navigator.status(
  status: UiStatus,
  navOptions: NavOptions? = null
) {
  val statusKey = when (status.platformType) {
    PlatformType.Twitter -> status.statusKey
    PlatformType.StatusNet -> TODO()
    PlatformType.Fanfou -> TODO()
    PlatformType.Mastodon -> {
      if (status.mastodonExtra != null) {
        when (status.mastodonExtra.type) {
          MastodonStatusType.Status -> status.statusKey
          MastodonStatusType.NotificationFollow, MastodonStatusType.NotificationFollowRequest -> null
          else -> status.referenceStatus[ReferenceType.MastodonNotification]?.statusKey
        }
      } else {
        status.statusKey
      }
    }
  }
  if (statusKey != null) {
    navigate(
      Root.Status(statusKey),
      navOptions
    )
  }
}

fun Navigator.media(
  statusKey: MicroBlogKey,
  selectedIndex: Int = 0,
  userKey: MicroBlogKey? = null,
  navOptions: NavOptions? = null,
) {
  navigate(Root.Media.Status(statusKey, selectedIndex, userKey), navOptions)
}

fun Navigator.searchInput(initial: String? = null) {
  navigate(
    Root.Search.Input(initial),
  )
}

fun Navigator.search(keyword: String) {
  navigate(Root.Search.Result(keyword))
}

fun Navigator.openLink(
  link: String,
  deepLink: Boolean = true,
  remoteNavigator: RemoteNavigator = get(),
) {
  if ((link.contains(twidereXSchema) || isTwitterDeeplink(link)) && deepLink) {
    navigate(link)
  } else {
    remoteNavigator.openDeepLink(link)
  }
}

fun Navigator.compose(
  composeType: ComposeType,
  statusKey: MicroBlogKey? = null,
  navOptions: NavOptions? = null,
) {
  navigate(Root.Compose.Home(composeType, statusKey), navOptions)
}

fun Navigator.hashtag(name: String) {
  navigate(Root.Mastodon.Hashtag(name))
}

fun Navigator.user(
  user: UiUser,
  navOptions: NavOptions? = null,
) {
  navigate(Root.User(user.userKey), navOptions)
}

private fun isTwitterDeeplink(url: String): Boolean {
  twitterHosts.forEach {
    if (url.startsWith(it) && url.length > it.length) {
      return true
    }
  }
  return false
}
