package com.twidere.twiderex

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.navDeepLink
import com.twidere.twiderex.scenes.ComposeScene
import com.twidere.twiderex.scenes.ComposeType
import com.twidere.twiderex.scenes.HomeScene
import com.twidere.twiderex.scenes.MediaScene
import com.twidere.twiderex.scenes.SearchScene
import com.twidere.twiderex.scenes.SplashScene
import com.twidere.twiderex.scenes.StatusScene
import com.twidere.twiderex.scenes.UserScene
import com.twidere.twiderex.scenes.settings.AppearanceScene
import com.twidere.twiderex.scenes.settings.SettingsScene
import com.twidere.twiderex.scenes.twitter.TwitterSignInScene
import com.twidere.twiderex.scenes.twitter.TwitterWebSignInScene

const val initialRoute = "splash"

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
            TwitterWebSignInScene(target = it)
        }
    }

    composable(
        "user/{screenName}",
        arguments = listOf(
            navArgument("userId") { type = NavType.StringType },
        ),
        deepLinks = listOf(navDeepLink { uriPattern = "$twitterHost/{screenName}" })
    ) { backStackEntry ->
        backStackEntry.arguments?.getString("screenName")?.let {
            UserScene(name = it)
        }
    }

    composable(
        "status/{statusId}",
        arguments = listOf(navArgument("statusId") { type = NavType.StringType }),
        deepLinks = listOf(navDeepLink {
            uriPattern = "$twitterHost/{screenName}/status/{statusId}"
        })
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
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "$twitterHost/{screenName}/status/{statusId}/photo/{selectedIndex}"
            }
        )
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
        backStackEntry.arguments?.getString("keyword")?.let {
            SearchScene(keyword = it)
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
            ComposeScene(type, statusId)
        }
    }

    composable("settings") {
        SettingsScene()
    }

    composable("settings/appearance") {
        AppearanceScene()
    }
}

