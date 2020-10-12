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
 
package com.twidere.twiderex.fragment

import androidx.compose.foundation.BaseTextField
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gif
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Pages
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Topic
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.onActive
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focusRequester
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import com.twidere.twiderex.component.AppBar
import com.twidere.twiderex.component.AppBarNavigationButton
import com.twidere.twiderex.component.NetworkImage
import com.twidere.twiderex.ui.profileImageSize
import com.twidere.twiderex.viewmodel.ActiveAccountViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ComposeFragment : JetFragment() {

    @OptIn(ExperimentalFoundationApi::class, ExperimentalFocus::class)
    @Composable
    override fun onCompose() {
        val textState = remember { mutableStateOf(TextFieldValue()) }
        val activeAccountViewModel = viewModel<ActiveAccountViewModel>()

        val focusRequester = FocusRequester()
        onActive {
            focusRequester.requestFocus()
        }
        Scaffold(
            topBar = {
                AppBar(
                    title = {
                        Text(text = "Compose")
                    },
                    navigationIcon = {
                        AppBarNavigationButton(icon = Icons.Default.Close)
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(asset = Icons.Default.Send)
                        }
                    }
                )
            }
        ) {
            Column {
                Row(
                    modifier = Modifier.weight(1F)
                        .padding(16.dp)
                ) {
                    Column {
                        NetworkImage(
                            url = activeAccountViewModel.account.user.profileImage,
                            modifier = Modifier
                                .clip(CircleShape)
                                .width(profileImageSize)
                                .height(profileImageSize)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .width(profileImageSize / 2)
                                .height(profileImageSize / 2),
                        ) {
                            CircularProgressIndicator(
                                progress = 1f,
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                            )
                            CircularProgressIndicator(
                                progress = textState.value.text.length.toFloat() / 1000f,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier.weight(1F)
                    ) {
                        BaseTextField(
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.TopStart)
                                .focusRequester(focusRequester),
                            value = textState.value,
                            onValueChange = { textState.value = it },
                        )
                    }
                }
                Divider()
                Box {
                    Row {
                        IconButton(onClick = {}) {
                            Icon(asset = Icons.Default.Camera)
                        }
                        IconButton(onClick = {}) {
                            Icon(asset = Icons.Default.Gif)
                        }
                        IconButton(onClick = {}) {
                            Icon(asset = Icons.Default.AlternateEmail)
                        }
                        IconButton(onClick = {}) {
                            Icon(asset = Icons.Default.Topic)
                        }
                        IconButton(onClick = {}) {
                            Icon(asset = Icons.Default.MyLocation)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = {}) {
                            Icon(asset = Icons.Default.Pages)
                        }
                    }
                }
            }
        }
    }
}
