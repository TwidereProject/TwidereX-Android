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

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalActiveAccountViewModel
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.utils.LocalPlatformResolver
//
//
// fun RouteBuilder.authorizedScene(
//   route: String,
//   deepLinks: List<String> = emptyList(),
//   navTransition: NavTransition? = null,
//   content: @Composable (BackStackEntry) -> Unit,
// ) {
//   scene(route, deepLinks, navTransition) {
//     RequireAuthorization {
//       content.invoke(it)
//     }
//   }
// }
//
// fun RouteBuilder.authorizedDialog(
//   route: String,
//   content: @Composable (BackStackEntry) -> Unit,
// ) {
//   dialog(route) {
//     RequireAuthorization {
//       content.invoke(it)
//     }
//   }
// }
//
@Composable
fun ProvidePlatformType(
  key: MicroBlogKey,
  provider: suspend () -> PlatformType?,
  content: @Composable (platformType: PlatformType) -> Unit,
) {
  var platformType by rememberSaveable {
    mutableStateOf<PlatformType?>(null)
  }
  val account = LocalActiveAccount.current
  LaunchedEffect(key) {
    platformType = provider.invoke() ?: account?.type
  }
  platformType?.let {
    content.invoke(it)
  } ?: run {
    TwidereScene {
      Scaffold {
      }
    }
  }
}

@Composable
fun ProvideStatusPlatform(
  statusKey: MicroBlogKey,
  content: @Composable (platformType: PlatformType) -> Unit,
) {
  val platformResolver = LocalPlatformResolver.current
  val account = LocalActiveAccount.current ?: return
  ProvidePlatformType(
    key = statusKey,
    provider = {
      platformResolver.resolveStatus(statusKey = statusKey, account.accountKey)
    },
    content = content
  )
}

@Composable
fun ProvideUserPlatform(
  userKey: MicroBlogKey,
  content: @Composable (platformType: PlatformType) -> Unit,
) {
  val platformResolver = LocalPlatformResolver.current
  ProvidePlatformType(
    key = userKey,
    provider = {
      platformResolver.resolveUser(userKey = userKey)
    },
    content = content
  )
}

