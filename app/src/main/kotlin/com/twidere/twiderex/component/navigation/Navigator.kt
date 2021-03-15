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
import android.webkit.CookieManager
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.navigate
import androidx.navigation.compose.popUpTo
import com.twidere.twiderex.db.model.ReferenceType
import com.twidere.twiderex.model.MastodonStatusType
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.navigation.Route
import com.twidere.twiderex.navigation.twidereXSchema
import com.twidere.twiderex.viewmodel.compose.ComposeType

val LocalNavigator = staticCompositionLocalOf<INavigator> { error("No Navigator") }

interface INavigator {
    fun user(user: UiUser, builder: NavOptionsBuilder.() -> Unit = {}) {}
    fun status(status: UiStatus, builder: NavOptionsBuilder.() -> Unit = {}) {}
    fun media(
        statusKey: MicroBlogKey,
        selectedIndex: Int = 0,
        builder: NavOptionsBuilder.() -> Unit = {}
    ) {
    }

    fun search(keyword: String) {}
    fun compose(
        composeType: ComposeType,
        statusKey: MicroBlogKey? = null,
        builder: NavOptionsBuilder.() -> Unit = {}
    ) {
    }

    fun openLink(it: String, deepLink: Boolean = true) {}
    fun twitterSignInWeb(target: String) {}
    fun mastodonSignInWeb(target: String) {}
    fun searchInput(initial: String? = null) {}
    fun hashtag(name: String) {}
    fun goBack() {}
}

class Navigator(
    private val navController: NavController,
    private val context: Context,
) : INavigator {
    override fun user(user: UiUser, builder: NavOptionsBuilder.() -> Unit) {
        navController.navigate(Route.User(user.userKey), builder)
    }

    override fun status(status: UiStatus, builder: NavOptionsBuilder.() -> Unit) {
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
            navController.navigate(
                Route.Status(statusKey),
                builder,
            )
        }
    }

    override fun media(
        statusKey: MicroBlogKey,
        selectedIndex: Int,
        builder: NavOptionsBuilder.() -> Unit
    ) {
        navController.navigate(Route.Media.Status(statusKey, selectedIndex), builder)
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

    override fun compose(
        composeType: ComposeType,
        statusKey: MicroBlogKey?,
        builder: NavOptionsBuilder.() -> Unit
    ) {
        navController.navigate(Route.Compose(composeType, statusKey), builder)
    }

    override fun openLink(it: String, deepLink: Boolean) {
        val uri = Uri.parse(it)
        if ((
            uri.scheme == twidereXSchema || uri.host?.contains(
                    "twitter.com",
                    ignoreCase = true
                ) == true
            ) &&
            deepLink
        ) {
            navController.navigate(uri)
        } else {
            context.startActivity(Intent(ACTION_VIEW, uri))
        }
    }

    override fun twitterSignInWeb(target: String) {
        CookieManager.getInstance().removeAllCookies {
        }
        navController.navigate(
            Route.SignIn.Web.Twitter(target)
        )
    }

    override fun mastodonSignInWeb(target: String) {
        CookieManager.getInstance().removeAllCookies {
        }
        navController.navigate(
            Route.SignIn.Web.Mastodon(target)
        )
    }

    override fun hashtag(name: String) {
        navController.navigate(Route.Mastodon.Hashtag(name))
    }

    override fun goBack() {
        navController.navigateUp()
    }
}

object FakeNavigator : INavigator
