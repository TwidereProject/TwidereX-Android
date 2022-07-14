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
package com.twidere.twiderex.scenes.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.MR
import com.twidere.twiderex.component.ImeVisibleWithInsets
import com.twidere.twiderex.component.foundation.AlertDialog
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.CheckboxItem
import com.twidere.twiderex.component.foundation.DropdownMenu
import com.twidere.twiderex.component.foundation.DropdownMenuItem
import com.twidere.twiderex.component.foundation.GifTag
import com.twidere.twiderex.component.foundation.InAppNotificationBottomSheetScaffold
import com.twidere.twiderex.component.foundation.NetworkImage
import com.twidere.twiderex.component.foundation.TextInput
import com.twidere.twiderex.component.foundation.platform.PlatformEmojiPanel
import com.twidere.twiderex.component.media.MediaInsertMenu
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.status.StatusLineComponent
import com.twidere.twiderex.component.status.TimelineStatusComponent
import com.twidere.twiderex.component.status.UserAvatar
import com.twidere.twiderex.component.status.UserAvatarDefaults
import com.twidere.twiderex.component.status.UserName
import com.twidere.twiderex.component.status.UserScreenName
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.di.ext.getViewModel
import com.twidere.twiderex.extensions.icon
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.extensions.stringName
import com.twidere.twiderex.extensions.withElevation
import com.twidere.twiderex.kmp.Platform
import com.twidere.twiderex.kmp.RequestLocationPermission
import com.twidere.twiderex.kmp.currentPlatform
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.ComposeType
import com.twidere.twiderex.model.enums.MastodonVisibility
import com.twidere.twiderex.model.enums.MediaType
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.ui.UiMediaInsert
import com.twidere.twiderex.navigation.Root
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.Orange
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.compose.ComposeViewModel
import com.twidere.twiderex.viewmodel.compose.DraftComposeViewModel
import com.twidere.twiderex.viewmodel.compose.DraftItemViewModel
import com.twidere.twiderex.viewmodel.compose.VoteExpired
import com.twidere.twiderex.viewmodel.compose.VoteState
import com.twitter.twittertext.TwitterTextParser
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.BackHandler
import org.koin.core.parameter.parametersOf

