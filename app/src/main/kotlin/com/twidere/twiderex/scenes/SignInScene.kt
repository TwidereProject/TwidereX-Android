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
package com.twidere.twiderex.scenes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.liveData
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigate
import com.twidere.twiderex.BuildConfig
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.SignInButton
import com.twidere.twiderex.component.foundation.SignInScaffold
import com.twidere.twiderex.navigation.Route
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.standardPadding

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SignInScene() {
    val navController = LocalNavController.current
    val state by navController.currentBackStackEntryAsState()
    val success by state?.savedStateHandle?.getLiveData<Boolean>("success").let {
        it ?: liveData { emit(false) }
    }.observeAsState(initial = false)
    DisposableEffect(success) {
        if (success) {
            navController.popBackStack()
        }
        onDispose { }
    }
    var showMastodon by remember { mutableStateOf(false) }
    SignInScaffold(
        countAction = {
            showMastodon = it > 9
        }
    ) {
        TwitterSignIn()
        if (showMastodon) {
            Spacer(modifier = Modifier.height(standardPadding * 2))
            SignInButton(
                onClick = {
                    navController.navigate(Route.SignIn.Mastodon)
                },
                border = ButtonDefaults.outlinedBorder,
                color = MaterialTheme.colors.surface,
                contentColor = MaterialTheme.colors.primary,
            ) {
                ListItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_mastodon_logo_blue),
                            contentDescription = stringResource(
                                id = R.string.accessibility_common_logo_mastodon
                            )
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(id = R.string.scene_sign_in_sign_in_with_mastodon)
                        )
                    },
                    trailing = {
                        IconButton(
                            enabled = false,
                            onClick = {},
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = stringResource(
                                    id = R.string.scene_sign_in_sign_in_with_mastodon
                                )
                            )
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TwitterSignIn() {
    val navController = LocalNavController.current
    var showKeyConfiguration by remember { mutableStateOf(false) }
    if (showKeyConfiguration) {
        TwitterCustomKeySignIn(
            onDismissRequest = {
                showKeyConfiguration = false
            }
        )
    }
    SignInButton(
        onClick = {
            navController.navigate(
                Route.SignIn.Twitter(
                    BuildConfig.CONSUMERKEY,
                    BuildConfig.CONSUMERSECRET,
                )
            )
        },
    ) {
        ListItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_twitter_logo_white),
                    contentDescription = stringResource(
                        id = R.string.accessibility_common_logo_twitter
                    )
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.scene_sign_in_sign_in_with_twitter)
                )
            },
            trailing = {
                IconButton(onClick = { showKeyConfiguration = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = stringResource(id = R.string.accessibility_common_more)
                    )
                }
            }
        )
    }
}

@Composable
private fun TwitterCustomKeySignIn(
    onDismissRequest: () -> Unit,
) {
    val navController = LocalNavController.current
    var apiKey by remember { mutableStateOf("") }
    var apiSecret by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = {
            onDismissRequest.invoke()
        },
        title = {
            Text(text = stringResource(id = R.string.scene_sign_in_twitter_options_sign_in_with_custom_twitter_key))
        },
        text = {
            Column {
                Text(text = stringResource(id = R.string.scene_sign_in_twitter_options_twitter_api_v2_access_is_required))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    placeholder = {
                        Text(text = "API key")
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = apiSecret,
                    onValueChange = { apiSecret = it },
                    placeholder = {
                        Text(text = "API secret key")
                    }
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest.invoke()
                }
            ) {
                Text(text = stringResource(id = R.string.common_controls_actions_cancel))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    navController.navigate(
                        Route.SignIn.Twitter(
                            apiKey,
                            apiSecret,
                        )
                    )
                }
            ) {
                Text(text = stringResource(id = R.string.scene_drawer_sign_in))
            }
        },
    )
}
