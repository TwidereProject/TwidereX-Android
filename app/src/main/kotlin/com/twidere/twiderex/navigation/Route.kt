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

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NamedNavArgument
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.fragment.dialog
import androidx.navigation.navDeepLink
import com.twidere.twiderex.component.requireAuthorization
import com.twidere.twiderex.dialog.TwitterWebSignInDialog
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.scenes.DraftListScene
import com.twidere.twiderex.scenes.HomeScene
import com.twidere.twiderex.scenes.RawMediaScene
import com.twidere.twiderex.scenes.SignInScene
import com.twidere.twiderex.scenes.StatusMediaScene
import com.twidere.twiderex.scenes.StatusScene
import com.twidere.twiderex.scenes.compose.ComposeScene
import com.twidere.twiderex.scenes.compose.ComposeSearchUserScene
import com.twidere.twiderex.scenes.compose.DraftComposeScene
import com.twidere.twiderex.scenes.mastodon.MastodonSignInScene
import com.twidere.twiderex.scenes.mastodon.MastodonWebSignInScene
import com.twidere.twiderex.scenes.search.SearchInputScene
import com.twidere.twiderex.scenes.search.SearchScene
import com.twidere.twiderex.scenes.settings.AboutScene
import com.twidere.twiderex.scenes.settings.AccountManagementScene
import com.twidere.twiderex.scenes.settings.AppearanceScene
import com.twidere.twiderex.scenes.settings.DisplayScene
import com.twidere.twiderex.scenes.settings.SettingsScene
import com.twidere.twiderex.scenes.twitter.TwitterSignInScene
import com.twidere.twiderex.scenes.twitter.TwitterWebSignInScene
import com.twidere.twiderex.scenes.twitter.user.TwitterUserScene
import com.twidere.twiderex.scenes.user.FollowersScene
import com.twidere.twiderex.scenes.user.FollowingScene
import com.twidere.twiderex.scenes.user.UserScene
import com.twidere.twiderex.twitterHosts
import com.twidere.twiderex.viewmodel.compose.ComposeType
import java.net.URLDecoder
import java.net.URLEncoder

val initialRoute = Route.Home
val twidereXSchema = "twiderex"

object Route {
    val Home = "home"

    object Draft {
        val List = "draft/list"
        fun Compose(draftId: String) = "draft/compose/$draftId"
    }

    object SignIn {
        val Default by lazy {
            General
        }
        val General = "signin/general"
        fun Twitter(consumerKey: String, consumerSecret: String) =
            "signin/twitter?consumerKey=$consumerKey&consumerSecret=$consumerSecret"

        val Mastodon = "signin/mastodon"

        object Web {
            fun Twitter(target: String) = "signin/twitter/web/${
            URLEncoder.encode(
                target,
                "UTF-8"
            )
            }"

            fun Mastodon(target: String) = "signin/mastodon/web/${
            URLEncoder.encode(
                target,
                "UTF-8"
            )
            }"
        }

        val TwitterWebSignInDialog = 1
    }

    fun User(userKey: MicroBlogKey) =
        "user/$userKey"

    fun Status(statusKey: MicroBlogKey) = "status/${statusKey.id}?host=${statusKey.host}"

    object Media {
        fun Status(statusKey: MicroBlogKey, selectedIndex: Int = 0) =
            "media/status/${statusKey.id}?selectedIndex=$selectedIndex&host=${statusKey.host}"

        fun Raw(url: String) =
            "media/raw/${URLEncoder.encode(url, "UTF-8")}"
    }

    fun Search(keyword: String) = "search/result/${
    URLEncoder.encode(
        keyword,
        "UTF-8"
    )
    }"

    fun SearchInput(keyword: String? = null): String {
        if (keyword == null) {
            return "search/input"
        }
        return "search/input?keyword=${
        URLEncoder.encode(
            keyword,
            "UTF-8"
        )
        }"
    }

    fun Compose(composeType: ComposeType, statusKey: MicroBlogKey? = null) =
        "compose?composeType=${composeType.name}&statusId=${statusKey?.id}&host=${statusKey?.host}"

    object Compose {
        object Search {
            const val User = "compose/search/user"
        }
    }

    fun Following(userKey: MicroBlogKey) = "following/$userKey"
    fun Followers(userKey: MicroBlogKey) = "followers/$userKey"

    object Settings {
        val Home = "settings"
        val Appearance = "settings/appearance"
        val Display = "settings/display"
        val About = "settings/about"
        val AccountManagement = "settings/accountmanagement"
    }
}

