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
package com.twidere.twiderex

import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.core.net.ConnectivityManagerCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.fragment.DialogFragmentNavigator
import com.twidere.twiderex.action.LocalStatusActions
import com.twidere.twiderex.action.StatusActions
import com.twidere.twiderex.component.foundation.LocalInAppNotification
import com.twidere.twiderex.di.assisted.AssistedViewModelFactoryHolder
import com.twidere.twiderex.di.assisted.ProvideAssistedFactory
import com.twidere.twiderex.launcher.ActivityLauncher
import com.twidere.twiderex.launcher.LocalLauncher
import com.twidere.twiderex.navigation.Router
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.preferences.PreferencesHolder
import com.twidere.twiderex.preferences.ProvidePreferences
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalActiveAccountViewModel
import com.twidere.twiderex.ui.LocalActivity
import com.twidere.twiderex.ui.LocalApplication
import com.twidere.twiderex.ui.LocalIsActiveNetworkMetered
import com.twidere.twiderex.ui.LocalWindow
import com.twidere.twiderex.ui.LocalWindowInsetsController
import com.twidere.twiderex.viewmodel.ActiveAccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.accompanist.insets.ExperimentalAnimatedInsets
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import javax.inject.Inject

@AndroidEntryPoint
class TwidereXActivity : FragmentActivity() {

    val navController by lazy {
        NavHostController(this).apply {
            navigatorProvider.apply {
                addNavigator(ComposeNavigator())
                addNavigator(DialogFragmentNavigator(this@TwidereXActivity, supportFragmentManager))
            }
        }
    }

    private lateinit var launcher: ActivityLauncher
    private val isActiveNetworkMetered = MutableLiveData(false)
    private val networkCallback by lazy {
        object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                isActiveNetworkMetered.postValue(
                    ConnectivityManagerCompat.isActiveNetworkMetered(
                        connectivityManager
                    )
                )
            }
        }
    }

    @Inject
    lateinit var statusActions: StatusActions

    @Inject
    lateinit var preferencesHolder: PreferencesHolder

    @Inject
    lateinit var assistedViewModelFactoryHolder: AssistedViewModelFactoryHolder

    @Inject
    lateinit var inAppNotification: InAppNotification

    @Inject
    lateinit var connectivityManager: ConnectivityManager

    @OptIn(ExperimentalAnimatedInsets::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launcher = ActivityLauncher(activityResultRegistry)
        lifecycle.addObserver(launcher)
        isActiveNetworkMetered.postValue(
            ConnectivityManagerCompat.isActiveNetworkMetered(
                connectivityManager
            )
        )
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.navigationBarColor = Color.TRANSPARENT
        window.statusBarColor = Color.TRANSPARENT
        setContent {
            val windowInsetsControllerCompat =
                remember { WindowInsetsControllerCompat(window, window.decorView) }
            val accountViewModel = viewModel<ActiveAccountViewModel>()
            val account by accountViewModel.account.observeAsState()
            val isActiveNetworkMetered by isActiveNetworkMetered.observeAsState(initial = false)
            CompositionLocalProvider(
                LocalInAppNotification provides inAppNotification,
                LocalLauncher provides launcher,
                LocalWindow provides window,
                LocalWindowInsetsController provides windowInsetsControllerCompat,
                LocalActiveAccount provides account,
                LocalApplication provides application,
                LocalStatusActions provides statusActions,
                LocalActivity provides this,
                LocalActiveAccountViewModel provides accountViewModel,
                LocalIsActiveNetworkMetered provides isActiveNetworkMetered,
            ) {
                ProvidePreferences(
                    preferencesHolder,
                ) {
                    ProvideAssistedFactory(
                        assistedViewModelFactoryHolder
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
        }
    }

    override fun onStart() {
        super.onStart()
        connectivityManager.registerNetworkCallback(
            NetworkRequest.Builder().build(),
            networkCallback,
        )
    }

    override fun onStop() {
        super.onStop()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}