@Composable
fun DraftComposeScene(
    draftId: String,
) {
    val account = LocalActiveAccount.current ?: return
    val draftItemViewModel: DraftItemViewModel = getViewModel {
        parametersOf(draftId)
    }
    val data by draftItemViewModel.draft.observeAsState(null)
    data?.let { draft ->
        val viewModel: DraftComposeViewModel = getViewModel {
            parametersOf(
                draft,
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
    val account = LocalActiveAccount.current ?: return
    val viewModel: ComposeViewModel = getViewModel {
        parametersOf(statusKey, composeType)
    }
    ComposeBody(viewModel = viewModel, account = account)
}

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class,
)
@Composable
private fun ComposeBody(
    viewModel: ComposeViewModel,
    account: AccountDetails,
) {
    val composeType = viewModel.composeType
    val status by viewModel.status.observeAsState(null)
    val images by viewModel.images.observeAsState(initial = emptyList())
    val location by viewModel.location.observeAsState(null)
    val locationEnabled by viewModel.locationEnabled.observeAsState(initial = false)
    val navController = LocalNavController.current
    val textFieldValue by viewModel.textFieldValue.observeAsState(initial = TextFieldValue())
    val keyboardController = LocalSoftwareKeyboardController.current
    val canSaveDraft by viewModel.canSaveDraft.observeAsState(initial = false)
    val enableThreadMode by viewModel.enableThreadMode.observeAsState(initial = false)
    var showSaveDraftDialog by remember { mutableStateOf(false) }
    val scaffoldState = rememberBottomSheetScaffoldState()
    val emojis by viewModel.emojis.observeAsState(emptyList())
    val maxLength by viewModel.maxContentLength.observeAsState(initial = 1)
    if (showSaveDraftDialog || canSaveDraft) {
        BackHandler {
            when {
                showSaveDraftDialog -> {
                    showSaveDraftDialog = false
                }
                canSaveDraft -> {
                    showSaveDraftDialog = true
                }
            }
        }
    }
    TwidereScene {
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
                                ComposeType.Reply -> stringResource(res = com.twidere.twiderex.MR.strings.scene_compose_title_reply)
                                ComposeType.Quote -> stringResource(res = com.twidere.twiderex.MR.strings.scene_compose_title_quote)
                                else -> stringResource(res = com.twidere.twiderex.MR.strings.scene_compose_title_compose)
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
                                painter = painterResource(res = com.twidere.twiderex.MR.files.ic_x),
                                contentDescription = stringResource(
                                    res = com.twidere.twiderex.MR.strings.accessibility_common_close
                                )
                            )
                        }
                    },
                    actions = {
                        val canSend by viewModel.canSend.observeAsState(initial = false)
                        IconButton(
                            enabled = canSend,
                            onClick = {
                                viewModel.compose()
                                navController.popBackStack()
                            }
                        ) {
                            Icon(
                                painter = painterResource(res = if (enableThreadMode) com.twidere.twiderex.MR.files.ic_send_thread else com.twidere.twiderex.MR.files.ic_send),
                                contentDescription = stringResource(
                                    res = if (enableThreadMode) com.twidere.twiderex.MR.strings.accessibility_scene_compose_thread else com.twidere.twiderex.MR.strings.accessibility_scene_compose_send
                                ),
                                tint = if (canSend) MaterialTheme.colors.primary else LocalContentColor.current.copy(
                                    alpha = LocalContentAlpha.current
                                )
                            )
                        }
                    }
                )
            }
        ) {
            Column {
                BoxWithConstraints(
                    modifier = Modifier
                        .weight(1f),
                ) {
                    val scrollState = rememberScrollState()
                    LaunchedEffect(scrollState) {
                        if (composeType == ComposeType.Reply || composeType == ComposeType.Thread) {
                            snapshotFlow { scrollState.value }
                                .map { it > 0 }
                                .distinctUntilChanged()
                                .collect {
                                    if (it) {
                                        keyboardController?.hide()
                                    } else {
                                        keyboardController?.show()
                                    }
                                }
                        }
                    }
                    val focusRequester = remember {
                        FocusRequester()
                    }
                    Column(
                        modifier = Modifier
                            .verticalScroll(
                                scrollState,
                                reverseScrolling = composeType == ComposeType.Reply || composeType == ComposeType.Thread,
                            )
                            .clickable(
                                onClick = {
                                    focusRequester.requestFocus()
                                    keyboardController?.show()
                                },
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            )
                    ) {
                        val height = with(LocalDensity.current) {
                            this@BoxWithConstraints.constraints.maxHeight.toDp()
                        }
                        if (composeType == ComposeType.Reply || composeType == ComposeType.Thread) {
                            status?.let { status ->
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
                        StatusLineComponent(
                            modifier = Modifier.let {
                                if (composeType != ComposeType.Quote) {
                                    it.heightIn(min = height)
                                } else {
                                    it
                                }
                            },
                            lineUp = composeType == ComposeType.Reply || composeType == ComposeType.Thread,
                        ) {
                            ComposeInput(
                                scaffoldState,
                                viewModel,
                                account,
                                autoFocus = if (composeType == ComposeType.Reply || composeType == ComposeType.Thread) {
                                    scrollState.value == 0
                                } else {
                                    true
                                },
                                focusRequester = focusRequester,
                            )
                        }
                        if (composeType == ComposeType.Quote) {
                            status?.let { status ->
                                Box(
                                    modifier = Modifier.background(
                                        LocalContentColor.current.copy(
                                            alpha = 0.04f
                                        )
                                    ),
                                ) {
                                    TimelineStatusComponent(
                                        data = status,
                                        showActions = false,
                                    )
                                }
                            }
                        }
                    }
                }

                if (images.any()) {
                    ComposeImageList(images, viewModel)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextProgress(textFieldValue, maxLength)
                    if (account.type == PlatformType.Mastodon) {
                        ComposeMastodonVisibility(
                            modifier = Modifier.weight(1f),
                            viewModel = viewModel,
                        )
                        CompositionLocalProvider(LocalContentAlpha.provides(ContentAlpha.medium)) {
                            MastodonExtraActions(images.map { it.filePath }, viewModel)
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1F))
                    }
                    if (locationEnabled) {
                        location?.let {
                            LocationDisplay(it)
                        }
                    }
                }
                Divider()
                var showEmoji by remember { mutableStateOf(false) }
                ImeVisibleWithInsets(
                    filter = {
                        it && showEmoji
                    },
                    collectIme = {
                        showEmoji = false
                    }
                )
                LaunchedEffect(showEmoji) {
                    if (showEmoji) {
                        keyboardController?.hide()
                    } else {
                        keyboardController?.show()
                    }
                }
                CompositionLocalProvider(LocalContentAlpha.provides(ContentAlpha.medium)) {
                    ComposeActions(
                        viewModel,
                        showEmoji = showEmoji,
                        emojiButtonClicked = {
                            showEmoji = !showEmoji
                        },
                    )
                }
                PlatformEmojiPanel(
                    showEmoji = showEmoji,
                    items = emojis,
                    onEmojiSelected = {
                        viewModel.insertEmoji(it)
                    },
                )
            }
        }
    }
}

