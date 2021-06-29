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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Constraints
import com.twidere.twiderex.component.RequireAuthorization
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.scenes.DraftListScene
import com.twidere.twiderex.scenes.HomeScene
import com.twidere.twiderex.scenes.PureMediaScene
import com.twidere.twiderex.scenes.RawMediaScene
import com.twidere.twiderex.scenes.SignInScene
import com.twidere.twiderex.scenes.StatusMediaScene
import com.twidere.twiderex.scenes.StatusScene
import com.twidere.twiderex.scenes.compose.ComposeScene
import com.twidere.twiderex.scenes.compose.ComposeSearchHashtagScene
import com.twidere.twiderex.scenes.compose.ComposeSearchUserScene
import com.twidere.twiderex.scenes.compose.DraftComposeScene
import com.twidere.twiderex.scenes.dm.DMConversationListScene
import com.twidere.twiderex.scenes.dm.DMConversationScene
import com.twidere.twiderex.scenes.lists.ListTimeLineScene
import com.twidere.twiderex.scenes.lists.ListsAddMembersScene
import com.twidere.twiderex.scenes.lists.ListsMembersScene
import com.twidere.twiderex.scenes.lists.ListsScene
import com.twidere.twiderex.scenes.lists.ListsSubscribersScene
import com.twidere.twiderex.scenes.lists.platform.TwitterListsCreateScene
import com.twidere.twiderex.scenes.lists.platform.TwitterListsEditScene
import com.twidere.twiderex.scenes.mastodon.MastodonHashtagScene
import com.twidere.twiderex.scenes.mastodon.MastodonSignInScene
import com.twidere.twiderex.scenes.mastodon.MastodonWebSignInScene
import com.twidere.twiderex.scenes.search.SearchInputScene
import com.twidere.twiderex.scenes.search.SearchScene
import com.twidere.twiderex.scenes.settings.AboutScene
import com.twidere.twiderex.scenes.settings.AccountManagementScene
import com.twidere.twiderex.scenes.settings.AppearanceScene
import com.twidere.twiderex.scenes.settings.DisplayScene
import com.twidere.twiderex.scenes.settings.MiscScene
import com.twidere.twiderex.scenes.settings.SettingsScene
import com.twidere.twiderex.scenes.settings.StorageScene
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
import moe.tlaster.precompose.navigation.BackStackEntry
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.query
import moe.tlaster.precompose.navigation.transition.NavTransition
import moe.tlaster.precompose.navigation.transition.fadeScaleCreateTransition
import moe.tlaster.precompose.navigation.transition.fadeScaleDestroyTransition
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

        fun Pure(belongToKey: MicroBlogKey, selectedIndex: Int = 0) =
            "media/pure/$belongToKey?selectedIndex=$selectedIndex"
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
        const val Storage = "settings/storage"
        const val About = "settings/about"
        const val AccountManagement = "settings/accountmanagement"
        const val Misc = "settings/misc"
    }

    object DeepLink {
        object Twitter {
            const val User = "deeplink/twitter/user/{screenName}"
            const val Status = "deeplink/twitter/status/{statusId}"
        }
        fun Draft(id: String) = "$twidereXSchema://draft/compose/$id"

        fun Compose(composeType: ComposeType, statusKey: MicroBlogKey? = null) = "$twidereXSchema://${Route.Compose(composeType, statusKey)}"

        fun Conversation(conversationKey: MicroBlogKey) = "$twidereXSchema://${Messages.Conversation(conversationKey)}"
    }

    fun Status(statusKey: MicroBlogKey) = "status/$statusKey"

    object Mastodon {
        fun Hashtag(keyword: String) = "mastodon/hashtag/$keyword"

        object Compose {
            const val Hashtag = "mastodon/compose/hashtag"
        }
    }

    object Lists {
        const val Home = "lists"
        const val TwitterCreate = "$Home/twitter/create"
        fun TwitterEdit(listKey: MicroBlogKey) = "$Home/twitter/edit/$listKey"
        fun Timeline(listKey: MicroBlogKey) = "$Home/timeline/$listKey"
        fun Members(listKey: MicroBlogKey, owned: Boolean) = "$Home/members/$listKey?owned=$owned"
        fun Subscribers(listKey: MicroBlogKey) = "$Home/subscribers/$listKey"
        fun AddMembers(listKey: MicroBlogKey) = "$Home/members/$listKey/add"
    }

    object Messages {
        const val Home = "messages"
        fun Conversation(conversationKey: MicroBlogKey) = "$Home/conversation/$conversationKey"
    }
}

