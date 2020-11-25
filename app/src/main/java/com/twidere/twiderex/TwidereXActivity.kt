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
package com.twidere.twiderex

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Providers
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.viewinterop.viewModel
import androidx.datastore.core.DataStore
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.HiltViewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.fragment.DialogFragmentNavigator
import com.twidere.twiderex.di.assisted.ProvideAssistedFactory
import com.twidere.twiderex.extensions.ProvideNavigationViewModelFactoryMap
import com.twidere.twiderex.launcher.ActivityLauncher
import com.twidere.twiderex.launcher.AmbientLauncher
import com.twidere.twiderex.navigation.Router
import com.twidere.twiderex.preferences.ProvidePreferences
import com.twidere.twiderex.preferences.proto.AppearancePreferences
import com.twidere.twiderex.preferences.proto.DisplayPreferences
import com.twidere.twiderex.providers.AmbientStatusActions
import com.twidere.twiderex.providers.StatusActions
import com.twidere.twiderex.ui.AmbientActiveAccount
import com.twidere.twiderex.ui.AmbientActiveAccountViewModel
import com.twidere.twiderex.ui.AmbientActivity
import com.twidere.twiderex.ui.AmbientApplication
import com.twidere.twiderex.ui.AmbientViewModelProviderFactory
import com.twidere.twiderex.ui.AmbientWindow
import com.twidere.twiderex.ui.AmbientWindowPadding
import com.twidere.twiderex.ui.ProvideWindowPadding
import com.twidere.twiderex.viewmodel.ActiveAccountViewModel
import com.twidere.twiderex.viewmodel.ComposeViewModel
import com.twidere.twiderex.viewmodel.DraftComposeViewModel
import com.twidere.twiderex.viewmodel.DraftItemViewModel
import com.twidere.twiderex.viewmodel.MediaViewModel
import com.twidere.twiderex.viewmodel.search.SearchInputViewModel
import com.twidere.twiderex.viewmodel.twitter.TwitterStatusViewModel
import com.twidere.twiderex.viewmodel.twitter.search.TwitterSearchMediaViewModel
import com.twidere.twiderex.viewmodel.twitter.search.TwitterSearchTweetsViewModel
import com.twidere.twiderex.viewmodel.twitter.timeline.HomeTimelineViewModel
import com.twidere.twiderex.viewmodel.twitter.timeline.MentionsTimelineViewModel
import com.twidere.twiderex.viewmodel.user.UserFavouriteTimelineViewModel
import com.twidere.twiderex.viewmodel.user.UserMediaTimelineViewModel
import com.twidere.twiderex.viewmodel.user.UserTimelineViewModel
import com.twidere.twiderex.viewmodel.user.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
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

    @Inject
    lateinit var statusActions: StatusActions

    @Inject
    lateinit var homeTimelineViewModelFactory: HomeTimelineViewModel.AssistedFactory

    @Inject
    lateinit var twitterStatusViewModelFactory: TwitterStatusViewModel.AssistedFactory

    @Inject
    lateinit var mentionsTimelineViewModelFactory: MentionsTimelineViewModel.AssistedFactory

    @Inject
    lateinit var twitterSearchMediaViewModelFactory: TwitterSearchMediaViewModel.AssistedFactory

    @Inject
    lateinit var twitterSearchTweetsViewModelFactory: TwitterSearchTweetsViewModel.AssistedFactory

    @Inject
    lateinit var userFavouriteTimelineViewModelFactory: UserFavouriteTimelineViewModel.AssistedFactory

    @Inject
    lateinit var userTimelineViewModelFactory: UserTimelineViewModel.AssistedFactory

    @Inject
    lateinit var userMediaTimelineViewModelFactory: UserMediaTimelineViewModel.AssistedFactory

    @Inject
    lateinit var userViewModelFactory: UserViewModel.AssistedFactory

    @Inject
    lateinit var composeViewModelFactory: ComposeViewModel.AssistedFactory

    @Inject
    lateinit var mediaViewModelFactory: MediaViewModel.AssistedFactory

    @Inject
    lateinit var searchInputViewModelFactory: SearchInputViewModel.AssistedFactory

    @Inject
    lateinit var draftItemViewModelFactory: DraftItemViewModel.AssistedFactory

    @Inject
    lateinit var draftComposeViewModelFactory: DraftComposeViewModel.AssistedFactory

    @Inject
    lateinit var appearancePreferences: DataStore<AppearancePreferences>

    @Inject
    lateinit var displayPreferences: DataStore<DisplayPreferences>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launcher = ActivityLauncher(activityResultRegistry)
        lifecycle.addObserver(launcher)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            @Suppress("DEPRECATION")
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
        setContent {
            val accountViewModel = viewModel<ActiveAccountViewModel>()
            val account by accountViewModel.account.observeAsState()
            Providers(
                AmbientLauncher provides launcher,
                AmbientWindow provides window,
                AmbientViewModelProviderFactory provides defaultViewModelProviderFactory,
                AmbientActiveAccount provides account,
                AmbientApplication provides application,
                AmbientStatusActions provides statusActions,
                AmbientActivity provides this,
                AmbientActiveAccountViewModel provides accountViewModel,
            ) {
                ProvidePreferences(
                    appearancePreferences = appearancePreferences,
                    displayPreferences = displayPreferences
                ) {
                    ProvideAssistedFactory(
                        homeTimelineViewModelFactory,
                        twitterStatusViewModelFactory,
                        mentionsTimelineViewModelFactory,
                        twitterSearchMediaViewModelFactory,
                        twitterSearchTweetsViewModelFactory,
                        userFavouriteTimelineViewModelFactory,
                        userTimelineViewModelFactory,
                        userViewModelFactory,
                        composeViewModelFactory,
                        mediaViewModelFactory,
                        userMediaTimelineViewModelFactory,
                        draftItemViewModelFactory,
                        draftComposeViewModelFactory,
                        searchInputViewModelFactory,
                    ) {
                        ProvideNavigationViewModelFactoryMap(factory = defaultViewModelProviderFactory as HiltViewModelFactory) {
                            ProvideWindowPadding {
                                val windowPadding = AmbientWindowPadding.current
                                Box(
                                    modifier = Modifier.padding(windowPadding)
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
        }
    }
}
