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
package com.twidere.twiderex.scenes.compose

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.AmbientContentAlpha
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.rememberBottomSheetScaffoldState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.node.Ref
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.navigate
import com.twidere.twiderex.R
import com.twidere.twiderex.component.BackButtonHandler
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.InAppNotificationBottomSheetScaffold
import com.twidere.twiderex.component.foundation.NetworkImage
import com.twidere.twiderex.component.foundation.TextInput
import com.twidere.twiderex.component.status.StatusLineComponent
import com.twidere.twiderex.component.status.TimelineStatusComponent
import com.twidere.twiderex.component.status.UserAvatar
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.extensions.checkAllSelfPermissionsGranted
import com.twidere.twiderex.extensions.navigateForResult
import com.twidere.twiderex.extensions.withElevation
import com.twidere.twiderex.launcher.AmbientLauncher
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.navigation.Route
import com.twidere.twiderex.ui.AmbientActiveAccount
import com.twidere.twiderex.ui.AmbientNavController
import com.twidere.twiderex.ui.Orange
import com.twidere.twiderex.ui.TwidereXTheme
import com.twidere.twiderex.ui.composeImageSize
import com.twidere.twiderex.ui.profileImageSize
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.viewmodel.compose.ComposeType
import com.twidere.twiderex.viewmodel.compose.ComposeViewModel
import com.twidere.twiderex.viewmodel.compose.DraftComposeViewModel
import com.twidere.twiderex.viewmodel.compose.DraftItemViewModel
import com.twitter.twittertext.TwitterTextConfiguration
import com.twitter.twittertext.TwitterTextParser
import kotlinx.coroutines.launch

@Composable
fun DraftComposeScene(
    draftId: String,
) {
    val account = AmbientActiveAccount.current ?: return
    val draftItemViewModel =
        assistedViewModel<DraftItemViewModel.AssistedFactory, DraftItemViewModel> {
            it.create(draftId = draftId)
        }
    val data by draftItemViewModel.draft.observeAsState()
    data?.let { draft ->
        val viewModel =
            assistedViewModel<DraftComposeViewModel.AssistedFactory, DraftComposeViewModel> {
                it.create(
                    account = account,
                    draft = draft,
                )
            }
        ComposeBody(viewModel = viewModel, account = account)
    }
}