@Composable
private fun ComposeImageList(
    images: List<UiMediaInsert>,
    viewModel: ComposeViewModel
) {
    Spacer(modifier = Modifier.height(ComposeImageListDefaults.Spacing))
    LazyRow(
        modifier = Modifier.padding(ComposeImageListDefaults.ContentPadding),
    ) {
        itemsIndexed(
            items = images,
        ) { index, item ->
            ComposeImage(item, viewModel)
            if (index != images.lastIndex) {
                Spacer(modifier = Modifier.width(ComposeImageListDefaults.ItemSpacing))
            }
        }
    }
}

private object ComposeImageListDefaults {
    val Spacing = 16.dp
    val ItemSpacing = 8.dp
    val ContentPadding = PaddingValues(
        horizontal = 16.dp,
        vertical = 0.dp
    )
}

@Composable
private fun MastodonExtraActions(
    images: List<String>,
    viewModel: ComposeViewModel
) {
    if (images.any()) {
        val isImageSensitive by viewModel.isImageSensitive.observeAsState(
            initial = false
        )
        IconButton(
            onClick = {
                viewModel.setImageSensitive(!isImageSensitive)
            }
        ) {
            Icon(
                painter = painterResource(res = com.twidere.twiderex.MR.files.ic_eye_off),
                contentDescription = null,
                tint = if (isImageSensitive) {
                    MaterialTheme.colors.primary
                } else {
                    LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
                }
            )
        }
    }
    val isContentWarning by viewModel.isContentWarningEnabled.observeAsState(
        initial = false
    )
    IconButton(
        onClick = {
            viewModel.setContentWarningEnabled(!isContentWarning)
        }
    ) {
        Icon(
            painter = painterResource(res = com.twidere.twiderex.MR.files.ic_alert_octagon),
            contentDescription = null,
            tint = if (isContentWarning) {
                MaterialTheme.colors.primary
            } else {
                LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
            }
        )
    }
}

@Composable
private fun LocationDisplay(it: com.twidere.twiderex.model.kmp.Location) {
    CompositionLocalProvider(
        LocalContentAlpha provides ContentAlpha.medium
    ) {
        Row {
            Icon(
                painter = painterResource(res = com.twidere.twiderex.MR.files.ic_map_pin),
                contentDescription = stringResource(
                    res = com.twidere.twiderex.MR.strings.accessibility_common_status_location
                )
            )
            Text(text = "${it.latitude}, ${it.longitude}")
            Spacer(modifier = Modifier.width(LocationDisplayDefaults.PaddingEnd))
        }
    }
}

private object LocationDisplayDefaults {
    val PaddingEnd = 8.dp
}

