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
package com.twidere.twiderex.scenes.settings.swipe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.foundation.AlertDialog
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.settings.RadioItem
import com.twidere.twiderex.component.settings.switchItem
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.dataprovider.mapper.Strings
import com.twidere.twiderex.extensions.rememberPresenterState
import com.twidere.twiderex.navigation.Root
import com.twidere.twiderex.preferences.model.SwipeActionType
import com.twidere.twiderex.preferences.model.tittle
import com.twidere.twiderex.preferences.model.toUi
import com.twidere.twiderex.ui.TwidereScene
import io.github.seiko.precompose.annotation.NavGraphDestination
import moe.tlaster.precompose.navigation.Navigator

@NavGraphDestination(
  route = Root.Settings.Swipe,
)
@Composable
fun SwipeScene(
  navigator: Navigator,
) {
  TwidereScene {
    InAppNotificationScaffold(
      topBar = {
        AppBar(
          title = {
            Text(
              text = stringResource(res = Strings.scene_settings_swipe_gestures_tittle)
            )
          },
          navigationIcon = {
            AppBarNavigationButton {
              navigator.popBackStack()
            }
          },
        )
      },
    ) {
      val (state, channel) = rememberPresenterState {
        SwipePresenter(it)
      }
      Column {
        switchItem(
          value = state.swipePreferences.useSwipeGestures,
          onChanged = {
            channel.trySend(SwipeEvent.SetUseSwipe(value = it))
          },
          describe = {
            Text(
              text = stringResource(Strings.scene_settings_swipe_gestures_desc_content)
            )
          },
        ) {
          Text(
            text = stringResource(Strings.scene_settings_swipe_gestures_desc_title)
          )
        }
        state.swipePreferences.leftShort.let {
          val ui = it.action.toUi()
          SwipeGestureItem(
            currentAction = it.action,
            leftIcon = {
              ActionIcon(
                icon = rememberVectorPainter(
                  image = Icons.TwoTone.ArrowForward
                ),
              )
            },
            tittle = {
              Text(text = it.tittle())
            },
            subTittle = {
              Text(text = ui.tittle)
            },
            rightIcon = {
              ActionIcon(
                icon = ui.icon?.let { painterResource(it) },
                isLargeIcon = true,
                hasPadding = true,
                backgroundColor = MaterialTheme.colors.surface,
                iconColor = MaterialTheme.colors.primary,
              )
            }
          ) { action ->
            channel.trySend(SwipeEvent.SetLeftShortSwipeAction(action))
          }
        }

        state.swipePreferences.leftLong.let {
          val ui = it.action.toUi()
          SwipeGestureItem(
            backgroundColor = MaterialTheme.colors.onBackground.copy(alpha = 0.6f),
            currentAction = it.action,
            leftIcon = {
              ActionIcon(
                icon = rememberVectorPainter(
                  image = Icons.TwoTone.ArrowForward
                ),
                isLargeIcon = true,
              )
            },
            tittle = {
              Text(text = it.tittle())
            },
            subTittle = {
              Text(text = ui.tittle)
            },
            rightIcon = {
              ActionIcon(
                icon = ui.icon?.let { painterResource(res = it) },
                hasPadding = true,
                backgroundColor = MaterialTheme.colors.surface,
                iconColor = MaterialTheme.colors.onBackground.copy(alpha = 0.6f),
              )
            }
          ) { action ->
            channel.trySend(SwipeEvent.SetLeftLongSwipeAction(action))
          }
        }

        state.swipePreferences.rightShort.let {
          val ui = it.action.toUi()
          SwipeGestureItem(
            currentAction = it.action,
            leftIcon = {
              ActionIcon(
                icon = ui.icon?.let { painterResource(res = it) },
                isLargeIcon = true,
                hasPadding = true,
                backgroundColor = MaterialTheme.colors.surface,
                iconColor = MaterialTheme.colors.primary,
              )
            },
            tittle = {
              Text(text = it.tittle())
            },
            subTittle = {
              Text(text = ui.tittle)
            },
            rightIcon = {
              ActionIcon(
                icon = rememberVectorPainter(
                  image = Icons.TwoTone.ArrowBack
                ),
              )
            }
          ) { action ->
            channel.trySend(SwipeEvent.SetRightShortSwipeAction(action))
          }
        }

        state.swipePreferences.rightLong.let {
          val ui = it.action.toUi()
          SwipeGestureItem(
            backgroundColor = MaterialTheme.colors.onBackground.copy(alpha = 0.6f),
            currentAction = it.action,
            leftIcon = {
              ActionIcon(
                icon = ui.icon?.let { painterResource(res = it) },
                hasPadding = true,
                backgroundColor = MaterialTheme.colors.surface,
                iconColor = MaterialTheme.colors.onBackground.copy(alpha = 0.6f),
              )
            },
            tittle = {
              Text(text = it.tittle())
            },
            subTittle = {
              Text(text = ui.tittle)
            },
            rightIcon = {
              ActionIcon(
                icon = rememberVectorPainter(
                  image = Icons.TwoTone.ArrowBack,
                ),
                isLargeIcon = true,
              )
            }
          ) { action ->
            channel.trySend(SwipeEvent.SetRightLongSwipeAction(action))
          }
        }
      }
    }
  }
}

