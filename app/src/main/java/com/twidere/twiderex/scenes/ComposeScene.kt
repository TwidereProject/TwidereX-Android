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

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRowForIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AmbientContentAlpha
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.node.Ref
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.NetworkImage
import com.twidere.twiderex.component.foundation.TextInput
import com.twidere.twiderex.component.status.StatusLineComponent
import com.twidere.twiderex.component.status.TimelineStatusComponent
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.extensions.checkAllSelfPermissionsGranted
import com.twidere.twiderex.extensions.withElevation
import com.twidere.twiderex.launcher.AmbientLauncher
import com.twidere.twiderex.maxComposeTextLength
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.ui.AmbientActiveAccount
import com.twidere.twiderex.ui.AmbientNavController
import com.twidere.twiderex.ui.TwidereXTheme
import com.twidere.twiderex.ui.composeImageSize
import com.twidere.twiderex.ui.profileImageSize
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.viewmodel.ComposeViewModel
import kotlinx.coroutines.launch

enum class ComposeType {
    New,
    Reply,
    Quote,
}

@OptIn(ExperimentalFocus::class, ExperimentalFoundationApi::class)
@Composable
fun ComposeScene(statusId: String? = null, composeType: ComposeType = ComposeType.New) {
    val account = AmbientActiveAccount.current ?: return
    val viewModel = assistedViewModel<ComposeViewModel.AssistedFactory, ComposeViewModel> {
        it.create(account, statusId)
    }
    val status by viewModel.status.observeAsState()
    val (text, setText) = remember { mutableStateOf("") }
    val images by viewModel.images.observeAsState(initial = emptyList())
    val location by viewModel.location.observeAsState()
    val locationEnabled by viewModel.locationEnabled.observeAsState(initial = false)
    val navController = AmbientNavController.current
    val keyboardController = remember { Ref<SoftwareKeyboardController>() }
    TwidereXTheme {
        Scaffold(
            topBar = {
                AppBar(
                    title = {
                        Text(
                            text = when (composeType) {
                                ComposeType.Reply -> stringResource(id = R.string.title_reply)
                                ComposeType.Quote -> stringResource(id = R.string.title_quote)
                                else -> stringResource(id = R.string.title_compose)
                            }
                        )
                    },
                    navigationIcon = {
                        AppBarNavigationButton(icon = vectorResource(id = R.drawable.ic_x))
                    },
                    actions = {
                        IconButton(
                            enabled = text.isNotEmpty(),
                            onClick = {
                                viewModel.compose(text, composeType, status)
                                navController.popBackStack()
                            }
                        ) {
                            Icon(asset = vectorResource(id = R.drawable.ic_send))
                        }
                    }
                )
            }
        ) {
            Column {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    if (composeType == ComposeType.New) {
                        ComposeInput(
                            account,
                            text,
                            setText,
                            keyboardController
                        )
                    } else {
                        status?.let { status ->
                            val listState = rememberLazyListState(
                                initialFirstVisibleItemIndex = if (composeType == ComposeType.Reply) {
                                    1
                                } else {
                                    0
                                }
                            )
                            if (listState.firstVisibleItemIndex == 0) {
                                keyboardController.value?.hideSoftwareKeyboard()
                            } else if (listState.firstVisibleItemIndex == 1) {
                                keyboardController.value?.showSoftwareKeyboard()
                            }
                            LazyColumn(
                                state = listState,
                            ) {
                                if (composeType == ComposeType.Reply) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .background(MaterialTheme.colors.surface.withElevation())
                                        ) {
                                            StatusLineComponent(lineDown = true) {
                                                TimelineStatusComponent(
                                                    data = status,
                                                    showActions = false,
                                                )
                                            }
                                        }
                                    }
                                }
                                item {
                                    StatusLineComponent(
                                        modifier = Modifier.fillParentMaxSize(),
                                        lineUp = composeType == ComposeType.Reply,
                                    ) {
                                        ComposeInput(
                                            account,
                                            text,
                                            setText,
                                            keyboardController,
                                            autoFocus = if (composeType == ComposeType.Reply) {
                                                listState.firstVisibleItemIndex == 1
                                            } else {
                                                true
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (images.any()) {
                    LazyRowForIndexed(
                        modifier = Modifier.padding(horizontal = standardPadding * 2),
                        items = images
                    ) { index, item ->
                        ComposeImage(item, viewModel)
                        if (index != images.lastIndex) {
                            Spacer(modifier = Modifier.width(standardPadding))
                        }
                    }
                    Spacer(modifier = Modifier.height(standardPadding * 2))
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = standardPadding * 2)
                ) {
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
                            progress = text.length.toFloat() / maxComposeTextLength.toFloat(),
                        )
                    }
                    Spacer(modifier = Modifier.weight(1F))
                    if (locationEnabled) {
                        location?.let {
                            Providers(
                                AmbientContentAlpha provides ContentAlpha.medium
                            ) {
                                Row {
                                    Icon(asset = vectorResource(id = R.drawable.ic_map_pin))
                                    Text(text = "${it.latitude}, ${it.longitude}")
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(standardPadding * 2))
                Divider()
                ComposeActions(viewModel)
            }
        }
    }
}

@ExperimentalFocus
@ExperimentalFoundationApi
@Composable
private fun ComposeInput(
    account: AccountDetails?,
    text: String,
    setText: (String) -> Unit,
    keyboardController: Ref<SoftwareKeyboardController>,
    autoFocus: Boolean = true,
) {
    Row(
        modifier = Modifier
            .padding(16.dp),
    ) {
        account?.let {
            NetworkImage(
                url = it.user.profileImage,
                modifier = Modifier
                    .clip(CircleShape)
                    .width(profileImageSize)
                    .height(profileImageSize)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Box(
            modifier = Modifier.weight(1F)
        ) {
            TextInput(
                modifier = Modifier.align(Alignment.TopCenter),
                value = text,
                onValueChange = { setText(it) },
                autoFocus = autoFocus,
                onTextInputStarted = {
                    keyboardController.value = it
                },
                onClicked = {
                    // TODO: scroll lazyColumn
                }
            )
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
private fun ComposeActions(viewModel: ComposeViewModel) {
    val locationEnabled by viewModel.locationEnabled.observeAsState(initial = false)
    val launcher = AmbientLauncher.current
    val scope = rememberCoroutineScope()
    val context = ContextAmbient.current
    Box {
        Row {
            IconButton(
                onClick = {
                    scope.launch {
                        val item =
                            launcher.launchMultipleFilePicker("image/*")
                        viewModel.putImages(item)
                    }
                }
            ) {
                Icon(asset = vectorResource(id = R.drawable.ic_camera))
            }
            IconButton(onClick = {}) {
                Icon(asset = vectorResource(id = R.drawable.ic_gif))
            }
            IconButton(onClick = {}) {
                Icon(asset = vectorResource(id = R.drawable.ic_at_sign))
            }
            IconButton(onClick = {}) {
                Icon(asset = vectorResource(id = R.drawable.ic_hash))
            }
            IconButton(
                onClick = {
                    if (locationEnabled) {
                        viewModel.disableLocation()
                    } else {
                        scope.launch {
                            val permissions = arrayOf(
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                            val hasPermissions =
                                if (!context.checkAllSelfPermissionsGranted(*permissions)) {
                                    launcher.requestMultiplePermissions(permissions)
                                        .all { it.value }
                                } else {
                                    true
                                }
                            if (hasPermissions) {
                                viewModel.trackingLocation()
                            }
                        }
                    }
                },
            ) {
                Icon(asset = vectorResource(id = R.drawable.ic_map_pin))
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = {}) {
                Icon(asset = vectorResource(id = R.drawable.ic_note))
            }
        }
    }
}

@Composable
private fun ComposeImage(item: Uri, viewModel: ComposeViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val image = @Composable {
        Box(
            modifier = Modifier
                .heightIn(max = composeImageSize)
                .aspectRatio(1F)
                .clickable(
                    onClick = {
                        expanded = true
                    }
                )
                .clip(MaterialTheme.shapes.small),
        ) {
            NetworkImage(url = item)
        }
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        toggle = image,
    ) {
        DropdownMenuItem(
            onClick = {
                expanded = false
                viewModel.removeImage(item)
            }
        ) {
            Text(text = stringResource(id = R.string.action_remove))
        }
    }
}
