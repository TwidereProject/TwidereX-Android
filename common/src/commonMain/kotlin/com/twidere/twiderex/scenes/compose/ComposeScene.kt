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
import com.twidere.twiderex.extensions.icon
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.extensions.rememberPresenterState
import com.twidere.twiderex.extensions.stringName
import com.twidere.twiderex.extensions.withElevation
import com.twidere.twiderex.kmp.RequestLocationPermission
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.ComposeType
import com.twidere.twiderex.model.enums.MastodonVisibility
import com.twidere.twiderex.model.enums.MediaType
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.ui.UiMediaInsert
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.navigation.Root
import com.twidere.twiderex.navigation.RootDeepLinks
import com.twidere.twiderex.navigation.StatusNavigationData
import com.twidere.twiderex.navigation.rememberStatusNavigationData
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.Orange
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.compose.ComposeEvent
import com.twidere.twiderex.viewmodel.compose.ComposePresenter
import com.twidere.twiderex.viewmodel.compose.ComposeState
import com.twidere.twiderex.viewmodel.compose.VoteExpired
import com.twidere.twiderex.viewmodel.compose.VoteState
import io.github.seiko.precompose.annotation.NavGraphDestination
import io.github.seiko.precompose.annotation.Path
import io.github.seiko.precompose.annotation.Query
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.BackHandler
import moe.tlaster.precompose.navigation.Navigator

@NavGraphDestination(
  route = Root.Draft.Compose.route,
  deepLink = [RootDeepLinks.Draft.route]
)
@Composable
fun DraftComposeScene(
  @Path("draftId") draftId: String,
  navigator: Navigator,
) {
  val statusNavigation = rememberStatusNavigationData(navigator)
  ComposeBody(
    statusNavigation = statusNavigation,
    draftId = draftId,
  )
}

@NavGraphDestination(
  route = Root.Compose.Home.route,
  deepLink = [RootDeepLinks.Compose.route]
)
@Composable
fun ComposeScene(
  @Query("statusKey") statusKey: String?,
  @Query("composeType") composeType: String?,
  navigator: Navigator,
) {
  val key = statusKey?.let {
    MicroBlogKey.valueOf(it)
  }
  val type = composeType?.let { enumValueOf(it) } ?: ComposeType.New
  val statusNavigation = rememberStatusNavigationData(navigator)
  ComposeBody(
    statusNavigation = statusNavigation,
    statusKey = key,
    composeType = type,
  )
}

