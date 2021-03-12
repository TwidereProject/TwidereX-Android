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

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NamedNavArgument
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.navDeepLink
import com.twidere.twiderex.component.RequireAuthorization
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.scenes.DraftListScene
import com.twidere.twiderex.scenes.HomeScene
import com.twidere.twiderex.scenes.RawMediaScene
import com.twidere.twiderex.scenes.SignInScene
import com.twidere.twiderex.scenes.StatusMediaScene
import com.twidere.twiderex.scenes.StatusScene
import com.twidere.twiderex.scenes.compose.ComposeScene
import com.twidere.twiderex.scenes.compose.ComposeSearchUserScene
import com.twidere.twiderex.scenes.compose.DraftComposeScene
import com.twidere.twiderex.scenes.mastodon.MastodonHashtagScene
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
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalActiveAccountViewModel
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.utils.LocalPlatformResolver
import com.twidere.twiderex.viewmodel.compose.ComposeType
import java.net.URLDecoder
import java.net.URLEncoder

const val initialRoute = Route.Home
const val twidereXSchema = "twiderex"

object Route {
    const val Home = "home"

    object Draft {
        const val List = "draft/list"
        fun Compose(draftId: String) = "draft/compose/$draftId"
    }

    object SignIn {
        val Default by lazy {
            General
        }
        const val General = "signin/general"
        fun Twitter(consumerKey: String, consumerSecret: String) =
            "signin/twitter?consumerKey=$consumerKey&consumerSecret=$consumerSecret"

        const val Mastodon = "signin/mastodon"

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
    }

    fun User(userKey: MicroBlogKey) =
        "user/$userKey"

    object Media {
        fun Status(statusKey: MicroBlogKey, selectedIndex: Int = 0) =
            "media/status/$statusKey?selectedIndex=$selectedIndex"

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
        "compose?composeType=${composeType.name}${
        if (statusKey != null) {
            "&statusKey=$statusKey"
        } else {
            ""
        }
        }"

    object Compose {
        object Search {
            const val User = "compose/search/user"
        }
    }

    fun Following(userKey: MicroBlogKey) = "following/$userKey"
    fun Followers(userKey: MicroBlogKey) = "followers/$userKey"

    object Settings {
        const val Home = "settings"
        const val Appearance = "settings/appearance"
        const val Display = "settings/display"
        const val About = "settings/about"
        const val AccountManagement = "settings/accountmanagement"
    }

    object DeepLink {
        object Twitter {
            const val User = "deeplink/twitter/user/{screenName}"
            const val Status = "deeplink/twitter/status/{statusId}"
        }
    }

    fun Status(statusKey: MicroBlogKey) = "status/$statusKey"

    object Mastodon {
        fun Hashtag(keyword: String) = "mastodon/hashtag/$keyword"
    }
}

object DeepLinks {
    object Twitter {
        const val User = "$twidereXSchema://twitter/user"
    }

    object Mastodon {
        const val Hashtag = "$twidereXSchema://mastodon/hashtag"
    }

    const val User = "$twidereXSchema://user"
    const val Search = "$twidereXSchema://search"
    const val SignIn = "$twidereXSchema://signin"
}

fun NavGraphBuilder.authorizedComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable (NavBackStackEntry) -> Unit,
) {
    composable(route, arguments, deepLinks) {
        RequireAuthorization {
            content.invoke(it)
        }
    }
}