@Composable
fun ComposeScene(
    statusKey: MicroBlogKey? = null,
    composeType: ComposeType = ComposeType.New,
) {
    val account = AmbientActiveAccount.current ?: return
    val viewModel = assistedViewModel<ComposeViewModel.AssistedFactory, ComposeViewModel> {
        it.create(account, statusKey, composeType)
    }
    ComposeBody(viewModel = viewModel, account = account)
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
private fun ComposeBody(
    viewModel: ComposeViewModel,
    account: AccountDetails,
) {
    val composeType = viewModel.composeType
    val status by viewModel.status.observeAsState()
    val images by viewModel.images.observeAsState(initial = emptyList())
    val location by viewModel.location.observeAsState()
    val locationEnabled by viewModel.locationEnabled.observeAsState(initial = false)
    val navController = AmbientNavController.current
    val textFieldValue by viewModel.textFieldValue.observeAsState(initial = TextFieldValue())
    val keyboardController = remember { Ref<SoftwareKeyboardController>() }
    val canSaveDraft by viewModel.canSaveDraft.observeAsState(initial = false)
    var showSaveDraftDialog by remember { mutableStateOf(false) }
    val scaffoldState = rememberBottomSheetScaffoldState()
    BackButtonHandler {
        when {
            showSaveDraftDialog -> {
                showSaveDraftDialog = false
            }
            canSaveDraft -> {
                showSaveDraftDialog = true
            }
            else -> {
                navController.popBackStack()
            }
        }
    }
    TwidereXTheme {
        if (showSaveDraftDialog) {
            ConfirmDraftDialog(
                onDismiss = {
                    showSaveDraftDialog = false
                },
                onConfirm = {
                    showSaveDraftDialog = false
                    viewModel.saveDraft()
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
        InAppNotificationBottomSheetScaffold(
            sheetPeekHeight = 0.dp,
            scaffoldState = scaffoldState,
            sheetContent = {
                ReplySheetContent(viewModel = viewModel, scaffoldState = scaffoldState)
            },
            topBar = {
                AppBar(
                    title = {
                        Text(
                            text = when (composeType) {
                                ComposeType.Reply -> stringResource(id = R.string.scene_compose_title_reply)
                                ComposeType.Quote -> stringResource(id = R.string.scene_compose_title_quote)
                                else -> stringResource(id = R.string.scene_compose_title_compose)
                            }
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (canSaveDraft) {
                                    showSaveDraftDialog = true
                                } else {
                                    navController.popBackStack()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = vectorResource(id = R.drawable.ic_x),
                                contentDescription = stringResource(
                                    id = R.string.accessibility_common_close
                                )
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            enabled = textFieldValue.text.isNotEmpty(),
                            onClick = {
                                viewModel.compose()
                                navController.popBackStack()
                            }
                        ) {
                            Icon(
                                imageVector = vectorResource(id = R.drawable.ic_send),
                                contentDescription = stringResource(
                                    id = R.string.accessibility_scene_compose_send
                                )
                            )
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
                            scaffoldState,
                            viewModel,
                            account,
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
                                            scaffoldState,
                                            viewModel,
                                            account,
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
                    LazyRow(
                        modifier = Modifier.padding(horizontal = standardPadding * 2),
                    ) {
                        itemsIndexed(
                            items = images,
                        ) { index, item ->
                            ComposeImage(item, viewModel)
                            if (index != images.lastIndex) {
                                Spacer(modifier = Modifier.width(standardPadding))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(standardPadding * 2))
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = standardPadding * 2)
                ) {
                    val maxLength = remember {
                        TwitterTextConfiguration.getDefaultConfig().maxWeightedTweetLength
                    }
                    val textLength = remember(textFieldValue) {
                        TwitterTextParser.parseTweet(textFieldValue.text).weightedLength
                    }
                    val progress = remember(textLength) {
                        textLength.toFloat() / maxLength.toFloat()
                    }
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
                            progress = progress,
                            color = when {
                                progress < 0.9 -> MaterialTheme.colors.primary
                                progress >= 0.9 && progress < 1.0 -> Orange
                                else -> Color.Red
                            },
                        )
                    }
                    Box(modifier = Modifier.width(4.dp))
                    if (progress > 1.0) {
                        Text(text = (maxLength - textLength).toString(), color = Color.Red)
                    }
                    Spacer(modifier = Modifier.weight(1F))
                    if (locationEnabled) {
                        location?.let {
                            Providers(
                                AmbientContentAlpha provides ContentAlpha.medium
                            ) {
                                Row {
                                    Icon(
                                        imageVector = vectorResource(id = R.drawable.ic_map_pin),
                                        contentDescription = stringResource(
                                            id = R.string.accessibility_common_status_location
                                        )
                                    )
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

@ExperimentalMaterialApi
@Composable
private fun ReplySheetContent(
    viewModel: ComposeViewModel,
    scaffoldState: BottomSheetScaffoldState,
) {
    if (viewModel.composeType != ComposeType.Reply) {
        return
    }
    val replyToUser by viewModel.replyToUser.observeAsState(initial = emptyList())
    val excludedUserIds by viewModel.excludedReplyUserIds.observeAsState(initial = emptyList())
    val status by viewModel.status.observeAsState(initial = null)
    ListItem(
        icon = {
            IconButton(
                onClick = {
                    scaffoldState.bottomSheetState.collapse()
                }
            ) {
                Icon(
                    imageVector = vectorResource(id = R.drawable.ic_x),
                    contentDescription = stringResource(
                        id = R.string.accessibility_common_close
                    )
                )
            }
        },
        text = {
            Text(text = stringResource(id = R.string.scene_compose_replying_to))
        }
    )
    status?.let {
        ListItem(
            icon = {
                UserAvatar(
                    user = it.user,
                )
            },
            text = {
                Text(text = it.user.name)
            },
            secondaryText = {
                Text(text = it.user.screenName)
            },
            trailing = {
                IconButton(onClick = {}) {
                    Checkbox(
                        checked = true,
                        onCheckedChange = {},
                        enabled = false,
                    )
                }
            }
        )
    }
    if (replyToUser.any()) {
        ListItem {
            Text(text = stringResource(id = R.string.scene_compose_others_in_this_conversation))
        }
        Divider(modifier = Modifier.padding(horizontal = 16.dp))
        replyToUser.forEach { user ->
            val excluded = excludedUserIds.contains(user.id)
            ListItem(
                modifier = Modifier.clickable(
                    onClick = {
                        if (excluded) {
                            viewModel.includeReplyUser(user)
                        } else {
                            viewModel.excludeReplyUser(user)
                        }
                    }
                ),
                icon = {
                    UserAvatar(
                        user = user,
                    )
                },
                text = {
                    Text(text = user.name)
                },
                secondaryText = {
                    Text(text = user.screenName)
                },
                trailing = {
                    IconButton(onClick = {}) {
                        Checkbox(
                            checked = !excluded,
                            onCheckedChange = {
                                if (excluded) {
                                    viewModel.includeReplyUser(user)
                                } else {
                                    viewModel.excludeReplyUser(user)
                                }
                            },
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun ConfirmDraftDialog(
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
    onCancel: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Text(
                text = stringResource(id = R.string.scene_compose_save_draft_message),
                style = MaterialTheme.typography.body2
            )
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(text = stringResource(id = R.string.common_controls_actions_cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(id = R.string.scene_compose_save_draft_action))
            }
        },
    )
}

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
private fun ComposeInput(
    scaffoldState: BottomSheetScaffoldState,
    composeViewModel: ComposeViewModel,
    account: AccountDetails?,
    keyboardController: Ref<SoftwareKeyboardController>,
    autoFocus: Boolean = true,
) {
    val text by composeViewModel.textFieldValue.observeAsState(initial = TextFieldValue())
    Column {
        ComposeReply(composeViewModel = composeViewModel, scaffoldState = scaffoldState)
        if (composeViewModel.composeType != ComposeType.Reply) {
            Box(modifier = Modifier.height(16.dp))
        }
        Row(
            modifier = Modifier
                .padding(start = 16.dp, bottom = 16.dp, end = 16.dp),
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
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxSize(),
                    value = text,
                    onValueChange = { composeViewModel.setText(it) },
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
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ComposeReply(
    composeViewModel: ComposeViewModel,
    scaffoldState: BottomSheetScaffoldState,
) {
    val composeType = composeViewModel.composeType
    if (composeType != ComposeType.Reply) {
        return
    }
    val viewModelStatus by composeViewModel.status.observeAsState(initial = null)
    viewModelStatus?.let { status ->
        val replyToUser by composeViewModel.replyToUser.observeAsState(initial = emptyList())
        val excludedUserIds by composeViewModel.excludedReplyUserIds.observeAsState(initial = emptyList())
        val loadingReplyUser by composeViewModel.loadingReplyUser.observeAsState(initial = false)
        Row(
            modifier = Modifier
                .clickable(
                    onClick = {
                        if (scaffoldState.bottomSheetState.isExpanded) {
                            scaffoldState.bottomSheetState.collapse()
                        } else {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }
                )
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
        ) {
            Box(
                modifier = Modifier
                    .width(profileImageSize)
                    .height(profileImageSize)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier.weight(1F)
            ) {
                if (loadingReplyUser) {
                    LinearProgressIndicator()
                } else {
                    Text(
                        text = (listOf(status.user) + replyToUser).filter {
                            !excludedUserIds.contains(
                                it.id
                            )
                        }
                            .joinToString(",") { "@${it.screenName}" },
                        color = MaterialTheme.colors.primary,
                    )
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
private fun ComposeActions(viewModel: ComposeViewModel) {
    val locationEnabled by viewModel.locationEnabled.observeAsState(initial = false)
    val launcher = AmbientLauncher.current
    val scope = rememberCoroutineScope()
    val context = AmbientContext.current
    val navController = AmbientNavController.current
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
                Icon(
                    imageVector = vectorResource(id = R.drawable.ic_camera),
                    contentDescription = stringResource(
                        id = R.string.accessibility_scene_compose_image
                    )
                )
            }
            // TODO:
//            IconButton(onClick = {}) {
//                Icon(imageVector = vectorResource(id = R.drawable.ic_gif))
//            }
            IconButton(
                onClick = {
                    scope.launch {
                        val result = navController.navigateForResult<String>("user_name") {
                            navigate(Route.Compose.Search.User)
                        }
                        if (result != null) {
                            viewModel.insertText("@$result ")
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = vectorResource(id = R.drawable.ic_at_sign),
                    contentDescription = stringResource(
                        id = R.string.accessibility_scene_compose_at
                    )
                )
            }
//            IconButton(onClick = {}) {
//                Icon(imageVector = vectorResource(id = R.drawable.ic_hash))
//            }
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
                Icon(
                    imageVector = vectorResource(id = R.drawable.ic_map_pin),
                    contentDescription = stringResource(
                        id = if (locationEnabled) {
                            R.string.accessibility_scene_compose_location_disable
                        } else {
                            R.string.accessibility_scene_compose_location_enable
                        }
                    )
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    navController.navigate(Route.Draft.List)
                }
            ) {
                Icon(
                    imageVector = vectorResource(id = R.drawable.ic_note),
                    contentDescription = stringResource(
                        id = R.string.accessibility_scene_compose_draft
                    )
                )
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
            Text(
                text = stringResource(id = R.string.common_controls_actions_remove),
                color = Color.Red,
            )
        }
    }
}
