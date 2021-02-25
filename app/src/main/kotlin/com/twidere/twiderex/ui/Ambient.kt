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
package com.twidere.twiderex.ui

import android.app.Activity
import android.app.Application
import android.view.Window
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.preferences.proto.DisplayPreferences
import com.twidere.twiderex.viewmodel.ActiveAccountViewModel

val LocalWindowInsetsController =
    staticCompositionLocalOf<WindowInsetsControllerCompat> { error("No WindowInsetsControllerCompat") }
val LocalWindow = staticCompositionLocalOf<Window> { error("No Window") }
val LocalNavController = staticCompositionLocalOf<NavController> { error("No NavController") }
val LocalActiveAccount = compositionLocalOf<AccountDetails?> { null }
val LocalActiveAccountViewModel =
    compositionLocalOf<ActiveAccountViewModel> { error("No ActiveAccountViewModel") }
val LocalApplication = staticCompositionLocalOf<Application> { error("No Application") }
val LocalActivity = staticCompositionLocalOf<Activity> { error("NoActivity") }
val LocalVideoPlayback = compositionLocalOf { DisplayPreferences.AutoPlayback.Auto }
val LocalIsActiveNetworkMetered = compositionLocalOf { false }
