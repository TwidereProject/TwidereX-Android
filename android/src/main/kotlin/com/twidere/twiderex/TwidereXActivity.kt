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
package com.twidere.twiderex

import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.insets.ProvideWindowInsets
import com.twidere.twiderex.action.LocalStatusActions
import com.twidere.twiderex.action.StatusActions
import com.twidere.twiderex.component.LocalWindowInsetsController
import com.twidere.twiderex.component.foundation.LocalInAppNotification
import com.twidere.twiderex.compose.LocalResLoader
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.kmp.LocalPlatformWindow
import com.twidere.twiderex.kmp.LocalRemoteNavigator
import com.twidere.twiderex.kmp.PlatformWindow
import com.twidere.twiderex.kmp.RemoteNavigator
import com.twidere.twiderex.kmp.ResLoader
import com.twidere.twiderex.navigation.Router
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.preferences.PreferencesHolder
import com.twidere.twiderex.preferences.ProvidePreferences
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalActiveAccountViewModel
import com.twidere.twiderex.ui.LocalIsActiveNetworkMetered
import com.twidere.twiderex.utils.CustomTabSignInChannel
import com.twidere.twiderex.utils.IsActiveNetworkMeteredLiveData
import com.twidere.twiderex.utils.LocalPlatformResolver
import com.twidere.twiderex.utils.PlatformResolver
import kotlinx.coroutines.flow.MutableStateFlow
import moe.tlaster.kfilepicker.FilePicker
import moe.tlaster.precompose.lifecycle.PreComposeActivity
import moe.tlaster.precompose.lifecycle.setContent
import moe.tlaster.precompose.navigation.NavController
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TwidereXActivity : PreComposeActivity(), KoinComponent {

    private val navController by lazy {
        NavController()
    }

    private val statusActions: StatusActions by inject()

    private val preferencesHolder: PreferencesHolder by inject()

    private val inAppNotification: InAppNotification by inject()

    private val connectivityManager: ConnectivityManager by inject()

    private val platformResolver: PlatformResolver by inject()

    private val remoteNavigator: RemoteNavigator by inject()

    private val isActiveNetworkMetered = MutableStateFlow(false)
    private val isActiveNetworkMeteredLiveData by lazy {
        IsActiveNetworkMeteredLiveData(connectivityManager = connectivityManager)
    }

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FilePicker.init(activityResultRegistry, this, contentResolver)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        isActiveNetworkMeteredLiveData.observe(this) {
            isActiveNetworkMetered.value = it
        }
        setContent {
            var showSplash by rememberSaveable { mutableStateOf(true) }
            LaunchedEffect(Unit) {
                preferencesHolder.warmup()
                showSplash = false
            }
            App()
            AnimatedVisibility(
                visible = showSplash,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Splash()
            }
        }
        intent.data?.let {
            onDeeplink(it)
        }
    }

    @Composable
    private fun Splash() {
        MaterialTheme(
            colors = if (isSystemInDarkTheme()) {
                darkColors()
            } else {
                lightColors()
            }
        ) {
            Scaffold {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_login_logo),
                        contentDescription = stringResource(id = com.twidere.common.R.string.accessibility_common_logo_twidere)
                    )
                }
            }
        }
    }

    @Composable
    private fun App() {
        val windowInsetsControllerCompat =
            remember { WindowInsetsControllerCompat(window, window.decorView) }
        val accountViewModel =
            com.twidere.twiderex.di.ext.getViewModel<com.twidere.twiderex.viewmodel.ActiveAccountViewModel>()
        val account by accountViewModel.account.observeAsState(null)
        val isActiveNetworkMetered by isActiveNetworkMetered.observeAsState(initial = false)
        CompositionLocalProvider(
            LocalInAppNotification provides inAppNotification,
            LocalWindowInsetsController provides windowInsetsControllerCompat,
            LocalActiveAccount provides account,
            LocalStatusActions provides statusActions,
            LocalActiveAccountViewModel provides accountViewModel,
            LocalIsActiveNetworkMetered provides isActiveNetworkMetered,
            LocalPlatformResolver provides platformResolver,
            LocalResLoader provides ResLoader(this),
            LocalRemoteNavigator provides remoteNavigator,
            LocalPlatformWindow provides PlatformWindow(window),
        ) {
            ProvidePreferences(
                preferencesHolder,
            ) {
                ProvideWindowInsets(
                    windowInsetsAnimationsEnabled = true
                ) {
                    Router(
                        navController = navController
                    )
                }
            }
        }
    }

    private fun onDeeplink(it: Uri) {
        if (CustomTabSignInChannel.canHandle(it.toString())) {
            lifecycleScope.launchWhenResumed {
                CustomTabSignInChannel.send(it.toString())
            }
        } else {
            navController.navigate(it.toString())
        }
    }

    override fun onNewIntent(intent: Intent?) {
        intent?.data?.let {
            onDeeplink(it)
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launchWhenResumed {
            CustomTabSignInChannel.onClose()
        }
    }
}
