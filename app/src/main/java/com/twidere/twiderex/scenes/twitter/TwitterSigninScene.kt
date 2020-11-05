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
package com.twidere.twiderex.scenes.twitter

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LifecycleOwnerAmbient
import androidx.navigation.compose.navigate
import androidx.navigation.navOptions
import com.twidere.twiderex.extensions.navViewModel
import com.twidere.twiderex.extensions.navigate
import com.twidere.twiderex.ui.AmbientNavController
import com.twidere.twiderex.ui.TwidereXTheme
import com.twidere.twiderex.viewmodel.twitter.TwitterSignInViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun TwitterSignInScene() {
    val viewModel = navViewModel<TwitterSignInViewModel>()
    val loading by viewModel.loading.observeAsState(initial = false)
    val navController = AmbientNavController.current
    val lifecycleOwner = LifecycleOwnerAmbient.current
    TwidereXTheme {
        Column(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (loading == true) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        GlobalScope.launch {
                            withContext(Dispatchers.Main) {
                                // TODO: dynamic key && secret
                                viewModel.beginOAuth(
                                    "wmtrtTaVOjUnH5pWQp4LDI5Qs",
                                    "E9Q9u2yK0COJae2tLcNEdY75OPA3bxqJiGZQztraHaQUtoI2cu"
                                ) { target ->
                                    suspendCoroutine {
                                        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>(
                                            "pin_code"
                                        )?.observe(lifecycleOwner) { result ->
                                            it.resume(result)
                                        }
                                        navController.navigate(
                                            "signin/twitter/web/${
                                            URLEncoder.encode(
                                                target,
                                                "UTF-8"
                                            )
                                            }"
                                        )
                                    }
                                }
                                navController.navigate(
                                    "home",
                                    navOptions {
                                        popUpTo(0) {
                                            inclusive = true
                                        }
                                    },
                                )
                            }
                        }
                    }
                ) {
                    Text(text = "Sign In")
                }
            }
        }
    }
}
