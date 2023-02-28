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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Icon
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
import com.twidere.twiderex.component.foundation.DropdownMenu
import com.twidere.twiderex.component.foundation.DropdownMenuItem
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.settings.RadioItem
import com.twidere.twiderex.component.settings.switchItem
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.dataprovider.mapper.Strings
import com.twidere.twiderex.extensions.rememberPresenterState
import com.twidere.twiderex.navigation.Root
import com.twidere.twiderex.preferences.model.SwipeAction
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
        SwipeGestureItem(
          leftIcon = {
            ActionIcon(
              icon = rememberVectorPainter(
                image = Icons.TwoTone.ArrowForward
              ),
            )
          },
          rightIcon = {
            val action = state.swipePreferences.leftShort.action.toUi()
            action.icon?.let {
              ActionIcon(
                icon = painterResource(it),
                isLargeIcon = true,
                hasPadding = true,
                backgroundColor = MaterialTheme.colors.surface,
                iconColor = MaterialTheme.colors.primary,
              )
            }
          }
        ) {
        }
        SwipeGestureItem(
          backgroundColor = MaterialTheme.colors.onBackground.copy(alpha = 0.6f),
          leftIcon = {
            ActionIcon(
              icon = rememberVectorPainter(
                image = Icons.TwoTone.ArrowForward
              ),
              isLargeIcon = true,
            )
          },
          rightIcon = {
            val action = state.swipePreferences.leftLong.action.toUi()
            action.icon?.let {
              ActionIcon(
                icon = painterResource(
                  res = it
                ),
                hasPadding = true,
                backgroundColor = MaterialTheme.colors.surface,
                iconColor = MaterialTheme.colors.onBackground.copy(alpha = 0.6f),
              )
            }
          }
        ) {
        }
        SwipeGestureItem(
          leftIcon = {
            val action = state.swipePreferences.rightShort.action.toUi()
            action.icon?.let {
              ActionIcon(
                icon = painterResource(
                  res = it
                ),
                isLargeIcon = true,
                hasPadding = true,
                backgroundColor = MaterialTheme.colors.surface,
                iconColor = MaterialTheme.colors.primary,
              )
            }
          },
          rightIcon = {
            ActionIcon(
              icon = rememberVectorPainter(
                image = Icons.TwoTone.ArrowBack
              ),
            )
          }
        ) {
        }
        SwipeGestureItem(
          backgroundColor = MaterialTheme.colors.onBackground.copy(alpha = 0.6f),
          leftIcon = {
            val action = state.swipePreferences.rightLong.action.toUi()
            action.icon?.let {
              ActionIcon(
                icon = painterResource(
                  res = it,
                ),
                hasPadding = true,
                backgroundColor = MaterialTheme.colors.surface,
                iconColor = MaterialTheme.colors.onBackground.copy(alpha = 0.6f),
              )
            }
          },
          rightIcon = {
            ActionIcon(
              icon = rememberVectorPainter(
                image = Icons.TwoTone.ArrowBack,
              ),
              isLargeIcon = true,
            )
          }
        ) {
        }
      }
    }
  }
}

@Composable
private fun RowScope.ActionIcon(
  icon: Painter,
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
    Icon(
      painter = icon,
      contentDescription = null,
      modifier = Modifier.height(
        16.dp
      ).wrapContentWidth()
        .align(Alignment.Center),
      tint = iconColor,
    )
  }
}

@Composable
private fun SwipeGestureItem(
  backgroundColor: Color = MaterialTheme.colors.primary,
  leftIcon: @Composable RowScope.() -> Unit,
  rightIcon: @Composable RowScope.() -> Unit,
  currentAction: SwipeAction = SwipeAction.None,
  onActionSelect: (SwipeAction) -> Unit,
) {
  Row(
    modifier = Modifier.padding(
      vertical = 18.dp,
      horizontal = 16.dp
    ).clickable {
      // onActionSelect.invoke()
    }
  ) {
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
  }
}

@Composable
private fun ActionSelectMenu(
  currentAction: SwipeAction = SwipeAction.None,
  onActionSelect: (SwipeAction) -> Unit,
) {
  var show by remember {
    mutableStateOf(false)
  }
  AlertDialog(
    text = {
      // SwipeAction.values().forEach {
      //
      // }
      // Column {
      //   RadioItem()
      // }
    },
    onDismissRequest = {

    },
    confirmButton = {

    }
  )
}
