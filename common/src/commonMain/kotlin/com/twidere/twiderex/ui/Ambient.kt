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
package com.twidere.twiderex.ui

// import android.app.Activity
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.preferences.model.DisplayPreferences
import moe.tlaster.precompose.navigation.NavController

val LocalNavController = staticCompositionLocalOf<NavController> { error("No NavController") }
val LocalActiveAccount = compositionLocalOf<AccountDetails?> { null }
val LocalActiveAccountViewModel =
    compositionLocalOf<com.twidere.twiderex.viewmodel.ActiveAccountViewModel> { error("No ActiveAccountViewModel") }
// val LocalActivity = staticCompositionLocalOf<Activity> { error("NoActivity") }
val LocalVideoPlayback = compositionLocalOf { DisplayPreferences.AutoPlayback.Auto }
val LocalIsActiveNetworkMetered = compositionLocalOf { false }
