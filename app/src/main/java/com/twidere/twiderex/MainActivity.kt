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

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Providers
import androidx.compose.ui.platform.setContent
import com.twidere.twiderex.settings.AmbientPrimaryColor
import com.twidere.twiderex.settings.AmbientTabPosition
import com.twidere.twiderex.settings.AmbientTheme
import com.twidere.twiderex.settings.PrimaryColorSetting
import com.twidere.twiderex.settings.TabPositionSetting
import com.twidere.twiderex.settings.ThemeSetting
import com.twidere.twiderex.ui.AmbientViewModelProviderFactory
import com.twidere.twiderex.ui.AmbientWindow
import com.twidere.twiderex.utils.ActivityLauncher
import com.twidere.twiderex.utils.AmbientLauncher
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var launcher: ActivityLauncher

    @Inject
    lateinit var tabPosition: TabPositionSetting

    @Inject
    lateinit var theme: ThemeSetting

    @Inject
    lateinit var primaryColor: PrimaryColorSetting

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launcher = ActivityLauncher(activityResultRegistry)
        lifecycle.addObserver(launcher)
        setContent {
            Providers(
                AmbientPrimaryColor provides primaryColor,
                AmbientTabPosition provides tabPosition,
                AmbientTheme provides theme,
                AmbientLauncher provides launcher,
                AmbientWindow provides window,
                AmbientViewModelProviderFactory provides defaultViewModelProviderFactory
            ) {
                App()
            }
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            window.setDecorFitsSystemWindows(false)
//            rootView.setOnApplyWindowInsetsListener { _, insets ->
//                if (!ignoreWindowInsets) {
//                    val systemInsets =
//                        insets.getInsets(WindowInsets.Type.ime() or WindowInsets.Type.systemBars())
//                    rootView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
//                        updateMargins(systemInsets)
//                    }
//                }
//                insets
//            }
//            rootView.setWindowInsetsAnimationCallback(object :
//                    WindowInsetsAnimation.Callback(DISPATCH_MODE_STOP) {
//                    override fun onPrepare(animation: WindowInsetsAnimation) {
//                        super.onPrepare(animation)
//                        ignoreWindowInsets = true
//                    }
//
//                    override fun onEnd(animation: WindowInsetsAnimation) {
//                        super.onEnd(animation)
//                        ignoreWindowInsets = false
//                    }
//
//                    override fun onProgress(
//                        insets: WindowInsets,
//                        animations: MutableList<WindowInsetsAnimation>
//                    ): WindowInsets {
//                        val systemInsets =
//                            insets.getInsets(WindowInsets.Type.ime() or WindowInsets.Type.systemBars())
//                        rootView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
//                            updateMargins(systemInsets)
//                        }
//                        return insets
//                    }
//                })
//        } else {
//            @Suppress("DEPRECATION")
//            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
//        }
    }
}
