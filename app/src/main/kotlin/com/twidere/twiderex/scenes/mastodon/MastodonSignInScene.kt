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
package com.twidere.twiderex.scenes.mastodon

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.SignInButton
import com.twidere.twiderex.component.foundation.SignInScaffold
import com.twidere.twiderex.component.navigation.INavigator
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.viewmodel.mastodon.MastodonSignInViewModel
import moe.tlaster.precompose.navigation.NavController

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MastodonSignInScene() {
    val viewModel =
        assistedViewModel<MastodonSignInViewModel.AssistedFactory, MastodonSignInViewModel> {
            it.create()
        }
    val host by viewModel.host.observeAsState(initial = TextFieldValue())
    val loading by viewModel.loading.observeAsState(initial = false)
    val navController = LocalNavController.current
    val navigator = LocalNavigator.current
    SignInScaffold {
        if (loading == true) {
            CircularProgressIndicator()
        } else {
            val focusRequester = remember { FocusRequester() }
            LaunchedEffect(focusRequester) {
                focusRequester.requestFocus()
            }
            OutlinedTextField(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .fillMaxWidth(),
                value = host,
                onValueChange = { viewModel.setHost(it) },
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Go,
                ),
                keyboardActions = KeyboardActions(
                    onGo = {
                        signin(viewModel, host, navController, navigator)
                    }
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            SignInButton(
                onClick = {
                    signin(viewModel, host, navController, navigator)
                }
            ) {
                ListItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_mastodon_logo_white),
                            contentDescription = stringResource(
                                id = R.string.accessibility_common_logo_mastodon
                            )
                        )
                    },
                    text = {
                        Text(text = stringResource(id = R.string.scene_sign_in_sign_in_with_mastodon))
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

private fun signin(
    viewModel: MastodonSignInViewModel,
    host: TextFieldValue,
    navController: NavController,
    navigator: INavigator
) {
    viewModel.beginOAuth(
        host.text,
        { target ->
            navigator.mastodonSignInWeb(target)
        },
        { success ->
            if (success) {
                navController.goBackWith(success)
            }
        },
    )
}