object DeepLinks {
    object Twitter {
        const val User = "$twidereXSchema://twitter/user"
        const val Status = "$twidereXSchema://twitter/status/{statusId}"
        fun Status(id: String) = "$twidereXSchema://twitter/status/$id"
    }

    object Mastodon {
        const val Hashtag = "$twidereXSchema://mastodon/hashtag"
    }

    const val User = "$twidereXSchema://user"
    fun User(userKey: MicroBlogKey) = "$twidereXSchema://user/$userKey"
    const val Status = "$twidereXSchema://status/{statusKey}"
    fun Status(statusKey: MicroBlogKey) = "$twidereXSchema://status/$statusKey"
    const val Search = "$twidereXSchema://search"
    const val SignIn = "$twidereXSchema://signin"

    const val Draft = "$twidereXSchema://draft/compose/{draftId}"
    const val Compose = "$twidereXSchema://compose"
    const val Conversation = "$twidereXSchema://${Route.Messages.Home}/conversation/{conversationKey}"

    object Callback {
        object SignIn {
            const val Mastodon = "$twidereXSchema://auth/callback/mastodon"
            const val Twitter = "$twidereXSchema://auth/callback/twitter"
        }
    }
}

fun RouteBuilder.authorizedScene(
    route: String,
    deepLinks: List<String> = emptyList(),
    navTransition: NavTransition? = null,
    content: @Composable (BackStackEntry) -> Unit,
) {
    scene(route, deepLinks, navTransition) {
        RequireAuthorization {
            content.invoke(it)
        }
    }
}

