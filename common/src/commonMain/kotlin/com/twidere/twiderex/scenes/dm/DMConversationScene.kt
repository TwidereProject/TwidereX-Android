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
package com.twidere.twiderex.scenes.dm

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.Dialog
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.NetworkImage
import com.twidere.twiderex.component.foundation.TextInput
import com.twidere.twiderex.component.lazy.ui.LazyUiDMEventList
import com.twidere.twiderex.component.media.MediaInsertMenu
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.di.ext.getViewModel
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiDMEvent
import com.twidere.twiderex.model.ui.UiMediaInsert
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.dm.DMEventViewModel
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf

@Composable
fun DMConversationScene(conversationKey: MicroBlogKey) {
    val account = LocalActiveAccount.current ?: return
    val viewModel: DMEventViewModel = getViewModel {
        parametersOf(conversationKey)
    }
    val conversation by viewModel.conversation.observeAsState(null)
    TwidereScene {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        conversation?.let {
                            Text(text = it.conversationName)
                        }
                    },
                )
            },
        ) {
            if (!account.supportDirectMessage) {
                Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_messages_error_not_supported))
            } else {
                // user might enter this page by notifications after switch platform
                NormalContent(viewModel)
            }
        }
    }
}

@Composable
fun NormalContent(viewModel: DMEventViewModel) {
    val clipboardManager = LocalClipboardManager.current
    val source = viewModel.source.collectAsLazyPagingItems()
    val input by viewModel.input.observeAsState(initial = "")
    val inputImage by viewModel.inputImage.observeAsState(null)
    val listState = rememberLazyListState()
    val firstEventKey by viewModel.firstEventKey.observeAsState(null)
    val pendingActionMessage by viewModel.pendingActionMessage.observeAsState(null)
    val scope = rememberCoroutineScope()
    if (source.itemCount > 0) {
        source.peek(0)?.messageKey?.let {
            viewModel.firstEventKey.value = it.toString()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(modifier = Modifier.weight(1f)) {
            LazyUiDMEventList(
                modifier = Modifier.fillMaxSize(),
                items = source,
                onResend = {
                    viewModel.sendDraftMessage(it)
                },
                state = listState,
                onItemLongClick = {
                    viewModel.pendingActionMessage.value = it
                }
            )
            MessageActionComponent(
                pendingActionMessage = pendingActionMessage,
                onDismissRequest = { viewModel.pendingActionMessage.value = null },
                onCopyText = { event ->
                    clipboardManager.setText(annotatedString = buildAnnotatedString { append(event.originText) })
                },
                onDelete = {
                    viewModel.deleteMessage(it)
                }
            )
        }
        Divider(modifier = Modifier.fillMaxWidth())
        InputMediaPreview(inputImage) {
            viewModel.inputImage.value = null
        }
        InputComponent(
            modifier = Modifier.fillMaxWidth(),
            onMediaInsert = {
                viewModel.inputImage.value = it.firstOrNull()
            },
            enableSelectPhoto = inputImage == null,
            enableSend = input.isNotEmpty() || inputImage != null,
            input = input,
            onValueChanged = { viewModel.input.value = it },
            onSend = { viewModel.sendMessage() },
        )
    }
    firstEventKey?.let {
        LaunchedEffect(
            key1 = firstEventKey,
            block = {
                if (source.itemCount <= 0) return@LaunchedEffect
                scope.launch {
                    try {
                        listState.scrollToItem(0)
                    } catch (e: Throwable) {
                        // when viewModel is restored from cache while List hasn't init yet
                        // might cause crash
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MessageActionComponent(
    pendingActionMessage: UiDMEvent?,
    onDismissRequest: () -> Unit,
    onCopyText: (message: UiDMEvent) -> Unit,
    onDelete: (message: UiDMEvent) -> Unit
) {
    pendingActionMessage?.let {
        Dialog(onDismissRequest = onDismissRequest) {
            Surface(shape = MaterialTheme.shapes.medium) {
                Column(modifier = Modifier.padding(MessageActionComponentDefaults.ContentPadding)) {
                    ListItem(
                        modifier = Modifier.clickable {
                            onCopyText(it)
                            onDismissRequest()
                        }
                    ) {
                        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_messages_action_copy_text))
                    }
                    ListItem(
                        modifier = Modifier.clickable {
                            onDelete(it)
                            onDismissRequest()
                        }
                    ) {
                        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_messages_action_delete))
                    }
                }
            }
        }
    }
}

private object MessageActionComponentDefaults {
    val ContentPadding = PaddingValues(16.dp)
}

@Composable
fun InputMediaPreview(inputImage: UiMediaInsert?, onRemove: () -> Unit) {
    if (inputImage == null) return
    Box(modifier = Modifier.padding(InputPhotoPreviewDefaults.ContentPadding)) {
        Box(
            modifier = Modifier
                .heightIn(max = InputPhotoPreviewDefaults.ImageSize)
                .widthIn(max = InputPhotoPreviewDefaults.ImageSize)
                .border(
                    1.dp,
                    MaterialTheme.colors.onBackground.copy(alpha = 0.33f),
                    shape = MaterialTheme.shapes.small,
                )
                .clip(MaterialTheme.shapes.small),
        ) {
            NetworkImage(data = inputImage.preview)
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .background(
                    MaterialTheme.colors.onSurface.copy(0.8f),
                    shape = CircleShape,
                )
                .clickable { onRemove() }
        ) {
            Icon(
                painter = painterResource(res = com.twidere.twiderex.MR.files.ic_x),
                contentDescription = stringResource(res = com.twidere.twiderex.MR.strings.common_controls_actions_remove),
                tint = MaterialTheme.colors.surface,
                modifier = Modifier.padding(InputPhotoPreviewDefaults.IconPadding)
            )
        }
    }
}

private object InputPhotoPreviewDefaults {
    val ContentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
    val ImageSize = 172.dp
    val IconPadding = 6.dp
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun InputComponent(
    modifier: Modifier = Modifier,
    onMediaInsert: (List<UiMediaInsert>) -> Unit,
    input: String,
    onValueChanged: (input: String) -> Unit,
    enableSelectPhoto: Boolean,
    enableSend: Boolean,
    onSend: () -> Unit,
) {
    Row(
        modifier = modifier.padding(InputComponentDefaults.ContentPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(visible = enableSelectPhoto) {
            MediaInsertMenu(
                onResult = {
                    onMediaInsert(it)
                },
                supportMultipleSelect = false,
            )
        }
        Spacer(modifier = Modifier.width(InputComponentDefaults.ContentSpacing))
        TextInput(
            value = input,
            onValueChange = onValueChanged,
            modifier = Modifier.weight(1f),
            maxLines = 3,
        )
        Spacer(modifier = Modifier.width(InputComponentDefaults.ContentSpacing))
        IconButton(
            enabled = enableSend,
            onClick = onSend
        ) {
            Icon(
                painter = painterResource(res = com.twidere.twiderex.MR.files.ic_send),
                contentDescription = stringResource(
                    res = com.twidere.twiderex.MR.strings.accessibility_scene_compose_send
                ),
                tint = if (enableSend) MaterialTheme.colors.primary else LocalContentColor.current.copy(
                    alpha = LocalContentAlpha.current
                )
            )
        }
    }
}

private object InputComponentDefaults {
    val ContentPadding = PaddingValues(horizontal = 4.dp)
    val ContentSpacing = 10.dp
}
