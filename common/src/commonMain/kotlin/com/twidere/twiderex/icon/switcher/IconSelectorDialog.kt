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
package com.twidere.twiderex.icon.switcher

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DrawerDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.foundation.AlertDialog
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.dataprovider.mapper.Strings
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.kmp.AppIcon
import com.twidere.twiderex.kmp.IconModifier
import com.twidere.twiderex.kmp.launchIcons
@Composable
fun IconSelectorDialog(
  show: Boolean,
  modifier: Modifier = Modifier,
  onDismissRequest: () -> Unit,
  onIconSelect: (AppIcon) -> Unit,
  iconModifier: IconModifier = get(),
) {
  if (!show) return
  AlertDialog(
    modifier = modifier,
    onDismissRequest = onDismissRequest,
    confirmButton = {},
    title = {
      Row {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "close",
          modifier = Modifier.clickable {
            onDismissRequest()
          }
        )
        Spacer(modifier = Modifier.width(32.dp))
        Text(
          modifier = Modifier.align(Alignment.CenterVertically),
          text = stringResource(
            res = Strings.scene_settings_appearance_app_icon
          )
        )
      }
    },
    text = {
      LazyRow(
        contentPadding = PaddingValues(IconSelectorDefault.rowPadding),
        horizontalArrangement = Arrangement.spacedBy(IconSelectorDefault.itemGap),
      ) {
        itemsIndexed(launchIcons) { index, icon ->
          Image(
            imageVector = icon,
            contentDescription = "app icon",
            modifier = Modifier.size(IconSelectorDefault.iconSize).clickable {
              val appIcon = AppIcon.fromIndex(index)
              iconModifier.changeIcon(appIcon)
              onIconSelect.invoke(appIcon)
              onDismissRequest.invoke()
            }.clip(RoundedCornerShape(IconSelectorDefault.iconRadius))
          )
        }
      }
    }
  )
}

@Composable
fun BottomSheet(
  onDismissRequest: () -> Unit,
  modifier: Modifier = Modifier,
  shape: Shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
  paddingValues: PaddingValues = PaddingValues(0.dp),
  content: @Composable BoxScope.() -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(DrawerDefaults.scrimColor)
      .clickable(
        onClick = {
          onDismissRequest()
        },
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
      ),
    contentAlignment = Alignment.BottomCenter,
  ) {
    Surface(
      modifier = modifier.clickable(
        onClick = {},
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
      ).padding(paddingValues),
      shape = shape,
    ) {
      content()
    }
  }
}

private object IconSelectorDefault {
  val rowPadding = 16.dp
  val itemGap = 12.dp
  val iconSize = 64.dp
  val iconRadius = 8.dp
}
