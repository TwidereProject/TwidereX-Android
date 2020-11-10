/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Providers
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.viewinterop.viewModel
import androidx.hilt.lifecycle.HiltViewModelFactory
import com.twidere.twiderex.di.assisted.ProvideAssistedFactory
import com.twidere.twiderex.extensions.ProvideNavigationViewModelFactoryMap
import com.twidere.twiderex.launcher.ActivityLauncher
import com.twidere.twiderex.launcher.AmbientLauncher
import com.twidere.twiderex.navigation.Router
import com.twidere.twiderex.providers.AmbientStatusActions
import com.twidere.twiderex.providers.StatusActions
import com.twidere.twiderex.settings.AmbientAvatarStyle
import com.twidere.twiderex.settings.AmbientFontScale
import com.twidere.twiderex.settings.AmbientMediaPreview
import com.twidere.twiderex.settings.AmbientPrimaryColor
import com.twidere.twiderex.settings.AmbientTabPosition
import com.twidere.twiderex.settings.AmbientTheme
import com.twidere.twiderex.settings.AmbientUseSystemFontSize
import com.twidere.twiderex.settings.AvatarStyleSettings
import com.twidere.twiderex.settings.FontScaleSettings
import com.twidere.twiderex.settings.MediaPreviewSettings
import com.twidere.twiderex.settings.PrimaryColorSetting
import com.twidere.twiderex.settings.TabPositionSetting
import com.twidere.twiderex.settings.ThemeSetting
import com.twidere.twiderex.settings.UseSystemFontSizeSettings
import com.twidere.twiderex.ui.AmbientActiveAccount
import com.twidere.twiderex.ui.AmbientApplication
import com.twidere.twiderex.ui.AmbientViewModelProviderFactory
import com.twidere.twiderex.ui.AmbientWindow
import com.twidere.twiderex.ui.AmbientWindowPadding
import com.twidere.twiderex.ui.ProvideWindowPadding
import com.twidere.twiderex.viewmodel.ActiveAccountViewModel
import com.twidere.twiderex.viewmodel.ComposeViewModel
import com.twidere.twiderex.viewmodel.MediaViewModel
import com.twidere.twiderex.viewmodel.twitter.TwitterStatusViewModel
import com.twidere.twiderex.viewmodel.twitter.search.TwitterSearchMediasViewModel
import com.twidere.twiderex.viewmodel.twitter.search.TwitterSearchTweetsViewModel
import com.twidere.twiderex.viewmodel.twitter.search.TwitterSearchUserViewModel
import com.twidere.twiderex.viewmodel.twitter.timeline.HomeTimelineViewModel
import com.twidere.twiderex.viewmodel.twitter.timeline.MentionsTimelineViewModel
import com.twidere.twiderex.viewmodel.user.UserFavouriteTimelineViewModel
import com.twidere.twiderex.viewmodel.user.UserTimelineViewModel
import com.twidere.twiderex.viewmodel.user.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TwidereXActivity : ComponentActivity() {

    private lateinit var launcher: ActivityLauncher

    @Inject
    lateinit var tabPositionSetting: TabPositionSetting

    @Inject
    lateinit var themeSetting: ThemeSetting

    @Inject
    lateinit var primaryColorSettings: PrimaryColorSetting

    @Inject
    lateinit var avatarStyleSettings: AvatarStyleSettings

    @Inject
    lateinit var mediaPreviewSettings: MediaPreviewSettings

    @Inject
    lateinit var useSystemFontSizeSettings: UseSystemFontSizeSettings

    @Inject
    lateinit var fontScaleSettings: FontScaleSettings

    @Inject
    lateinit var statusActions: StatusActions

    @Inject
    lateinit var homeTimelineViewModelFactory: HomeTimelineViewModel.AssistedFactory

    @Inject
    lateinit var twitterStatusViewModelFactory: TwitterStatusViewModel.AssistedFactory

    @Inject
    lateinit var mentionsTimelineViewModelFactory: MentionsTimelineViewModel.AssistedFactory

    @Inject
    lateinit var twitterSearchMediasViewModelFactory: TwitterSearchMediasViewModel.AssistedFactory

    @Inject
    lateinit var twitterSearchTweetsViewModelFactory: TwitterSearchTweetsViewModel.AssistedFactory

    @Inject
    lateinit var twitterSearchUserViewModelFactory: TwitterSearchUserViewModel.AssistedFactory

    @Inject
    lateinit var userFavouriteTimelineViewModelFactory: UserFavouriteTimelineViewModel.AssistedFactory

    @Inject
    lateinit var userTimelineViewModelFactory: UserTimelineViewModel.AssistedFactory

    @Inject
    lateinit var userViewModelFactory: UserViewModel.AssistedFactory

    @Inject
    lateinit var composeViewModelFactory: ComposeViewModel.AssistedFactory

    @Inject
    lateinit var mediaViewModelFactory: MediaViewModel.AssistedFactory

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
            val tabPosition by tabPositionSetting.data.observeAsState(initial = tabPositionSetting.initialValue)
            val primaryColor by primaryColorSettings.data.observeAsState(initial = primaryColorSettings.initialValue)
            val theme by themeSetting.data.observeAsState(initial = themeSetting.initialValue)
            val avatarStyle by avatarStyleSettings.data.observeAsState(initial = avatarStyleSettings.initialValue)
            val mediaPreview by mediaPreviewSettings.data.observeAsState(initial = mediaPreviewSettings.initialValue)
            val useSystemFontSize by useSystemFontSizeSettings.data.observeAsState(initial = useSystemFontSizeSettings.initialValue)
            val fontScale by fontScaleSettings.data.observeAsState(initial = fontScaleSettings.initialValue)

            Providers(
                AmbientPrimaryColor provides primaryColor,
                AmbientTabPosition provides tabPosition,
                AmbientTheme provides theme,
                AmbientLauncher provides launcher,
                AmbientWindow provides window,
                AmbientViewModelProviderFactory provides defaultViewModelProviderFactory,
                AmbientActiveAccount provides account,
                AmbientApplication provides application,
                AmbientAvatarStyle provides avatarStyle,
                AmbientMediaPreview provides mediaPreview,
                AmbientUseSystemFontSize provides useSystemFontSize,
                AmbientFontScale provides fontScale,
                AmbientStatusActions provides statusActions,
            ) {
                ProvideAssistedFactory(
                    homeTimelineViewModelFactory,
                    twitterStatusViewModelFactory,
                    mentionsTimelineViewModelFactory,
                    twitterSearchMediasViewModelFactory,
                    twitterSearchTweetsViewModelFactory,
                    twitterSearchUserViewModelFactory,
                    userFavouriteTimelineViewModelFactory,
                    userTimelineViewModelFactory,
                    userViewModelFactory,
                    composeViewModelFactory,
                    mediaViewModelFactory,
                ) {
                    ProvideNavigationViewModelFactoryMap(factory = defaultViewModelProviderFactory as HiltViewModelFactory) {
                        ProvideWindowPadding {
                            val windowPadding = AmbientWindowPadding.current
                            Box(
                                modifier = Modifier.padding(windowPadding)
                            ) {
                                Router()
                            }
                        }
                    }
                }
            }
        }
    }
}
