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
package com.twidere.twiderex.component.navigation

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Build
import android.webkit.CookieManager
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import androidx.navigation.compose.popUpTo
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.navigation.Route
import com.twidere.twiderex.navigation.twidereXSchema
import com.twidere.twiderex.viewmodel.compose.ComposeType

val LocalNavigator = staticCompositionLocalOf<INavigator>()

interface INavigator {
    fun user(user: UiUser) {}
    fun status(statusKey: MicroBlogKey) {}
    fun media(statusKey: MicroBlogKey, selectedIndex: Int = 0) {}
    fun search(keyword: String) {}
    fun compose(composeType: ComposeType, statusKey: MicroBlogKey? = null) {}
    fun openLink(it: String) {}
    fun twitterSignInWeb(target: String) {}
    fun mastodonSignInWeb(target: String) {}
    fun searchInput(initial: String? = null) {}
}

class Navigator(
    private val navController: NavController,
    private val context: Context,
) : INavigator {
    override fun user(user: UiUser) {
        navController.navigate(Route.User(user.screenName, user.userKey.host, user.userKey))
    }

    override fun status(statusKey: MicroBlogKey) {
        navController.navigate(Route.Status(statusKey))
    }

    override fun media(statusKey: MicroBlogKey, selectedIndex: Int) {
        navController.navigate(Route.Media(statusKey, selectedIndex))
    }

    override fun search(keyword: String) {
        navController.navigate(Route.Search(keyword)) {
            popUpTo(Route.Home) {
                inclusive = false
            }
        }
    }

    override fun searchInput(initial: String?) {
        navController.navigate(Route.SearchInput(initial)) {
            popUpTo(Route.Home) {
                inclusive = false
            }
        }
    }

    override fun compose(composeType: ComposeType, statusKey: MicroBlogKey?) {
        navController.navigate(Route.Compose(composeType, statusKey))
    }

    override fun openLink(it: String) {
        val uri = Uri.parse(it)
        if (uri.scheme == twidereXSchema || uri.host?.contains(
                "twitter.com",
                ignoreCase = true
            ) == true
        ) {
            navController.navigate(uri)
        } else {
            context.startActivity(Intent(ACTION_VIEW, uri))
        }
    }

    override fun twitterSignInWeb(target: String) {
        CookieManager.getInstance().removeAllCookies {
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            navController.navigate(
                Route.SignIn.Web.Twitter(target)
            )
        } else {
            navController.navigate(
                Route.SignIn.TwitterWebSignInDialog,
                bundleOf("target" to target)
            )
        }
    }

    override fun mastodonSignInWeb(target: String) {
        CookieManager.getInstance().removeAllCookies {
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            navController.navigate(
                Route.SignIn.Web.Mastodon(target)
            )
        } else {
        }
    }
}

object FakeNavigator : INavigator