@Composable
private fun TextProgress(textFieldValue: TextFieldValue, maxLength: Int) {
    val textLength = remember(textFieldValue) {
        TwitterTextParser.parseTweet(textFieldValue.text).weightedLength
    }
    val progress = remember(textLength) {
        textLength.toFloat() / maxLength.toFloat()
    }
    Box(
        modifier = Modifier
            .size(48.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(UserAvatarDefaults.AvatarSize / 2),
            progress = 1f,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
        )
        CircularProgressIndicator(
            modifier = Modifier
                .size(UserAvatarDefaults.AvatarSize / 2),
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
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ComposeMastodonVisibility(
    modifier: Modifier = Modifier,
    viewModel: ComposeViewModel
) {
    var showDropdown by remember {
        mutableStateOf(false)
    }
    val visibility by viewModel.visibility.observeAsState(initial = MastodonVisibility.Public)
    Box(
        modifier = modifier
    ) {
        DropdownMenu(expanded = showDropdown, onDismissRequest = { showDropdown = false }) {
            MastodonVisibility.values().forEach {
                DropdownMenuItem(
                    onClick = {
                        showDropdown = false
                        viewModel.setVisibility(it)
                    }
                ) {
                    ListItem(
                        text = {
                            Text(text = it.stringName())
                        },
                        icon = {
                            Icon(painter = it.icon(), contentDescription = it.stringName())
                        }
                    )
                }
            }
        }
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colors.primary
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable {
                        showDropdown = !showDropdown
                    }
                    .padding(ComposeMastodonVisibilityDefaults.ContentPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painter = visibility.icon(), contentDescription = visibility.stringName())
                Spacer(modifier = Modifier.width(ComposeMastodonVisibilityDefaults.IconSpacing))
                Text(text = visibility.stringName())
            }
        }
    }
}

object ComposeMastodonVisibilityDefaults {
    val ContentPadding = PaddingValues(
        horizontal = 8.dp,
        vertical = 0.dp
    )
    val IconSpacing = 8.dp
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
    val scope = rememberCoroutineScope()
    ListItem(
        icon = {
            IconButton(
                onClick = {
                    scope.launch {
                        scaffoldState.bottomSheetState.collapse()
                    }
                }
            ) {
                Icon(
                    painter = painterResource(res = com.twidere.twiderex.MR.files.ic_x),
                    contentDescription = stringResource(
                        res = com.twidere.twiderex.MR.strings.accessibility_common_close
                    )
                )
            }
        },
        text = {
            Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_compose_replying_to))
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
                UserName(user = it.user)
            },
            secondaryText = {
                UserScreenName(user = it.user)
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
            Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_compose_others_in_this_conversation))
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
                    UserName(user = user)
                },
                secondaryText = {
                    UserScreenName(user = user)
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
                text = stringResource(res = com.twidere.twiderex.MR.strings.scene_compose_save_draft_message),
                style = MaterialTheme.typography.body2
            )
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(text = stringResource(res = com.twidere.twiderex.MR.strings.common_controls_actions_cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_compose_save_draft_action))
            }
        },
    )
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
private fun ComposeInput(
    scaffoldState: BottomSheetScaffoldState,
    viewModel: ComposeViewModel,
    account: AccountDetails,
    autoFocus: Boolean = true,
    focusRequester: FocusRequester,
) {
    val text by viewModel.textFieldValue.observeAsState(initial = TextFieldValue())
    Column {
        ComposeReply(composeViewModel = viewModel, scaffoldState = scaffoldState)
        Row(
            modifier = Modifier
                .padding(start = 16.dp, bottom = 16.dp, end = 16.dp),
        ) {
            NetworkImage(
                data = account.user.profileImage,
                modifier = Modifier
                    .clip(CircleShape)
                    .width(UserAvatarDefaults.AvatarSize)
                    .height(UserAvatarDefaults.AvatarSize)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1F)
            ) {
                if (account.type == PlatformType.Mastodon) {
                    MastodonContentWarningInput(viewModel)
                }
                TextInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    value = text,
                    onValueChange = { viewModel.setText(it) },
                    autoFocus = autoFocus,
                    placeholder = {
                        CompositionLocalProvider(LocalContentAlpha.provides(ContentAlpha.medium)) {
                            Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_compose_placeholder))
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                    )
                )

                val voteState by viewModel.voteState.observeAsState(initial = null)
                voteState?.let {
                    Spacer(modifier = Modifier.height(ComposeInputDefaults.VoteSpacing))
                    ComposeVote(voteState = it)
                }
            }
        }
    }
}

private object ComposeInputDefaults {
    val VoteSpacing = 16.dp
}

