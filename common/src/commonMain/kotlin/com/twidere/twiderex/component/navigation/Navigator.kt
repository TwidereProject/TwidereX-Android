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
// import com.twidere.twiderex.navigation.twidereXSchema
import com.twidere.twiderex.twitterHosts
import moe.tlaster.precompose.navigation.NavOptions


// interface Navigator {
//   fun user(user: UiUser, navOptions: NavOptions? = null) {}
//   fun status(status: UiStatus, navOptions: NavOptions? = null) {}
//   fun media(
//     statusKey: MicroBlogKey,
//     selectedIndex: Int = 0,
//     navOptions: NavOptions? = null
//   ) {
//   }
//
//   fun search(keyword: String) {}
//   fun compose(
//     composeType: ComposeType,
//     statusKey: MicroBlogKey? = null,
//     navOptions: NavOptions? = null
//   ) {
//   }
//
//   fun openLink(it: String, deepLink: Boolean = true) {}
//   suspend fun twitterSignInWeb(target: String): String = ""
//   fun searchInput(initial: String? = null) {}
//   fun hashtag(name: String) {}
//   fun goBack() {}
//
//   fun navigate(route: String, options: NavOptions? = null)
//
//   suspend fun navigateForResult(route: String, options: NavOptions? = null): Any?
//
//   fun goBackWith(result: Any? = null)
// }
//
// class Navigator(
//   private val navController: moe.tlaster.precompose.navigation.Navigator,
//   private val remoteNavigator: RemoteNavigator,
// ) : Navigator {
//   override fun user(user: UiUser, navOptions: NavOptions?) {
//     navController.navigate(Root.User(user.userKey), navOptions)
//   }
//
//   override fun status(status: UiStatus, navOptions: NavOptions?) {
//     val statusKey = when (status.platformType) {
//       PlatformType.Twitter -> status.statusKey
//       PlatformType.StatusNet -> TODO()
//       PlatformType.Fanfou -> TODO()
//       PlatformType.Mastodon -> {
//         if (status.mastodonExtra != null) {
//           when (status.mastodonExtra.type) {
//             MastodonStatusType.Status -> status.statusKey
//             MastodonStatusType.NotificationFollow, MastodonStatusType.NotificationFollowRequest -> null
//             else -> status.referenceStatus[ReferenceType.MastodonNotification]?.statusKey
//           }
//         } else {
//           status.statusKey
//         }
//       }
//     }
//     if (statusKey != null) {
//       navController.navigate(
//         Root.Status(statusKey),
//         navOptions
//       )
//     }
//   }
//
//   override fun media(
//     statusKey: MicroBlogKey,
//     selectedIndex: Int,
//     navOptions: NavOptions?
//   ) {
//     navController.navigate(Root.Media.Status(statusKey, selectedIndex), navOptions)
//   }
//
//   override fun search(keyword: String) {
//     navController.navigate(Root.Search.Result(keyword))
//   }
//
//   override fun searchInput(initial: String?) {
//     navController.navigate(
//       Root.Search.Input(initial),
//     )
//   }
//
//   override fun compose(
//     composeType: ComposeType,
//     statusKey: MicroBlogKey?,
//     navOptions: NavOptions?
//   ) {
//     navController.navigate(Root.Compose.Home(composeType, statusKey))
//   }
//
//   override fun openLink(it: String, deepLink: Boolean) {
//     if ((it.contains(twidereXSchema) || isTwitterDeeplink(it)) && deepLink) {
//       navController.navigate(it)
//     } else {
//       remoteNavigator.openDeepLink(it)
//     }
//   }
//
//   private fun isTwitterDeeplink(url: String): Boolean {
//     twitterHosts.forEach {
//       if (url.startsWith(it) && url.length > it.length) {
//         return true
//       }
//     }
//     return false
//   }
//
//   override suspend fun twitterSignInWeb(target: String): String {
//     clearCookie()
//     return navController.navigateForResult(
//       Root.SignIn.Web.Twitter(target)
//     ).toString()
//   }
//
//   override fun hashtag(name: String) {
//     navController.navigate(Root.Mastodon.Hashtag(name))
//   }
//
//   override fun goBack() {
//     navController.goBack()
//   }
//
//   override fun navigate(route: String, options: NavOptions?) {
//     navController.navigate(route, options)
//   }
//
//   override suspend fun navigateForResult(route: String, options: NavOptions?): Any? {
//     return navController.navigateForResult(route, options)
//   }
//
//   override fun goBackWith(result: Any?) {
//     navController.goBackWith(result)
//   }
// }
