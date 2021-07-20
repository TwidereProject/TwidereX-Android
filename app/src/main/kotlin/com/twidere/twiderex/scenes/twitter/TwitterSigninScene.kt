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
package com.twidere.twiderex.scenes.twitter

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.twidere.twiderex.component.foundation.SignInScaffold
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.utils.CustomTabSignInChannel
import com.twidere.twiderex.viewmodel.twitter.TwitterSignInViewModel

@Composable
fun TwitterSignInScene(
    consumerKey: String,
    consumerSecret: String,
) {
    val navController = LocalNavController.current
    val context = LocalContext.current
    val navigator = LocalNavigator.current
    val viewModel =
        assistedViewModel<TwitterSignInViewModel.AssistedFactory, TwitterSignInViewModel> {
            it.create(
                consumerKey,
                consumerSecret,
                oauthVerifierProvider = { target ->
                    CustomTabsIntent.Builder()
                        .setShareState(CustomTabsIntent.SHARE_STATE_OFF)
                        .build().launchUrl(context, Uri.parse(target))
                    CustomTabSignInChannel.waitOne().getQueryParameter("oauth_verifier")
                },
                pinCodeProvider = { target ->
                    navigator.twitterSignInWeb(target)
                },
                onResult = { success ->
                    navController.goBackWith(success)
                }
            )
        }
    val loading by viewModel.loading.observeAsState(initial = false)

    SignInScaffold {
        if (loading) {
            CircularProgressIndicator()
        }
    }
}