@ExperimentalAnimationApi
@Composable
private fun ColumnScope.MastodonContentWarningInput(viewModel: ComposeViewModel) {
    val isContentWarningEnabled by viewModel.isContentWarningEnabled.observeAsState(
        initial = false
    )
    AnimatedVisibility(visible = isContentWarningEnabled) {
        val cwText by viewModel.contentWarningTextFieldValue.observeAsState(initial = TextFieldValue())
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CompositionLocalProvider(
                    LocalContentAlpha provides ContentAlpha.disabled,
                ) {
                    Icon(
                        painter = painterResource(res = com.twidere.twiderex.MR.files.ic_alert_octagon),
                        contentDescription = null,
                    )
                }
                Spacer(modifier = Modifier.width(MastodonContentWarningInputDefaults.IconSpacing))
                TextInput(
                    value = cwText,
                    onValueChange = { viewModel.setContentWarningText(it) },
                    placeholder = {
                        CompositionLocalProvider(
                            LocalContentAlpha provides ContentAlpha.disabled,
                        ) {
                            Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_compose_cw_placeholder))
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                    )
                )
            }
            Spacer(modifier = Modifier.height(MastodonContentWarningInputDefaults.ContentPadding))
            Divider()
            Spacer(modifier = Modifier.height(MastodonContentWarningInputDefaults.ContentPadding))
        }
    }
}