@Composable
fun ProvidePlatformType(
    key: MicroBlogKey,
    provider: suspend () -> PlatformType?,
    content: @Composable (platformType: PlatformType) -> Unit,
) {
    var platformType by rememberSaveable(
        saver = Saver(
            save = {
                it.value?.toString()
            },
            restore = {
                mutableStateOf(PlatformType.valueOf(it))
            }
        ),
    ) {
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
    ProvidePlatformType(
        key = statusKey,
        provider = {
            platformResolver.resolveStatus(statusKey = statusKey)
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
    content: @Composable () -> Unit,
) {
    var account = LocalActiveAccount.current ?: return
    if (account.type != platformType) {
        account = LocalActiveAccountViewModel.current.getTargetPlatformDefault(platformType)
            ?: return
    }
    CompositionLocalProvider(
        LocalActiveAccount provides account
    ) {
        content.invoke()
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

    authorizedComposable(
        Route.DeepLink.Twitter.User,
        arguments = listOf(
            navArgument("screenName") { type = NavType.StringType },
        ),
        deepLinks = twitterHosts.map {
            navDeepLink {
                uriPattern = "$it/{screenName}"
            }
        } + navDeepLink {
            uriPattern = "${DeepLinks.Twitter.User}/{screenName}"
        }
    ) { backStackEntry ->
        backStackEntry.arguments?.let { arguments ->
            arguments.getString("screenName")?.let {
                RequirePlatformAccount(platformType = PlatformType.Twitter) {
                    TwitterUserScene(screenName = it)
                }
            }
        }
    }

    authorizedComposable(
        "user/{userKey}",
        arguments = listOf(
            navArgument("userKey") { type = NavType.StringType },
        ),
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "${DeepLinks.User}/{userKey}"
            }
        )
    ) { backStackEntry ->
        backStackEntry.arguments?.let { arguments ->
            arguments.getString("userKey")?.let {
                MicroBlogKey.valueOf(it)
            }?.let { userKey ->
                ProvideUserPlatform(userKey = userKey) { platformType ->
                    RequirePlatformAccount(platformType = platformType) {
                        UserScene(userKey)
                    }
                }
            }
        }
    }

    authorizedComposable(
        "mastodon/hashtag/{keyword}",
        arguments = listOf(navArgument("keyword") { type = NavType.StringType }),
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "${DeepLinks.Mastodon.Hashtag}/{keyword}"
            }
        )
    ) { backStackEntry ->
        backStackEntry.arguments?.getString("keyword")?.let {
            MastodonHashtagScene(keyword = it)
        }
    }

    authorizedComposable(
        "status/{statusKey}",
        arguments = listOf(
            navArgument("statusKey") { type = NavType.StringType },
        ),
    ) { backStackEntry ->
        backStackEntry.arguments?.let { argument ->
            argument.getString("statusKey")?.let {
                MicroBlogKey.valueOf(it)
            }?.let { statusKey ->
                ProvideStatusPlatform(statusKey = statusKey) { platform ->
                    RequirePlatformAccount(platformType = platform) {
                        StatusScene(statusKey = statusKey)
                    }
                }
            }
        }
    }

    authorizedComposable(
        Route.DeepLink.Twitter.Status,
        arguments = listOf(
            navArgument("statusId") { type = NavType.StringType },
        ),
        deepLinks = twitterHosts.map {
            navDeepLink {
                uriPattern = "$it/{screenName}/status/{statusId}"
            }
        }
    ) { backStackEntry ->
        backStackEntry.arguments?.let { argument ->
            argument.getString("statusId")?.let {
                RequirePlatformAccount(platformType = PlatformType.Twitter) {
                    StatusScene(statusKey = MicroBlogKey.twitter(it))
                }
            }
        }
    }

    authorizedComposable(
        "media/status/{statusKey}?selectedIndex={selectedIndex}",
        arguments = listOf(
            navArgument("statusKey") { type = NavType.StringType },
            navArgument("selectedIndex") { type = NavType.IntType; defaultValue = 0; },
        ),
    ) { backStackEntry ->
        backStackEntry.arguments?.let { argument ->
            argument.getString("statusKey")?.let {
                MicroBlogKey.valueOf(it)
            }?.let { statusKey ->
                ProvideStatusPlatform(statusKey = statusKey) { platformType ->
                    RequirePlatformAccount(platformType = platformType) {
                        val selectedIndex = argument.getInt("selectedIndex", 0)
                        StatusMediaScene(
                            statusKey = statusKey,
                            selectedIndex = selectedIndex
                        )
                    }
                }
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
            uriPattern = "${DeepLinks.Search}/{keyword}"
        }
    ) { backStackEntry ->
        backStackEntry.arguments?.getString("keyword")?.takeIf { it.isNotEmpty() }?.let {
            SearchScene(keyword = URLDecoder.decode(it, "UTF-8"))
        }
    }

    authorizedComposable(
        "compose?composeType={composeType}&statusKey={statusKey}",
        arguments = listOf(
            navArgument("composeType") { type = NavType.StringType; nullable = true; },
            navArgument("statusKey") { type = NavType.StringType; nullable = true; },
        )
    ) { backStackEntry ->
        backStackEntry.arguments?.let { args ->
            val type = args.getString("composeType")?.let {
                enumValueOf(it)
            } ?: ComposeType.New
            val statusKey = args.getString("statusKey")
                ?.takeIf { it.isNotEmpty() }
                ?.let { MicroBlogKey.valueOf(it) }
            if (statusKey != null) {
                ProvideStatusPlatform(statusKey = statusKey) { platformType ->
                    RequirePlatformAccount(platformType = platformType) {
                        ComposeScene(statusKey, type)
                    }
                }
            } else {
                ComposeScene(statusKey, type)
            }
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