object DeepLinks {
    val Search = "$twidereXSchema://search/"
    val User = "$twidereXSchema://user/"
    val SignIn = "$twidereXSchema://signin"
}

fun NavGraphBuilder.authorizedComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable (NavBackStackEntry) -> Unit,
) {
    composable(route, arguments, deepLinks) {
        requireAuthorization {
            content.invoke(it)
        }
    }
}

fun NavGraphBuilder.route() {

    authorizedComposable(Route.Home) {
        HomeScene()
    }

    composable(
        Route.SignIn.General,
        deepLinks = listOf(
            navDeepLink {
                uriPattern = DeepLinks.SignIn
            }
        ),
    ) {
        SignInScene()
    }

    composable(
        "signin/twitter?consumerKey={consumerKey}&consumerSecret={consumerSecret}",
        arguments = listOf(
            navArgument("consumerKey") { type = NavType.StringType },
            navArgument("consumerSecret") { type = NavType.StringType },
        )
    ) { backStackEntry ->
        backStackEntry.arguments?.let { arguments ->
            val consumerKey = arguments.getString("consumerKey")
            val consumerSecret = arguments.getString("consumerSecret")
            if (consumerKey != null && consumerSecret != null) {
                TwitterSignInScene(consumerKey = consumerKey, consumerSecret = consumerSecret)
            }
        }
    }

    composable(Route.SignIn.Mastodon) {
        MastodonSignInScene()
    }

    composable(
        "signin/twitter/web/{target}",
        arguments = listOf(navArgument("target") { type = NavType.StringType }),
    ) { backStackEntry ->
        backStackEntry.arguments?.getString("target")?.let {
            TwitterWebSignInScene(target = URLDecoder.decode(it, "UTF-8"))
        }
    }

    composable(
        "signin/mastodon/web/{target}",
        arguments = listOf(
            navArgument("target") {
                type =
                    NavType.StringType
            },
        ),
    ) { backStackEntry ->
        backStackEntry.arguments?.getString("target")?.let {
            MastodonWebSignInScene(target = URLDecoder.decode(it, "UTF-8"))
        }
    }

    dialog<TwitterWebSignInDialog>(Route.SignIn.TwitterWebSignInDialog) {
        argument("target") {
            nullable = false
            type = NavType.StringType
        }
    }

    authorizedComposable(
        "deeplink/twitter/{screenName}",
        arguments = listOf(
            navArgument("screenName") { type = NavType.StringType },
        ),
        deepLinks = twitterHosts.map {
            navDeepLink {
                uriPattern = "$it/{screenName}"
            }
        } + navDeepLink {
            uriPattern = "${DeepLinks.User}{screenName}"
        }
    ) { backStackEntry ->
        backStackEntry.arguments?.let { arguments ->
            arguments.getString("screenName")?.let {
                TwitterUserScene(screenName = it)
            }
        }
    }

    authorizedComposable(
        "user/{userKey}",
        arguments = listOf(
            navArgument("userKey") { type = NavType.StringType },
        ),
    ) { backStackEntry ->
        backStackEntry.arguments?.let { arguments ->
            arguments.getString("userKey")?.let {
                MicroBlogKey.valueOf(it)
            }?.let {
                UserScene(it)
            }
        }
    }

    authorizedComposable(
        "status/{statusId}?host={host}",
        arguments = listOf(
            navArgument("statusId") { type = NavType.StringType },
            navArgument("host") { type = NavType.StringType; nullable = true; },
        ),
        deepLinks = twitterHosts.map {
            navDeepLink {
                uriPattern = "$it/{screenName}/status/{statusId}"
            }
        }
    ) { backStackEntry ->
        backStackEntry.arguments?.let { argument ->
            val host = argument.getString("host") ?: MicroBlogKey.TwitterHost
            argument.getString("statusId")?.let {
                StatusScene(statusKey = MicroBlogKey(it, host))
            }
        }
    }

    authorizedComposable(
        "media/status/{statusId}?selectedIndex={selectedIndex}&host={host}",
        arguments = listOf(
            navArgument("statusId") { type = NavType.StringType },
            navArgument("selectedIndex") { type = NavType.IntType; defaultValue = 0; },
            navArgument("host") { type = NavType.StringType; nullable = true; },
        ),
        deepLinks = twitterHosts.map {
            navDeepLink {
                uriPattern = "$it/{screenName}/status/{statusId}/photo/{selectedIndex}"
            }
        }
    ) { backStackEntry ->
        backStackEntry.arguments?.let { argument ->
            val statusId = argument.getString("statusId")
            val selectedIndex = argument.getInt("selectedIndex", 0)
            val host = argument.getString("host") ?: MicroBlogKey.TwitterHost
            if (statusId != null) {
                StatusMediaScene(
                    statusKey = MicroBlogKey(statusId, host),
                    selectedIndex = selectedIndex
                )
            }
        }
    }

    authorizedComposable(
        "media/raw/{url}",
        arguments = listOf(
            navArgument("url") { type = NavType.StringType },
        )
    ) { backStackEntry ->
        backStackEntry.arguments?.getString("url")?.let {
            URLDecoder.decode(it, "UTF-8")
        }?.let {
            RawMediaScene(url = it)
        }
    }

    authorizedComposable(
        "search/input?keyword={keyword}",
        arguments = listOf(
            navArgument("keyword") { type = NavType.StringType; nullable = true; }
        )
    ) { backStackEntry ->
        SearchInputScene(
            backStackEntry.arguments?.getString("keyword")?.let { URLDecoder.decode(it, "UTF-8") }
        )
    }

    authorizedComposable(
        "search/result/{keyword}",
        arguments = listOf(
            navArgument("keyword") { type = NavType.StringType }
        ),
        deepLinks = twitterHosts.map {
            navDeepLink {
                uriPattern = "$it/search?q={keyword}"
            }
        } + navDeepLink {
            uriPattern = "${DeepLinks.Search}{keyword}"
        }
    ) { backStackEntry ->
        backStackEntry.arguments?.getString("keyword")?.takeIf { it.isNotEmpty() }?.let {
            SearchScene(keyword = URLDecoder.decode(it, "UTF-8"))
        }
    }

    authorizedComposable(
        "compose?composeType={composeType}&statusId={statusId}&host={host}",
        arguments = listOf(
            navArgument("composeType") { type = NavType.StringType; nullable = true; },
            navArgument("statusId") { nullable = true },
            navArgument("host") { type = NavType.StringType; nullable = true; },
        )
    ) { backStackEntry ->
        backStackEntry.arguments?.let { args ->
            val host = args.getString("host") ?: MicroBlogKey.TwitterHost
            val type = args.getString("composeType")?.let {
                enumValueOf(it)
            } ?: ComposeType.New
            val statusId = args.getString("statusId")
            ComposeScene(statusId?.let { MicroBlogKey(it, host) }, type)
        }
    }

    authorizedComposable(
        "followers/{userKey}",
        arguments = listOf(
            navArgument("userKey") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        backStackEntry.arguments?.let { args ->
            args.getString("userKey")?.let {
                MicroBlogKey.valueOf(it)
            }?.let {
                FollowersScene(it)
            }
        }
    }

    authorizedComposable(
        "following/{userKey}",
        arguments = listOf(
            navArgument("userKey") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        backStackEntry.arguments?.let { args ->
            args.getString("userKey")?.let {
                MicroBlogKey.valueOf(it)
            }?.let {
                FollowingScene(it)
            }
        }
    }

    composable(Route.Settings.Home) {
        SettingsScene()
    }

    composable(Route.Settings.Appearance) {
        AppearanceScene()
    }

    composable(Route.Settings.Display) {
        DisplayScene()
    }

    composable(Route.Settings.AccountManagement) {
        AccountManagementScene()
    }

    composable(Route.Settings.About) {
        AboutScene()
    }

    composable(Route.Draft.List) {
        DraftListScene()
    }

    composable(
        "draft/compose/{draftId}",
        arguments = listOf(
            navArgument("draftId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        backStackEntry.arguments?.getString("draftId")?.let {
            DraftComposeScene(draftId = it)
        }
    }

    composable("compose/search/user") {
        ComposeSearchUserScene()
    }
}
