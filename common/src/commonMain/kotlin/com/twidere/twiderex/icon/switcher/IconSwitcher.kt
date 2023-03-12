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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.dataprovider.mapper.Strings
import com.twidere.twiderex.icon.TwidereIcons
import com.twidere.twiderex.icon.twidereicons.ChooseToUse
import com.twidere.twiderex.kmp.AppIcon
import com.twidere.twiderex.kmp.Platform
import com.twidere.twiderex.kmp.currentPlatform
import com.twidere.twiderex.kmp.systemVersion

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun IconSwitcher(
  appIcon: AppIcon,
  onclick: () -> Unit,
) {
  if (
    currentPlatform != Platform.Android ||
    systemVersion < 25
  ) {
    return
  }
  ListItem(
    modifier = Modifier.clickable {
      onclick.invoke()
    },
    icon = {
      Icon(
        imageVector = TwidereIcons.ChooseToUse,
        contentDescription = stringResource(Strings.scene_settings_appearance_app_icon),
        modifier = Modifier.size(24.dp),
      )
    },
    trailing = {
      Image(
        painter = rememberVectorPainter(appIcon.toImageVector()),
        contentDescription = stringResource(Strings.scene_settings_appearance_app_icon),
        modifier = Modifier.size(32.dp).clip(MaterialTheme.shapes.small).clipToBounds(),
      )
    }
  ) {
    Text(stringResource(Strings.scene_settings_appearance_app_icon))
  }
}