@OptIn(
  ExperimentalFoundationApi::class,
  ExperimentalMaterialApi::class,
  ExperimentalComposeUiApi::class,
)
@Composable
private fun ComposeBody(
  statusNavigation: StatusNavigationData,
  draftId: String? = null,
  statusKey: MicroBlogKey? = null,
  composeType: ComposeType? = null,
) {
  val (state, channel) = rememberPresenterState<ComposeState, ComposeEvent> {
    ComposePresenter(
      it,
      id = draftId,
      type = composeType,
      statusKey = statusKey,
    )
  }
  if (
    state !is ComposeState.Data ||
    state.composeType == null
  ) {
    return
  }
  val keyboardController = LocalSoftwareKeyboardController.current
  var showSaveDraftDialog by remember { mutableStateOf(false) }
  val scaffoldState = rememberBottomSheetScaffoldState()
  if (showSaveDraftDialog || state.canSaveDraft) {
    BackHandler {
      when {
        showSaveDraftDialog -> {
          showSaveDraftDialog = false
        }
        state.canSaveDraft -> {
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
          channel.trySend(ComposeEvent.SaveDraft)
          statusNavigation.popBackStack()
        },
        onCancel = {
          statusNavigation.popBackStack()
        }
      )
    }
    InAppNotificationBottomSheetScaffold(
      sheetPeekHeight = 0.dp,
      scaffoldState = scaffoldState,
      sheetContent = {
        ReplySheetContent(
          scaffoldState = scaffoldState,
          statusNavigation = statusNavigation,
          composeType = state.composeType,
          status = state.status,
          replyToUser = state.replyToUser,
          contains = {
            state.excludedReplyUserIds.contains(it)
          },
          includeReplyUser = {
            channel.trySend(ComposeEvent.IncludeReplyUser(it))
          },
          excludeReplyUser = {
            channel.trySend(ComposeEvent.ExcludeReplyUser(it))
          }
        )
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
                if (state.canSaveDraft) {
                  showSaveDraftDialog = true
                } else {
                  statusNavigation.popBackStack()
                }
              }
            ) {
              Icon(
                painter = painterResource(res = com.twidere.twiderex.MR.files.ic_x),
                contentDescription = stringResource(
                  res = com.twidere.twiderex.MR.strings.accessibility_common_close
                ),
                modifier = Modifier.size(24.dp),
              )
            }
          },
          actions = {
            IconButton(
              enabled = state.canSend,
              onClick = {
                channel.trySend(ComposeEvent.Compose)
                statusNavigation.popBackStack()
              }
            ) {
              Icon(
                painter = painterResource(
                  res = if (state.enableThreadMode) {
                    com.twidere.twiderex.MR.files.ic_send_thread
                  } else {
                    com.twidere.twiderex.MR.files.ic_send
                  }
                ),
                contentDescription = stringResource(
                  res = if (state.enableThreadMode) {
                    com.twidere.twiderex
                      .MR.strings.accessibility_scene_compose_thread
                  } else {
                    com.twidere.twiderex.MR.strings.accessibility_scene_compose_send
                  }
                ),
                tint = if (state.canSend) {
                  MaterialTheme.colors.primary
                } else LocalContentColor.current.copy(
                  alpha = LocalContentAlpha.current
                ),
                modifier = Modifier.size(24.dp),
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
              state.status?.let { status ->
                Box(
                  modifier = Modifier
                    .background(MaterialTheme.colors.surface.withElevation())
                ) {
                  StatusLineComponent(lineDown = true) {
                    TimelineStatusComponent(
                      data = status,
                      showActions = false,
                      statusNavigation = statusNavigation,
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
                scaffoldState = scaffoldState,
                state = state,
                autoFocus = if (composeType == ComposeType.Reply || composeType == ComposeType.Thread) {
                  scrollState.value == 0
                } else {
                  true
                },
                focusRequester = focusRequester,
                setText = {
                  channel.trySend(
                    ComposeEvent.SetText(it)
                  )
                },
                setContentWarningText = {
                  channel.trySend(
                    ComposeEvent.WarmingText(it)
                  )
                }
              )
            }
            if (composeType == ComposeType.Quote) {
              state.status?.let { status ->
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
                    statusNavigation = statusNavigation,
                  )
                }
              }
            }
          }
        }

        if (state.images.any()) {
          ComposeImageList(
            images = state.images,
            removeImage = {
              channel.trySend(ComposeEvent.RemoveImage(it))
            },
            navigate = statusNavigation.navigate,
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
        ) {
          TextProgress(state.parsedTextLength, state.maxLength)
          if (state.account.type == PlatformType.Mastodon) {
            ComposeMastodonVisibility(
              modifier = Modifier.weight(1f),
              visibility = state.visibility,
              setVisibility = {
                channel.trySend(ComposeEvent.ChangeVisibility(it))
              }
            )
            CompositionLocalProvider(LocalContentAlpha.provides(ContentAlpha.medium)) {
              MastodonExtraActions(
                images = state.images.map { it.filePath },
                isContentWarning = state.isContentWarning,
                isImageSensitive = state.isImageSensitive,
                setImageSensitive = {
                  channel.trySend(ComposeEvent.EnableSensitive)
                },
                setContentWarningEnabled = {
                  channel.trySend(ComposeEvent.EnableWarning)
                },
              )
            }
          } else {
            Spacer(modifier = Modifier.weight(1F))
          }
          if (state.locationEnabled) {
            state.location?.let {
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
            showEmoji = showEmoji,
            navigateForResult = statusNavigation.navigateForResult,
            navigate = statusNavigation.navigate,
            emojiButtonClicked = {
              showEmoji = !showEmoji
            },
            state = state,
            channel = channel,
          )
        }
        PlatformEmojiPanel(
          showEmoji = showEmoji,
          items = state.emojis,
          onEmojiSelected = {
            channel.trySend(
              ComposeEvent.InsertEmoji(it)
            )
          },
        )
      }
    }
  }
}