private object MastodonContentWarningInputDefaults {
    val ContentPadding = 8.dp
    val IconSpacing = 8.dp
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ComposeReply(
    composeViewModel: ComposeViewModel,
    scaffoldState: BottomSheetScaffoldState,
) {
    val account = LocalActiveAccount.current ?: return
    if (account.type != PlatformType.Twitter) {
        Box(modifier = Modifier.height(16.dp))
        return
    }
    val composeType = composeViewModel.composeType
    if (composeType != ComposeType.Reply) {
        Box(modifier = Modifier.height(16.dp))
        return
    }
    val viewModelStatus by composeViewModel.status.observeAsState(initial = null)
    viewModelStatus?.let { status ->
        val replyToUser by composeViewModel.replyToUser.observeAsState(initial = emptyList())
        val excludedUserIds by composeViewModel.excludedReplyUserIds.observeAsState(initial = emptyList())
        val loadingReplyUser by composeViewModel.loadingReplyUser.observeAsState(initial = false)
        val scope = rememberCoroutineScope()
        Row(
            modifier = Modifier
                .clickable(
                    onClick = {
                        scope.launch {
                            if (scaffoldState.bottomSheetState.isExpanded) {
                                scaffoldState.bottomSheetState.collapse()
                            } else {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }
                    }
                )
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
        ) {
            Box(
                modifier = Modifier
                    .width(UserAvatarDefaults.AvatarSize)
                    .height(UserAvatarDefaults.AvatarSize)
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

@Composable
private fun ComposeVote(voteState: VoteState) {
    val options by voteState.options.observeAsState(initial = emptyList())
    val multiple by voteState.multiple.observeAsState(initial = false)
    val expired by voteState.expired.observeAsState(initial = VoteExpired.Day_1)
    Column {
        options.forEachIndexed { index, option ->
            val text by option.text.observeAsState(initial = "")
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = text,
                onValueChange = { voteState.setOption(it, index) }
            )
        }
        Spacer(modifier = Modifier.height(ComposeVoteDefaults.VoteOptionSpacing))
        Box {
            var showDropdown by remember {
                mutableStateOf(false)
            }
            DropdownMenu(expanded = showDropdown, onDismissRequest = { showDropdown = false }) {
                VoteExpired.values().forEach {
                    DropdownMenuItem(
                        onClick = {
                            voteState.setExpired(it)
                            showDropdown = false
                        }
                    ) {
                        Text(text = it.stringName())
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showDropdown = !showDropdown
                    }
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f),
                    text = expired.stringName(),
                )
                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
            }
        }
        Spacer(modifier = Modifier.height(ComposeVoteDefaults.MultipleCheckBoxSpacing))
        CheckboxItem(
            modifier = Modifier.fillMaxWidth(),
            checked = multiple,
            onCheckedChange = { voteState.setMultiple(!multiple) },
            text = stringResource(res = com.twidere.twiderex.MR.strings.scene_compose_vote_multiple)
        )
    }
}

private object ComposeVoteDefaults {
    val VoteOptionSpacing = 8.dp
    val MultipleCheckBoxSpacing = 8.dp
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ComposeActions(
    viewModel: ComposeViewModel,
    showEmoji: Boolean = false,
    emojiButtonClicked: () -> Unit = {},
) {
    val account = LocalActiveAccount.current ?: return
    val images by viewModel.images.observeAsState(initial = emptyList())
    val isInVoteState by viewModel.isInVoteMode.observeAsState(initial = false)
    val enableThreadMode by viewModel.enableThreadMode.observeAsState(initial = false)
    val allowImage by derivedStateOf {
        account.type == PlatformType.Twitter || (account.type == PlatformType.Mastodon && !isInVoteState)
    }
    val allowVote by derivedStateOf {
        account.type == PlatformType.Mastodon && !images.any()
    }
    val locationEnabled by viewModel.locationEnabled.observeAsState(initial = false)

    val allowLocation by derivedStateOf {
        account.type != PlatformType.Mastodon && currentPlatform == Platform.Android
    }
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current

    val draftCount = viewModel.draftCount.observeAsState(0)
    val insertMode by viewModel.mediaInsertMode.observeAsState(initial = ComposeViewModel.MediaInsertMode.All)
    Box {
        Row {
            AnimatedVisibility(visible = allowImage) {
                MediaInsertMenu(
                    onResult = {
                        viewModel.putImages(it)
                    },
                    supportMultipleSelect = insertMode.multiSelect,
                    disableList = insertMode.disabledInsertType,
                    librariesSupported = insertMode.librarySupportedType.toTypedArray()
                )
            }
            if (account.type == PlatformType.Mastodon) {
                IconButton(
                    onClick = {
                        emojiButtonClicked.invoke()
                    }
                ) {
                    Icon(
                        painter = painterResource(res = if (showEmoji) com.twidere.twiderex.MR.files.ic_keyboard else com.twidere.twiderex.MR.files.ic_mood_smile),
                        contentDescription = null,
                        tint = if (showEmoji)
                            MaterialTheme.colors.primary
                        else
                            LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
                    )
                }
            }
            AnimatedVisibility(visible = allowVote) {
                IconButton(
                    onClick = {
                        viewModel.setInVoteMode(!isInVoteState)
                    }
                ) {
                    Icon(
                        painter = painterResource(res = com.twidere.twiderex.MR.files.ic_poll),
                        contentDescription = null,
                        tint = if (isInVoteState) {
                            MaterialTheme.colors.primary
                        } else {
                            LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
                        }
                    )
                }
            }
            if (account.type == PlatformType.Mastodon || account.type == PlatformType.Twitter) {
                IconButton(
                    onClick = {
                        scope.launch {
                            val result =
                                navController.navigateForResult(Root.Compose.Search.User)
                                    ?.toString()
                            if (!result.isNullOrEmpty()) {
                                viewModel.insertText("$result ")
                            }
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(res = com.twidere.twiderex.MR.files.ic_at_sign),
                        contentDescription = stringResource(
                            res = com.twidere.twiderex.MR.strings.accessibility_scene_compose_add_mention,
                        )
                    )
                }
            }
            if (account.type == PlatformType.Mastodon) {
                IconButton(
                    onClick = {
                        scope.launch {
                            val result =
                                navController.navigateForResult(Root.Mastodon.Compose.Hashtag)
                                    ?.toString()
                            if (!result.isNullOrEmpty()) {
                                viewModel.insertText("$result ")
                            }
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(res = com.twidere.twiderex.MR.files.ic_hash),
                        contentDescription = null
                    )
                }
            }
            if (allowLocation) {
                RequestLocationPermission(
                    onPermissionGrantt = {
                        viewModel.trackingLocation()
                    },
                ) { launchRequest ->
                    IconButton(
                        onClick = {
                            if (locationEnabled) {
                                viewModel.disableLocation()
                            } else {
                                launchRequest.invoke()
                            }
                        },
                    ) {
                        Icon(
                            painter = painterResource(res = com.twidere.twiderex.MR.files.ic_map_pin),
                            contentDescription = stringResource(
                                res = if (locationEnabled) {
                                    com.twidere.twiderex.MR.strings.accessibility_scene_compose_location_disable
                                } else {
                                    com.twidere.twiderex.MR.strings.accessibility_scene_compose_location_enable
                                }
                            )
                        )
                    }
                }
            }
            IconButton(
                onClick = {
                    viewModel.setEnableThreadMode(!enableThreadMode)
                },
            ) {
                Icon(
                    painter = painterResource(res = com.twidere.twiderex.MR.files.ic_thread_mode),
                    contentDescription = stringResource(res = com.twidere.twiderex.MR.strings.accessibility_scene_compose_thread),
                    tint = if (enableThreadMode) MaterialTheme.colors.primary else LocalContentColor.current.copy(
                        alpha = LocalContentAlpha.current
                    )
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            if (draftCount.value > 0) {
                IconButton(
                    onClick = {
                        navController.navigate(Root.Draft.List)
                    }
                ) {
                    Box {
                        Icon(
                            painter = painterResource(
                                res = if (draftCount.value > 9)
                                    com.twidere.twiderex.MR.files.ic_drafts_more
                                else
                                    com.twidere.twiderex.MR.files.ic_draft_number
                            ),
                            contentDescription = stringResource(
                                res = com.twidere.twiderex.MR.strings.accessibility_scene_compose_draft
                            )
                        )
                        if (draftCount.value < 9) {
                            Text(
                                text = draftCount.value.toString(),
                                style = MaterialTheme.typography.overline.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(ComposeActionsDefaults.Draft.CountPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}

private object ComposeActionsDefaults {
    object Draft {
        val CountPadding = PaddingValues(end = 1.dp, bottom = 1.dp)
    }
}

@Composable
private fun ComposeImage(item: UiMediaInsert, viewModel: ComposeViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val type = item.type
    val navController = LocalNavController.current
    Box {
        Box(
            modifier = Modifier
                .heightIn(max = ComposeImageDefaults.ImageSize)
                .aspectRatio(1F)
                .border(
                    1.dp,
                    MaterialTheme.colors.onBackground.copy(alpha = 0.33f),
                    shape = MaterialTheme.shapes.small,
                )
                .clickable(
                    onClick = {
                        navController.navigate(
                            Root.Media.Raw(
                                if (type == MediaType.video) MediaType.video else MediaType.photo,
                                item.filePath.toString()
                            )
                        )
                    }
                )
                .clip(MaterialTheme.shapes.small),
        ) {
            NetworkImage(data = item.preview)
            when (type) {
                MediaType.animated_gif ->
                    GifTag(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .width(ComposeImageDefaults.Tag.Width)
                            .height(ComposeImageDefaults.Tag.Height)
                            .padding(ComposeImageDefaults.Tag.Padding),
                        resize = true
                    )
                MediaType.video -> Icon(
                    imageVector = Icons.Default.PlayArrow,
                    tint = Color.White.copy(alpha = LocalContentAlpha.current),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(ComposeImageDefaults.VideoIconSize)
                        .background(MaterialTheme.colors.primary, CircleShape),
                    contentDescription = type.name
                )
                else -> {}
            }
            Image(
                painter = painterResource(res = MR.files.ic_dots_circle_horiz),
                contentDescription = type.name,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(ComposeImageDefaults.Menu.Size)
                    .padding(ComposeImageDefaults.Menu.Padding)
                    .clickable { expanded = !expanded }
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    viewModel.removeImage(item.filePath)
                }
            ) {
                Text(
                    text = stringResource(res = com.twidere.twiderex.MR.strings.common_controls_actions_remove),
                    color = Color.Red,
                )
            }
        }
    }
}

private object ComposeImageDefaults {
    val ImageSize = 72.dp

    object Tag {
        val Width = 24.dp
        val Height = 18.dp
        val Padding = PaddingValues(start = 6.dp, bottom = 6.dp)
    }
    object Menu {
        val Size = 24.dp
        val Padding = PaddingValues(end = 6.dp, bottom = 3.dp)
    }
    val VideoIconSize = 30.dp
}

@Composable
fun VoteExpired.stringName(): String {
    return when (this) {
        VoteExpired.Min_5 -> stringResource(res = com.twidere.twiderex.MR.strings.scene_compose_vote_expiration_5_Min)
        VoteExpired.Min_30 -> stringResource(res = com.twidere.twiderex.MR.strings.scene_compose_vote_expiration_30_Min)
        VoteExpired.Hour_1 -> stringResource(res = com.twidere.twiderex.MR.strings.scene_compose_vote_expiration_1_Hour)
        VoteExpired.Hour_6 -> stringResource(res = com.twidere.twiderex.MR.strings.scene_compose_vote_expiration_6_Hour)
        VoteExpired.Day_1 -> stringResource(res = com.twidere.twiderex.MR.strings.scene_compose_vote_expiration_1_Day)
        VoteExpired.Day_3 -> stringResource(res = com.twidere.twiderex.MR.strings.scene_compose_vote_expiration_3_Day)
        VoteExpired.Day_7 -> stringResource(res = com.twidere.twiderex.MR.strings.scene_compose_vote_expiration_7_Day)
    }
}