@Composable
private fun RowScope.ActionIcon(
  icon: Painter?,
  isLargeIcon: Boolean = false,
  hasPadding: Boolean = false,
  backgroundColor: Color = Color.Transparent,
  iconColor: Color = Color.White,
) {
  Box(
    modifier = Modifier
      .width(if (isLargeIcon) 54.dp else 32.dp)
      .height(if (hasPadding) 34.dp else 36.dp)
      .background(backgroundColor)
      .align(Alignment.CenterVertically),
  ) {
    icon?.let {
      Icon(
        painter = it,
        contentDescription = null,
        modifier = Modifier.height(
          16.dp
        ).wrapContentWidth()
          .align(Alignment.Center),
        tint = iconColor,
      )
    }
  }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SwipeGestureItem(
  backgroundColor: Color = MaterialTheme.colors.primary,
  leftIcon: @Composable RowScope.() -> Unit,
  rightIcon: @Composable RowScope.() -> Unit,
  tittle: @Composable () -> Unit,
  subTittle: @Composable () -> Unit,
  currentAction: SwipeActionType = SwipeActionType.None,
  onActionSelect: (SwipeActionType) -> Unit,
) {
  var show by remember {
    mutableStateOf(false)
  }
  ListItem(
    modifier = Modifier.padding(
      vertical = 18.dp,
      horizontal = 16.dp
    ).clickable {
      show = !show
    },
    icon = {
      Row(
        modifier = Modifier
          .width(86.dp)
          .height(36.dp)
          .background(
            backgroundColor
          )
      ) {
        leftIcon()
        rightIcon()
      }
    },
    secondaryText = {
      subTittle()
    }
  ) {
    tittle()
  }
  if (show) {
    ActionSelectMenu(
      currentAction = currentAction,
      onActionSelect = {
        show = false
        onActionSelect(it)
      },
    ) {
      show = false
    }
  }
}

@Composable
private fun ActionSelectMenu(
  currentAction: SwipeActionType = SwipeActionType.None,
  onActionSelect: (SwipeActionType) -> Unit,
  onDismissRequest: () -> Unit,
) {
  AlertDialog(
    text = {
      Column {
        RadioItem(
          options = SwipeActionType.values().toList(),
          value = currentAction,
          itemContent = {
            Text(text = it.toUi().tittle)
          },
          onChanged = {
            onActionSelect(it)
          },
          title = {}
        )
      }
    },
    onDismissRequest = {
      onDismissRequest()
    },
    confirmButton = {}
  )
}