@Composable
fun RequirePlatformAccount(
  platformType: PlatformType,
  fallback: () -> Unit = {},
  content: @Composable () -> Unit,
) {
  var account = LocalActiveAccount.current ?: run {
    fallback.invoke()
    return
  }
  if (account.type != platformType) {
    account = LocalActiveAccountViewModel.current.getTargetPlatformDefault(platformType)
      ?: run {
        fallback.invoke()
        return
      }
  }
  CompositionLocalProvider(
    LocalActiveAccount provides account
  ) {
    content.invoke()
  }
}
//
// /**
//  * Note: Path/Query key must be exactly the same as function's parameter's name in Root/RootDeepLinks
//  */
// @OptIn(ExperimentalAnimationApi::class)
// fun RouteBuilder.route(navigator: Navigator) {
//   authorizedScene(
//     Root.Home,
//     deepLinks = twitterHosts.map { "$it/*" }
//   ) {
//     HomeScene(
//       navigator = navigator,
//     )
//   }
//
//   authorizedScene(Root.Mastodon.Notification) {
//     MastodonNotificationScene(
//       navigator = navigator,
//     )
//   }
//
//   authorizedScene(Root.Mastodon.FederatedTimeline) {
//     FederatedTimelineScene(
//       navigator = navigator
//     )
//   }
//
//   authorizedScene(Root.Mastodon.LocalTimeline) {
//     LocalTimelineScene(
//       navigator = navigator,
//     )
//   }
//
//   authorizedScene(Root.Me) {
//     MeScene(
//       navigator = navigator,
//     )
//   }
//
//   authorizedScene(Root.Mentions) {
//     MentionScene(
//       navigator = navigator,
//     )
//   }
//
//   authorizedScene(Root.HomeTimeline) {
//     HomeTimelineScene(
//       navigator = navigator,
//     )
//   }
//
//   scene(
//     Root.SignIn.General,
//     deepLinks = listOf(
//       RootDeepLinks.SignIn
//     ),
//   ) {
//     SignInScene()
//   }
//
//   scene(
//     Root.SignIn.Twitter,
//   ) { backStackEntry ->
//     val consumerKey = backStackEntry.path<String>("consumerKey")
//     val consumerSecret = backStackEntry.path<String>("consumerSecret")
//     if (consumerKey != null && consumerSecret != null) {
//       TwitterSignInScene(
//         consumerKey = consumerKey,
//         consumerSecret = consumerSecret,
//         navigator = navigator,
//       )
//     }
//   }
//
//   scene(Root.SignIn.Mastodon) {
//     MastodonSignInScene()
//   }
//
//   // scene(
//   //     "signin/mastodon/web/{target}",
//   // ) { backStackEntry ->
//   //     backStackEntry.path<String>("target")?.let {
//   //         MastodonWebSignInScene(target = URLDecoder.decode(it, "UTF-8"))
//   //     }
//   // }
//
//   authorizedScene(
//     RootDeepLinks.Twitter.User,
//     deepLinks = twitterHosts.map {
//       "$it/{screenName}"
//     }
//   ) { backStackEntry ->
//     backStackEntry.path<String>("screenName")?.let { screenName ->
//       RequirePlatformAccount(
//         platformType = PlatformType.Twitter,
//         fallback = {
//           navigator.openLink("https://twitter.com/$screenName", deepLink = false)
//           navigator.goBack()
//         }
//       ) {
//         TwitterUserScene(screenName = screenName) {
//           navigator.user(
//             user = it,
//             NavOptions(popUpTo = PopUpTo(RootDeepLinks.Twitter.User.route, inclusive = true))
//           )
//         }
//       }
//     }
//   }
//
//   authorizedScene(
//     Root.User,
//     deepLinks = listOf(
//       RootDeepLinks.User
//     )
//   ) { backStackEntry ->
//     backStackEntry.path<String>("userKey")?.let {
//       MicroBlogKey.valueOf(it)
//     }?.let { userKey ->
//       ProvideUserPlatform(userKey = userKey) { platformType ->
//         RequirePlatformAccount(platformType = platformType) {
//           UserScene(
//             userKey = userKey,
//             navigator = navigator,
//           )
//         }
//       }
//     }
//   }
//
//   authorizedScene(
//     Root.Mastodon.Hashtag,
//     deepLinks = listOf(
//       RootDeepLinks.Mastodon.Hashtag
//     )
//   ) { backStackEntry ->
//     backStackEntry.path<String>("keyword")?.let {
//       MastodonHashtagScene(
//         keyword = it,
//         navigator = navigator,
//       )
//     }
//   }
//
//   authorizedScene(
//     Root.Status,
//     deepLinks = listOf(
//       RootDeepLinks.Status,
//     )
//   ) { backStackEntry ->
//     backStackEntry.path<String>("statusKey")?.let {
//       MicroBlogKey.valueOf(it)
//     }?.let { statusKey ->
//       ProvideStatusPlatform(statusKey = statusKey) { platform ->
//         RequirePlatformAccount(platformType = platform) {
//           StatusScene(
//             statusKey = statusKey,
//             navigator = navigator,
//           )
//         }
//       }
//     }
//   }
//
//   authorizedScene(
//     RootDeepLinks.Twitter.Status,
//     deepLinks = twitterHosts.map {
//       "$it/{screenName}/status/{statusId:[0-9]+}"
//     }
//   ) { backStackEntry ->
//     backStackEntry.path<String>("statusId")?.let { statusId ->
//       RequirePlatformAccount(
//         platformType = PlatformType.Twitter,
//         fallback = {
//           navigator.openLink(
//             "https://twitter.com/${backStackEntry.path<String>("screenName")}/status/$statusId",
//             deepLink = false
//           )
//           navigator.goBack()
//         }
//       ) {
//         StatusScene(
//           statusKey = MicroBlogKey.twitter(statusId),
//           navigator = navigator,
//         )
//       }
//     }
//   }
//
//   authorizedDialog(
//     Root.Media.Status,
//   ) { backStackEntry ->
//     backStackEntry.path<String>("statusKey")?.let {
//       MicroBlogKey.valueOf(it)
//     }?.let { statusKey ->
//       ProvideStatusPlatform(statusKey = statusKey) { platformType ->
//         RequirePlatformAccount(platformType = platformType) {
//           val selectedIndex = backStackEntry.query("selectedIndex", 0) ?: 0
//           PlatformStatusMediaScene(
//             statusKey = statusKey,
//             selectedIndex = selectedIndex,
//             navigator = navigator,
//           )
//         }
//       }
//     }
//   }
//
//   authorizedDialog(
//     Root.Media.Pure,
//   ) { backStackEntry ->
//     backStackEntry.path<String>("belongToKey")?.let {
//       MicroBlogKey.valueOf(it)
//     }?.let { belongToKey ->
//       val selectedIndex = backStackEntry.query("selectedIndex", 0) ?: 0
//       PlatformPureMediaScene(
//         belongToKey = belongToKey,
//         selectedIndex = selectedIndex
//       )
//     }
//   }
//
//   authorizedDialog(
//     Root.Media.Raw,
//   ) { backStackEntry ->
//     val url = backStackEntry.path<String>("url")?.let {
//       URLDecoder.decode(it, "UTF-8")
//     }
//     val type = MediaType.valueOf(backStackEntry.path<String>("type") ?: MediaType.photo.name)
//     url?.let {
//       PlatformRawMediaScene(url = it, type = type)
//     }
//   }
//
//   authorizedScene(Root.Search.Home) {
//     com.twidere.twiderex.scenes.home.SearchScene(
//       navigator = navigator,
//     )
//   }
//
//   authorizedScene(
//     Root.Search.Input,
//     navTransition = NavTransition(
//       createTransition = fadeIn(),
//       destroyTransition = fadeOut(),
//       pauseTransition = fadeOut(),
//       resumeTransition = fadeIn(),
//     ),
//   ) { backStackEntry ->
//     SearchInputScene(
//       backStackEntry.query<String>("keyword")?.let { URLDecoder.decode(it, "UTF-8") },
//       navigator,
//     )
//   }
//
//   authorizedScene(
//     Root.Search.Result,
//     deepLinks = twitterHosts.map {
//       "$it/search?q={keyword}"
//     } + RootDeepLinks.Search,
//     navTransition = NavTransition(
//       createTransition = fadeIn(),
//       destroyTransition = fadeOut(),
//       pauseTransition = fadeOut(),
//       resumeTransition = fadeIn(),
//     ),
//   ) { backStackEntry ->
//     backStackEntry.path<String>("keyword")?.takeIf { it.isNotEmpty() }?.let {
//       SearchScene(
//         keyword = URLDecoder.decode(it, "UTF-8"),
//         navigator = navigator,
//       )
//     }
//   }
//
//   authorizedScene(
//     Root.Compose.Home,
//     navTransition = NavTransition(
//       createTransition = slideInVertically(initialOffsetY = { it }),
//       destroyTransition = slideOutVertically(targetOffsetY = { it }),
//       pauseTransition = scaleOut(targetScale = 0.9f),
//       resumeTransition = scaleIn(initialScale = 0.9f),
//     ),
//     deepLinks = listOf(
//       RootDeepLinks.Compose
//     )
//   ) { backStackEntry ->
//     val type = backStackEntry.query<String>("composeType")?.let {
//       enumValueOf(it)
//     } ?: ComposeType.New
//     val statusKey = backStackEntry.query<String>("statusKey")
//       ?.takeIf { it.isNotEmpty() }
//       ?.let { MicroBlogKey.valueOf(it) }
//     if (statusKey != null) {
//       ProvideStatusPlatform(statusKey = statusKey) { platformType ->
//         RequirePlatformAccount(platformType = platformType) {
//           ComposeScene(statusKey, type, navigator)
//         }
//       }
//     } else {
//       ComposeScene(statusKey, type, navigator)
//     }
//   }
//
//   authorizedScene(
//     Root.Followers,
//   ) { backStackEntry ->
//     backStackEntry.path<String>("userKey")?.let {
//       MicroBlogKey.valueOf(it)
//     }?.let {
//       FollowersScene(
//         userKey = it,
//         navigator = navigator,
//       )
//     }
//   }
//
//   authorizedScene(
//     Root.Following,
//   ) { backStackEntry ->
//     backStackEntry.path<String>("userKey")?.let {
//       MicroBlogKey.valueOf(it)
//     }?.let {
//       FollowingScene(
//         userKey = it,
//         navigator = navigator,
//       )
//     }
//   }
//
//   scene(Root.Settings.Home) {
//     SettingsScene()
//   }
//
//   scene(Root.Settings.Appearance) {
//     AppearanceScene()
//   }
//
//   scene(Root.Settings.Display) {
//     DisplayScene()
//   }
//
//   scene(Root.Settings.Storage) {
//     StorageScene()
//   }
//
//   scene(Root.Settings.AccountManagement) {
//     AccountManagementScene(
//       navigator = navigator,
//     )
//   }
//
//   scene(Root.Settings.Misc) {
//     MiscScene(
//       navigator = navigator,
//     )
//   }
//
//   scene(Root.Settings.Notification) {
//     NotificationScene(
//       navigator = navigator,
//     )
//   }
//
//   authorizedScene(Root.Settings.Layout) {
//     LayoutScene(
//       navigator = navigator,
//     )
//   }
//
//   scene(
//     Root.Settings.AccountNotification
//   ) {
//     it.path<String>("accountKey", null)?.let {
//       MicroBlogKey.valueOf(it)
//     }?.let {
//       AccountNotificationScene(
//         accountKey = it,
//         navigator = navigator,
//       )
//     }
//   }
//
//   scene(Root.Settings.About) {
//     AboutScene(
//       navigator = navigator,
//     )
//   }
//
//   scene(Root.Draft.List) {
//     DraftListScene()
//   }
//
//   scene(
//     Root.Draft.Compose,
//     deepLinks = listOf(
//       RootDeepLinks.Draft,
//     )
//   ) { backStackEntry ->
//     backStackEntry.path<String>("draftId")?.let {
//       DraftComposeScene(
//         draftId = it,
//         navigator = navigator,
//       )
//     }
//   }
//
//   authorizedScene(Root.Compose.Search.User) {
//     ComposeSearchUserScene(
//       navigator = navigator,
//     )
//   }
//
//   authorizedScene(Root.Mastodon.Compose.Hashtag) {
//     ComposeSearchHashtagScene()
//   }
//
//   authorizedScene(Root.Lists.Home) {
//     ListsScene()
//   }
//
//   authorizedDialog(Root.Lists.MastodonCreateDialog) {
//     val navController = LocalNavController.current
//     MastodonListsCreateDialog(onDismissRequest = { navController.goBack() })
//   }
//
//   authorizedScene(Root.Lists.TwitterCreate) {
//     TwitterListsCreateScene()
//   }
//
//   authorizedScene(
//     Root.Lists.TwitterEdit,
//   ) { backStackEntry ->
//     backStackEntry.path<String>("listKey")?.let {
//       TwitterListsEditScene(listKey = MicroBlogKey.valueOf(it))
//     }
//   }
//
//   authorizedScene(
//     Root.Lists.Timeline,
//   ) { backStackEntry ->
//     backStackEntry.path<String>("listKey")?.let {
//       ListTimeLineScene(
//         listKey = MicroBlogKey.valueOf(it),
//         navigator = navigator,
//       )
//     }
//   }
//
//   authorizedScene(
//     Root.Lists.Members,
//   ) { backStackEntry ->
//     backStackEntry.path<String>("listKey")?.let {
//       ListsMembersScene(listKey = MicroBlogKey.valueOf(it), backStackEntry.query<Boolean>("owned") ?: false, navigator)
//     }
//   }
//
//   authorizedScene(
//     Root.Lists.Subscribers,
//   ) { backStackEntry ->
//     backStackEntry.path<String>("listKey")?.let {
//       ListsSubscribersScene(
//         listKey = MicroBlogKey.valueOf(it),
//         navigator = navigator,
//       )
//     }
//   }
//
//   authorizedScene(
//     Root.Lists.AddMembers,
//   ) { backStackEntry ->
//     backStackEntry.path<String>("listKey")?.let {
//       ListsAddMembersScene(
//         listKey = MicroBlogKey.valueOf(it),
//         navigator = navigator,
//       )
//     }
//   }
//
//   authorizedScene(Root.Messages.Home) {
//     DMConversationListScene(
//       navigator = navigator,
//     )
//   }
//
//   authorizedScene(Root.Messages.NewConversation) {
//     DMNewConversationScene(
//       navigator = navigator,
//     )
//   }
//
//   authorizedScene(
//     Root.Messages.Conversation,
//     deepLinks = listOf(
//       RootDeepLinks.Conversation
//     )
//   ) { backStackEntry ->
//     backStackEntry.path<String>("conversationKey")?.let {
//       DMConversationScene(
//         conversationKey = MicroBlogKey.valueOf(it),
//         navigator = navigator,
//       )
//     }
//   }
//
//   authorizedScene(Root.Gif.Home) {
//     GifScene()
//   }
//
//   platformScene()
// }
//
// expect fun RouteBuilder.platformScene()
