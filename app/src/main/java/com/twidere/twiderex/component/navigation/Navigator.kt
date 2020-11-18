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
package com.twidere.twiderex.component.navigation

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Build
import android.webkit.CookieManager
import androidx.compose.runtime.staticAmbientOf
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.twidere.twiderex.navigation.Route
import com.twidere.twiderex.navigation.twidereXSchema
import com.twidere.twiderex.scenes.ComposeType

val AmbientNavigator = staticAmbientOf<INavigator>()

interface INavigator {
    fun user(screenName: String) {}
    fun status(statusId: String) {}
    fun media(statusId: String, selectedIndex: Int = 0) {}
    fun search(keyword: String) {}
    fun compose(composeType: ComposeType, statusId: String? = null) {}
    fun openLink(it: String) {}
    fun twitterSignInWeb(target: String) {}
}

class Navigator(
    private val navController: NavController,
    private val context: Context,
) : INavigator {
    override fun user(screenName: String) {
        navController.navigate(Route.User(screenName))
    }

    override fun status(statusId: String) {
        navController.navigate(Route.Status(statusId))
    }

    override fun media(statusId: String, selectedIndex: Int) {
        navController.navigate(Route.Media(statusId, selectedIndex))
    }

    override fun search(keyword: String) {
        navController.navigate(Route.Search(keyword))
    }

    override fun compose(composeType: ComposeType, statusId: String?) {
        navController.navigate(Route.Compose(composeType, statusId))
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
                Route.SignIn.TwitterWeb(target)
            )
        } else {
            navController.navigate(Route.SignIn.TwitterWebSignInDialog, bundleOf("target" to target))
        }
    }
}

object FakeNavigator : INavigator
