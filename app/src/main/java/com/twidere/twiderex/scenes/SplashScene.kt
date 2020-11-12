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
package com.twidere.twiderex.scenes

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onActive
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.imageResource
import androidx.navigation.navOptions
import com.twidere.twiderex.R
import com.twidere.twiderex.extensions.navViewModel
import com.twidere.twiderex.extensions.navigate
import com.twidere.twiderex.ui.AmbientNavController
import com.twidere.twiderex.ui.TwidereXTheme
import com.twidere.twiderex.viewmodel.SplashViewModel
import kotlinx.coroutines.launch

@Composable
fun SplashScene() {

    val viewModel = navViewModel<SplashViewModel>()
    val scope = rememberCoroutineScope()
    val navController = AmbientNavController.current
    TwidereXTheme {

        Scaffold {
            Column(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(asset = imageResource(id = R.mipmap.ic_launcher_foreground))
            }
        }
    }
    onActive(
        callback = {
            scope.launch {
                if (viewModel.hasAccount()) {
                    navController.navigate(
                        "home",
                        navOptions {
                            popUpTo(0) {
                                inclusive = true
                            }
                        },
                    )
                } else {
                    navController.navigate(
                        "signin/twitter",
                        navOptions {
                            popUpTo(0) {
                                inclusive = true
                            }
                        },
                    )
                }
            }
        }
    )
}
