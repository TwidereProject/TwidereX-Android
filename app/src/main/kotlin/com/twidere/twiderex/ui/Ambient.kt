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
package com.twidere.twiderex.ui

import android.app.Activity
import android.app.Application
import android.view.Window
import androidx.compose.runtime.ambientOf
import androidx.compose.runtime.staticAmbientOf
import androidx.hilt.lifecycle.ViewModelAssistedFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.preferences.proto.DisplayPreferences
import com.twidere.twiderex.viewmodel.ActiveAccountViewModel

val AmbientWindow = staticAmbientOf<Window> { error("No Window") }
val AmbientNavController = staticAmbientOf<NavController> { error("No NavController") }
val AmbientViewModelProviderFactory = staticAmbientOf<ViewModelProvider.Factory>()
val AmbientActiveAccount = ambientOf<AccountDetails?>()
val AmbientViewModelFactoriesMap =
    staticAmbientOf<Map<String, ViewModelAssistedFactory<out ViewModel>>>()
val AmbientActiveAccountViewModel = ambientOf<ActiveAccountViewModel>()
val AmbientApplication = staticAmbientOf<Application>()
val AmbientActivity = staticAmbientOf<Activity>()
val AmbientVideoPlayback = ambientOf { DisplayPreferences.AutoPlayback.Auto }
val AmbientIsActiveNetworkMetered = ambientOf { false }
