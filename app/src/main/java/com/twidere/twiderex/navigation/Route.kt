/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.navDeepLink
import com.twidere.services.twitter.model.fields.TweetFields
import com.twidere.twiderex.scenes.ComposeScene
import com.twidere.twiderex.scenes.ComposeType
import com.twidere.twiderex.scenes.HomeScene
import com.twidere.twiderex.scenes.MediaScene
import com.twidere.twiderex.scenes.SearchScene
import com.twidere.twiderex.scenes.SplashScene
import com.twidere.twiderex.scenes.StatusScene
import com.twidere.twiderex.scenes.UserScene
import com.twidere.twiderex.scenes.settings.AppearanceScene
import com.twidere.twiderex.scenes.settings.DisplayScene
import com.twidere.twiderex.scenes.settings.SettingsScene
import com.twidere.twiderex.scenes.twitter.TwitterSignInScene
import com.twidere.twiderex.scenes.twitter.TwitterWebSignInScene
import com.twidere.twiderex.twitterHosts
import java.net.URLDecoder
import java.net.URLEncoder

const val initialRoute = "splash"

object Route {
    val Home = "home"

    object SignIn {
        val Twitter = "signin/twitter"
        fun TwitterWeb(target: String) = "signin/twitter/web/${
            URLEncoder.encode(
                target,
                "UTF-8"
            )
        }"
    }

    fun User(screenName: String) = "user/$screenName"
    fun Status(statusId: String) = "status/$statusId"
    fun Media(statusId: String, selectedIndex: Int = 0) =
        "media/$statusId?selectedIndex=$selectedIndex"

    fun Search(keyword: String) = "search/${
        URLEncoder.encode(
            keyword,
            "UTF-8"
        )
    }"

    fun Compose(composeType: ComposeType, statusId: String? = null) =
        "compose/${composeType.name}?statusId=$statusId"

    object Settings {
        val Home = "settings"
        val Appearance = "settings/appearance"
        val Display = "settings/display"
    }
}


fun NavGraphBuilder.route() {
    composable("splash") {
        SplashScene()
    }

    composable("home") {
        HomeScene()
    }

    composable("signin/twitter") {
        TwitterSignInScene()
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
        "user/{screenName}",
        arguments = listOf(
            navArgument("screenName") { type = NavType.StringType },
        ),
        deepLinks = twitterHosts.map { navDeepLink { uriPattern = "$it/{screenName}" } }
    ) { backStackEntry ->
        backStackEntry.arguments?.getString("screenName")?.let {
            UserScene(name = it)
        }
    }

    composable(
        "status/{statusId}",
        arguments = listOf(navArgument("statusId") { type = NavType.StringType }),
        deepLinks = twitterHosts.map {
            navDeepLink {
                uriPattern = "$it/{screenName}/status/{statusId}"
            }
        }
    ) { backStackEntry ->
        backStackEntry.arguments?.getString("statusId")?.let {
            StatusScene(statusId = it)
        }
    }

    composable(
        "media/{statusId}?selectedIndex={selectedIndex}",
        arguments = listOf(
            navArgument("statusId") { type = NavType.StringType },
            navArgument("selectedIndex") { type = NavType.IntType; defaultValue = 0; },
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
            if (statusId != null) {
                MediaScene(statusId = statusId, selectedIndex = selectedIndex)
            }
        }
    }

    composable(
        "search/{keyword}",
        arguments = listOf(
            navArgument("keyword") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        backStackEntry.arguments?.getString("keyword")?.takeIf { it.isNotEmpty() }?.let {
            SearchScene(keyword = URLDecoder.decode(it, "UTF-8"))
        }
    }

    composable(
        "compose/{composeType}?statusId={statusId}",
        arguments = listOf(
            navArgument("composeType") { type = NavType.StringType },
            navArgument("statusId") { nullable = true }
        )
    ) { backStackEntry ->
        backStackEntry.arguments?.let { args ->
            val type = args.getString("composeType")?.let {
                enumValueOf(it)
            } ?: ComposeType.New
            val statusId = args.getString("statusId")
            ComposeScene(statusId, type)
        }
    }

    composable("settings") {
        SettingsScene()
    }

    composable("settings/appearance") {
        AppearanceScene()
    }

    composable("settings/display") {
        DisplayScene()
    }
}
