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
package com.twidere.twiderex.scenes.settings.layout

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.ReorderableColumn
import com.twidere.twiderex.component.foundation.rememberReorderableColumnState
import com.twidere.twiderex.component.lazy.ItemHeader
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.status.UserName
import com.twidere.twiderex.component.status.UserScreenName
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.extensions.rememberPresenterState
import com.twidere.twiderex.model.HomeMenus
import com.twidere.twiderex.scenes.home.item
import com.twidere.twiderex.ui.TwidereScene

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LayoutScene() {
  val (state, channel) = rememberPresenterState { LayoutPresenter(it) }
  if (state !is LayoutState.Data) {
    // TODO: Show other states
    return
  }
  TwidereScene {
    InAppNotificationScaffold(
      topBar = {
        AppBar(
          navigationIcon = {
            AppBarNavigationButton()
          },
          title = {
            Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_layout_title))
          }
        )
      }
    ) {
      Column(
        modifier = Modifier
          .verticalScroll(rememberScrollState()),
      ) {
        Surface(
          color = MaterialTheme.colors.primary,
        ) {
          ListItem(
            text = {
              Row {
                UserName(user = state.user)
                UserScreenName(user = state.user)
              }
            }
          )
        }
        ListItem(
          text = {
            Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_layout_desc_title))
          },
          secondaryText = {
            Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_layout_desc_content))
          }
        )
        ReorderableColumn(
          data = state.menus,
          state = rememberReorderableColumnState { oldIndex, newIndex ->
            channel.trySend(
              LayoutEvent.UpdateMenuOrder(
                oldIndex = oldIndex,
                newIndex = newIndex
              )
            )
          },
          dragingContent = {
            Card {
              LayoutItemContent(
                it = it,
                menus = state.menus,
                addMenu = {
                  channel.trySend(
                    LayoutEvent.AddMenu(
                      index = it
                    )
                  )
                },
                removeMenu = {
                  channel.trySend(
                    LayoutEvent.RemoveMenu(
                      index = it
                    )
                  )
                }
              )
            }
          }
        ) {
          LayoutItemContent(
            it = it,
            menus = state.menus,
            addMenu = {
              channel.trySend(
                LayoutEvent.AddMenu(
                  index = it
                )
              )
            },
            removeMenu = {
              channel.trySend(
                LayoutEvent.RemoveMenu(
                  index = it
                )
              )
            }
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun LayoutItemContent(
  it: Any,
  menus: List<Any>,
  removeMenu: (Int) -> Unit,
  addMenu: (Int) -> Unit,
) {
  val current = remember(menus, it) { menus.indexOf(it) }
  val falseIndex = remember(menus) { menus.indexOf(false) }
  val visible = remember(current, falseIndex) { current < falseIndex }
  when (it) {
    is Boolean -> {
      ItemHeader {
        Text(
          text = if (it) {
            stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_layout_actions_tabbar)
          } else {
            stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_layout_actions_drawer)
          }
        )
      }
    }
    is HomeMenus -> {
      ListItem(
        text = {
          Text(text = it.item.name())
        },
        icon = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
          ) {
            IconButton(
              onClick = {
                if (visible) {
                  removeMenu(
                    current,
                  )
                } else {
                  addMenu(
                    current,
                  )
                }
              }
            ) {
              Image(
                painter = painterResource(
                  res = if (visible) {
                    com.twidere.twiderex.MR.files.ic_delete_colored
                  } else {
                    com.twidere.twiderex.MR.files.ic_add_colored
                  }
                ),
                modifier = Modifier.size(24.dp),
                contentDescription = null,
              )
            }
            Icon(
              it.item.icon(),
              contentDescription = null,
              tint = MaterialTheme.colors.primary,
            )
          }
        },
        trailing = {
          CompositionLocalProvider(
            LocalContentAlpha provides ContentAlpha.medium
          ) {
            Icon(Icons.Default.Menu, contentDescription = null)
          }
        }
      )
    }
  }
}