fun RouteBuilder.authorizedDialog(
    route: String,
    content: @Composable (BackStackEntry) -> Unit,
) {
    dialog(route) {
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

fun RouteBuilder.route(constraints: Constraints) {
    authorizedScene(
        Route.Home,
        deepLinks = twitterHosts.map { "$it/*" }
    ) {
        HomeScene()
    }

    scene(
        Route.SignIn.General,
        deepLinks = listOf(
            DeepLinks.SignIn
        ),
    ) {
        SignInScene()
    }

    scene(
        "signin/twitter",
    ) { backStackEntry ->
        val consumerKey = backStackEntry.query<String>("consumerKey")
        val consumerSecret = backStackEntry.query<String>("consumerSecret")
        if (consumerKey != null && consumerSecret != null) {
            TwitterSignInScene(consumerKey = consumerKey, consumerSecret = consumerSecret)
        }
    }

    scene(Route.SignIn.Mastodon) {
        MastodonSignInScene()
    }

    scene(
        "signin/twitter/web/{target}",
    ) { backStackEntry ->
        backStackEntry.path<String>("target")?.let {
            TwitterWebSignInScene(target = URLDecoder.decode(it, "UTF-8"))
        }
    }

    scene(
        "signin/mastodon/web/{target}",
    ) { backStackEntry ->
        backStackEntry.path<String>("target")?.let {
            MastodonWebSignInScene(target = URLDecoder.decode(it, "UTF-8"))
        }
    }

    authorizedScene(
        Route.DeepLink.Twitter.User,
        deepLinks = twitterHosts.map {
            "$it/{screenName}"
        } + "${DeepLinks.Twitter.User}/{screenName}"
    ) { backStackEntry ->
        backStackEntry.path<String>("screenName")?.let { screenName ->
            val navigator = LocalNavigator.current
            RequirePlatformAccount(
                platformType = PlatformType.Twitter,
                fallback = {
                    navigator.openLink("https://twitter.com/$screenName", deepLink = false)
                    navigator.goBack()
                }
            ) {
                TwitterUserScene(screenName = screenName)
            }
        }
    }

    authorizedScene(
        "user/{userKey}",
        deepLinks = listOf(
            "${DeepLinks.User}/{userKey}"
        )
    ) { backStackEntry ->
        backStackEntry.path<String>("userKey")?.let {
            MicroBlogKey.valueOf(it)
        }?.let { userKey ->
            ProvideUserPlatform(userKey = userKey) { platformType ->
                RequirePlatformAccount(platformType = platformType) {
                    UserScene(userKey)
                }
            }
        }
    }

    authorizedScene(
        "mastodon/hashtag/{keyword}",
        deepLinks = listOf(
            "${DeepLinks.Mastodon.Hashtag}/{keyword}"
        )
    ) { backStackEntry ->
        backStackEntry.path<String>("keyword")?.let {
            MastodonHashtagScene(keyword = it)
        }
    }

    authorizedScene(
        "status/{statusKey}",
        deepLinks = listOf(
            DeepLinks.Status,
        )
    ) { backStackEntry ->
        backStackEntry.path<String>("statusKey")?.let {
            MicroBlogKey.valueOf(it)
        }?.let { statusKey ->
            ProvideStatusPlatform(statusKey = statusKey) { platform ->
                RequirePlatformAccount(platformType = platform) {
                    StatusScene(statusKey = statusKey)
                }
            }
        }
    }

    authorizedScene(
        Route.DeepLink.Twitter.Status,
        deepLinks = twitterHosts.map {
            "$it/{screenName}/status/{statusId:[0-9]+}"
        } + listOf(
            DeepLinks.Twitter.Status
        )
    ) { backStackEntry ->
        backStackEntry.path<String>("statusId")?.let { statusId ->
            val navigator = LocalNavigator.current
            RequirePlatformAccount(
                platformType = PlatformType.Twitter,
                fallback = {
                    navigator.openLink(
                        "https://twitter.com/${backStackEntry.path<String>("screenName")}/status/$statusId",
                        deepLink = false
                    )
                    navigator.goBack()
                }
            ) {
                StatusScene(statusKey = MicroBlogKey.twitter(statusId))
            }
        }
    }

    authorizedDialog(
        "media/status/{statusKey}",
    ) { backStackEntry ->
        backStackEntry.path<String>("statusKey")?.let {
            MicroBlogKey.valueOf(it)
        }?.let { statusKey ->
            ProvideStatusPlatform(statusKey = statusKey) { platformType ->
                RequirePlatformAccount(platformType = platformType) {
                    val selectedIndex = backStackEntry.query("selectedIndex", 0) ?: 0
                    StatusMediaScene(
                        statusKey = statusKey,
                        selectedIndex = selectedIndex
                    )
                }
            }
        }
    }

    authorizedDialog(
        "media/pure/{belongToKey}",
    ) { backStackEntry ->
        backStackEntry.path<String>("belongToKey")?.let {
            MicroBlogKey.valueOf(it)
        }?.let { belongToKey ->
            val selectedIndex = backStackEntry.query("selectedIndex", 0) ?: 0
            PureMediaScene(
                belongToKey = belongToKey,
                selectedIndex = selectedIndex
            )
        }
    }

    authorizedDialog(
        "media/raw/{url}",
    ) { backStackEntry ->
        backStackEntry.path<String>("url")?.let {
            URLDecoder.decode(it, "UTF-8")
        }?.let {
            RawMediaScene(url = it)
        }
    }

    authorizedScene(
        "search/input",
    ) { backStackEntry ->
        SearchInputScene(
            backStackEntry.query<String>("keyword")?.let { URLDecoder.decode(it, "UTF-8") }
        )
    }

    authorizedScene(
        "search/result/{keyword}",
        deepLinks = twitterHosts.map {
            "$it/search?q={keyword}"
        } + "${DeepLinks.Search}/{keyword}"
    ) { backStackEntry ->
        backStackEntry.path<String>("keyword")?.takeIf { it.isNotEmpty() }?.let {
            SearchScene(keyword = URLDecoder.decode(it, "UTF-8"))
        }
    }

    authorizedScene(
        "compose",
        navTransition = NavTransition(
            createTransition = {
                translationY = constraints.maxHeight * (1 - it)
                alpha = it
            },
            destroyTransition = {
                translationY = constraints.maxHeight * (1 - it)
                alpha = it
            },
            pauseTransition = fadeScaleDestroyTransition,
            resumeTransition = fadeScaleCreateTransition,
        ),
        deepLinks = listOf(
            DeepLinks.Compose
        )
    ) { backStackEntry ->
        val type = backStackEntry.query<String>("composeType")?.let {
            enumValueOf(it)
        } ?: ComposeType.New
        val statusKey = backStackEntry.query<String>("statusKey")
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

    authorizedScene(
        "followers/{userKey}",
    ) { backStackEntry ->
        backStackEntry.path<String>("userKey")?.let {
            MicroBlogKey.valueOf(it)
        }?.let {
            FollowersScene(it)
        }
    }

    authorizedScene(
        "following/{userKey}",
    ) { backStackEntry ->
        backStackEntry.path<String>("userKey")?.let {
            MicroBlogKey.valueOf(it)
        }?.let {
            FollowingScene(it)
        }
    }

    scene(Route.Settings.Home) {
        SettingsScene()
    }

    scene(Route.Settings.Appearance) {
        AppearanceScene()
    }

    scene(Route.Settings.Display) {
        DisplayScene()
    }

    scene(Route.Settings.Storage) {
        StorageScene()
    }

    scene(Route.Settings.AccountManagement) {
        AccountManagementScene()
    }

    scene(Route.Settings.Misc) {
        MiscScene()
    }

    scene(Route.Settings.About) {
        AboutScene()
    }

    scene(Route.Draft.List) {
        DraftListScene()
    }

    scene(
        "draft/compose/{draftId}",
        deepLinks = listOf(
            DeepLinks.Draft,
        )
    ) { backStackEntry ->
        backStackEntry.path<String>("draftId")?.let {
            DraftComposeScene(draftId = it)
        }
    }

    authorizedScene(Route.Compose.Search.User) {
        ComposeSearchUserScene()
    }

    authorizedScene(Route.Mastodon.Compose.Hashtag) {
        ComposeSearchHashtagScene()
    }

    authorizedScene(Route.Lists.Home) {
        ListsScene()
    }

    authorizedScene(Route.Lists.TwitterCreate) {
        TwitterListsCreateScene()
    }

    authorizedScene(
        "${Route.Lists.Home}/twitter/edit/{listKey}"
    ) { backStackEntry ->
        backStackEntry.path<String>("listKey")?.let {
            TwitterListsEditScene(listKey = MicroBlogKey.valueOf(it))
        }
    }

    authorizedScene(
        "${Route.Lists.Home}/timeline/{listKey}"
    ) { backStackEntry ->
        backStackEntry.path<String>("listKey")?.let {
            ListTimeLineScene(listKey = MicroBlogKey.valueOf(it))
        }
    }

    authorizedScene(
        "${Route.Lists.Home}/members/{listKey}"
    ) { backStackEntry ->
        backStackEntry.path<String>("listKey")?.let {
            ListsMembersScene(listKey = MicroBlogKey.valueOf(it), backStackEntry.query<Boolean>("owned") ?: false)
        }
    }

    authorizedScene(
        "${Route.Lists.Home}/subscribers/{listKey}"
    ) { backStackEntry ->
        backStackEntry.path<String>("listKey")?.let {
            ListsSubscribersScene(listKey = MicroBlogKey.valueOf(it))
        }
    }

    authorizedScene(
        "${Route.Lists.Home}/members/{listKey}/add"
    ) { backStackEntry ->
        backStackEntry.path<String>("listKey")?.let {
            ListsAddMembersScene(listKey = MicroBlogKey.valueOf(it))
        }
    }

    authorizedScene(Route.Messages.Home) {
        DMConversationListScene()
    }

    authorizedScene(
        "${Route.Messages.Home}/conversation/{conversationKey}",
        deepLinks = listOf(
            DeepLinks.Conversation
        )
    ) { backStackEntry ->
        backStackEntry.path<String>("conversationKey")?.let {
            DMConversationScene(conversationKey = MicroBlogKey.valueOf(it))
        }
    }
}