@Composable
private fun ComposeImageList(
  images: List<UiMediaInsert>,
  removeImage: (String) -> Unit,
  navigate: (String) -> Unit,
) {
  Spacer(modifier = Modifier.height(ComposeImageListDefaults.Spacing))
  LazyRow(
    modifier = Modifier.padding(ComposeImageListDefaults.ContentPadding),
  ) {
    itemsIndexed(
      items = images,
    ) { index, item ->
      ComposeImage(
        item,
        removeImage,
        navigate,
      )
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
  isContentWarning: Boolean,
  isImageSensitive: Boolean,
  setImageSensitive: () -> Unit,
  setContentWarningEnabled: () -> Unit,
) {
  if (images.any()) {
    IconButton(
      onClick = {
        setImageSensitive.invoke()
      }
    ) {
      Icon(
        painter = painterResource(res = com.twidere.twiderex.MR.files.ic_eye_off),
        contentDescription = null,
        tint = if (isImageSensitive) {
          MaterialTheme.colors.primary
        } else {
          LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
        },
        modifier = Modifier.size(24.dp),
      )
    }
  }
  IconButton(
    onClick = {
      setContentWarningEnabled.invoke()
    }
  ) {
    Icon(
      painter = painterResource(res = com.twidere.twiderex.MR.files.ic_alert_octagon),
      contentDescription = null,
      tint = if (isContentWarning) {
        MaterialTheme.colors.primary
      } else {
        LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
      },
      modifier = Modifier.size(24.dp),
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
        ),
        modifier = Modifier.size(24.dp),
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
private fun TextProgress(parsedTextLength: Int, maxLength: Int) {
  val progress = remember(parsedTextLength) {
    parsedTextLength.toFloat() / maxLength.toFloat()
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
    Text(text = (maxLength - parsedTextLength).toString(), color = Color.Red)
  }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ComposeMastodonVisibility(
  modifier: Modifier = Modifier,
  visibility: MastodonVisibility,
  setVisibility: (MastodonVisibility) -> Unit,
) {
  var showDropdown by remember {
    mutableStateOf(false)
  }
  Box(
    modifier = modifier
  ) {
    DropdownMenu(expanded = showDropdown, onDismissRequest = { showDropdown = false }) {
      MastodonVisibility.values().forEach {
        DropdownMenuItem(
          onClick = {
            showDropdown = false
            setVisibility(it)
          }
        ) {
          ListItem(
            text = {
              Text(text = it.stringName())
            },
            icon = {
              Icon(
                painter = it.icon(),
                contentDescription = it.stringName(),
                modifier = Modifier.size(24.dp),
              )
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
        Icon(
          painter = visibility.icon(),
          contentDescription = visibility.stringName(),
          modifier = Modifier.size(24.dp),
        )
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
  scaffoldState: BottomSheetScaffoldState,
  statusNavigation: StatusNavigationData,
  composeType: ComposeType,
  status: UiStatus?,
  replyToUser: List<UiUser>,
  contains: (String) -> Boolean,
  includeReplyUser: (UiUser) -> Unit,
  excludeReplyUser: (UiUser) -> Unit,

) {
  if (composeType != ComposeType.Reply) {
    return
  }
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
          ),
          modifier = Modifier.size(24.dp),
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
          onClick = statusNavigation.toUser,
        )
      },
      text = {
        UserName(
          user = it.user,
          onUserNameClicked = statusNavigation.openLink,
        )
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
      val excluded = contains(user.id)
      ListItem(
        modifier = Modifier.clickable(
          onClick = {
            if (excluded) {
              includeReplyUser(user)
            } else {
              excludeReplyUser(user)
            }
          }
        ),
        icon = {
          UserAvatar(
            user = user,
            onClick = statusNavigation.toUser,
          )
        },
        text = {
          UserName(
            user = user,
            onUserNameClicked = statusNavigation.openLink,
          )
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
                  includeReplyUser(user)
                } else {
                  excludeReplyUser(user)
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

@OptIn(ExperimentalAnimationApi::class)
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
private fun ComposeInput(
  scaffoldState: BottomSheetScaffoldState,
  state: ComposeState.Data,
  autoFocus: Boolean = true,
  focusRequester: FocusRequester,
  setText: (TextFieldValue) -> Unit,
  setContentWarningText: (TextFieldValue) -> Unit,
) {
  Column {
    ComposeReply(
      scaffoldState = scaffoldState,
      state = state,
    )
    Row(
      modifier = Modifier
        .padding(start = 16.dp, bottom = 16.dp, end = 16.dp),
    ) {
      NetworkImage(
        data = state.account.user.profileImage,
        modifier = Modifier
          .clip(CircleShape)
          .width(UserAvatarDefaults.AvatarSize)
          .height(UserAvatarDefaults.AvatarSize)
      )
      Spacer(modifier = Modifier.width(16.dp))
      Column(
        modifier = Modifier.weight(1F)
      ) {
        if (state.account.type == PlatformType.Mastodon) {
          MastodonContentWarningInput(
            isContentWarningEnabled = state.isContentWarningEnabled,
            cwText = state.contentWarningTextFieldValue,
            setContentWarningText = setContentWarningText,
          )
        }
        TextInput(
          modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
          value = state.textFieldValue,
          onValueChange = {
            setText(it)
          },
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
        state.voteState?.let {
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
private fun ColumnScope.MastodonContentWarningInput(
  isContentWarningEnabled: Boolean,
  cwText: TextFieldValue,
  setContentWarningText: (TextFieldValue) -> Unit,
) {
  AnimatedVisibility(visible = isContentWarningEnabled) {
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
            modifier = Modifier.size(24.dp),
          )
        }
        Spacer(modifier = Modifier.width(MastodonContentWarningInputDefaults.IconSpacing))
        TextInput(
          value = cwText,
          onValueChange = {
            setContentWarningText(it)
          },
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
  scaffoldState: BottomSheetScaffoldState,
  state: ComposeState.Data,
) {
  val account = LocalActiveAccount.current ?: return
  if (account.type != PlatformType.Twitter) {
    Box(modifier = Modifier.height(16.dp))
    return
  }
  val composeType = state.composeType
  if (composeType != ComposeType.Reply) {
    Box(modifier = Modifier.height(16.dp))
    return
  }
  state.status?.let { status ->
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
        if (state.loadingReplyUser) {
          LinearProgressIndicator()
        } else {
          Text(
            text = (listOf(status.user) + state.replyToUser).filter {
              !state.excludedReplyUserIds.contains(
                it.id
              )
            }.joinToString(",") { "@${it.screenName}" },
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

@Composable
private fun ComposeActions(
  showEmoji: Boolean = false,
  navigateForResult: suspend (String) -> Any?,
  navigate: (String) -> Unit,
  emojiButtonClicked: () -> Unit = {},
  state: ComposeState.Data,
  channel: Channel<ComposeEvent>,
) {
  val scope = rememberCoroutineScope()
  Box {
    Row {
      AnimatedVisibility(visible = state.allowImage) {
        MediaInsertMenu(
          onResult = {
            channel.trySend(ComposeEvent.PutImages(it))
          },
          supportMultipleSelect = state.mediaInsertMode.multiSelect,
          disableList = state.mediaInsertMode.disabledInsertType,
          librariesSupported = state.mediaInsertMode.librarySupportedType.toTypedArray(),
          navigateForResult = navigateForResult,
        )
      }
      if (state.account.type == PlatformType.Mastodon) {
        IconButton(
          onClick = {
            emojiButtonClicked.invoke()
          }
        ) {
          Icon(
            painter = painterResource(res = if (showEmoji) com.twidere.twiderex.MR.files.ic_keyboard else com.twidere.twiderex.MR.files.ic_mood_smile),
            contentDescription = null,
            tint = if (showEmoji) {
              MaterialTheme.colors.primary
            } else {
              LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
            },
            modifier = Modifier.size(24.dp),
          )
        }
      }
      AnimatedVisibility(visible = state.allowVote) {
        IconButton(
          onClick = {
            channel.trySend(ComposeEvent.VoteMode(!state.isInVoteMode))
          }
        ) {
          Icon(
            painter = painterResource(res = com.twidere.twiderex.MR.files.ic_poll),
            contentDescription = null,
            tint = if (state.isInVoteMode) {
              MaterialTheme.colors.primary
            } else {
              LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
            },
            modifier = Modifier.size(24.dp),
          )
        }
      }
      if (state.account.type == PlatformType.Mastodon || state.account.type == PlatformType.Twitter) {
        IconButton(
          onClick = {
            scope.launch {
              val result =
                navigateForResult(Root.Compose.Search.User)
                  ?.toString()
              if (!result.isNullOrEmpty()) {
                channel.trySend(
                  ComposeEvent.InsertText("$result ")
                )
              }
            }
          }
        ) {
          Icon(
            painter = painterResource(res = com.twidere.twiderex.MR.files.ic_at_sign),
            contentDescription = stringResource(
              res = com.twidere.twiderex.MR.strings.accessibility_scene_compose_add_mention,
            ),
            modifier = Modifier.size(24.dp),
          )
        }
      }
      if (state.account.type == PlatformType.Mastodon) {
        IconButton(
          onClick = {
            scope.launch {
              val result = navigateForResult(Root.Mastodon.Compose.Hashtag)
                ?.toString()
              if (!result.isNullOrEmpty()) {
                channel.trySend(
                  ComposeEvent.InsertText("$result ")
                )
              }
            }
          }
        ) {
          Icon(
            painter = painterResource(res = com.twidere.twiderex.MR.files.ic_hash),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
          )
        }
      }
      if (state.allowLocation) {
        RequestLocationPermission(
          onPermissionGrantt = {
            channel.trySend(
              ComposeEvent.TrackLocation
            )
          },
        ) { launchRequest ->
          IconButton(
            onClick = {
              if (state.locationEnabled) {
                channel.trySend(
                  ComposeEvent.DisableLocation
                )
              } else {
                launchRequest.invoke()
              }
            },
          ) {
            Icon(
              painter = painterResource(res = com.twidere.twiderex.MR.files.ic_map_pin),
              contentDescription = stringResource(
                res = if (state.locationEnabled) {
                  com.twidere.twiderex.MR.strings.accessibility_scene_compose_location_disable
                } else {
                  com.twidere.twiderex.MR.strings.accessibility_scene_compose_location_enable
                }
              ),
              modifier = Modifier.size(24.dp),
            )
          }
        }
      }
      IconButton(
        onClick = {
          channel.trySend(
            ComposeEvent.EnableThreadMode(!state.enableThreadMode)
          )
        },
      ) {
        Icon(
          painter = painterResource(res = com.twidere.twiderex.MR.files.ic_thread_mode),
          contentDescription = stringResource(res = com.twidere.twiderex.MR.strings.accessibility_scene_compose_thread),
          tint = if (state.enableThreadMode) MaterialTheme.colors.primary else LocalContentColor.current.copy(
            alpha = LocalContentAlpha.current
          ),
          modifier = Modifier.size(24.dp),
        )
      }
      Spacer(modifier = Modifier.weight(1f))
      if (state.draftCount > 0) {
        IconButton(
          onClick = {
            navigate(Root.Draft.List)
          }
        ) {
          Box {
            Icon(
              painter = painterResource(
                res = if (state.draftCount > 9) {
                  com.twidere.twiderex.MR.files.ic_drafts_more
                } else {
                  com.twidere.twiderex.MR.files.ic_draft_number
                }
              ),
              contentDescription = stringResource(
                res = com.twidere.twiderex.MR.strings.accessibility_scene_compose_draft
              ),
              modifier = Modifier.size(24.dp),
            )
            if (state.draftCount < 9) {
              Text(
                text = state.draftCount.toString(),
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
private fun ComposeImage(
  item: UiMediaInsert,
  removeImage: (String) -> Unit,
  navigate: (String) -> Unit,
) {
  var expanded by remember { mutableStateOf(false) }
  val type = item.type
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
            navigate(
              Root.Media.Raw(
                if (type == MediaType.video) MediaType.video else MediaType.photo,
                item.filePath
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
          removeImage.invoke(item.filePath)
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
