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
package com.twidere.twiderex.scenes.settings.display

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.preferences.PreferencesHolder
import com.twidere.twiderex.preferences.model.DisplayPreferences
import kotlinx.coroutines.flow.Flow

@Composable
fun DisplayPresenter(
  event: Flow<DisplayEvent>,
  preferencesHolder: PreferencesHolder = get(),
): DisplayState {
  val display by preferencesHolder.displayPreferences.data.collectAsState(DisplayPreferences())
  var fontScale by remember { mutableStateOf(display.fontScale) }

  LaunchedEffect(Unit) {
    event.collect { event ->
      when (event) {
        DisplayEvent.CommitFontScale -> preferencesHolder.displayPreferences.updateData {
          it.copy(fontScale = fontScale)
        }
        is DisplayEvent.SetAutoPlayback -> preferencesHolder.displayPreferences.updateData {
          it.copy(autoPlayback = event.value)
        }
        is DisplayEvent.SetAvatarStyle -> preferencesHolder.displayPreferences.updateData {
          it.copy(avatarStyle = event.avatarStyle)
        }
        is DisplayEvent.SetFontScale -> fontScale = event.fontScale
        is DisplayEvent.SetMediaPreview -> preferencesHolder.displayPreferences.updateData {
          it.copy(mediaPreview = event.value)
        }
        is DisplayEvent.SetMuteByDefault -> preferencesHolder.displayPreferences.updateData {
          it.copy(muteByDefault = event.value)
        }
        is DisplayEvent.SetUrlPreview -> preferencesHolder.displayPreferences.updateData {
          it.copy(urlPreview = event.value)
        }
        is DisplayEvent.SetUseSystemFontSize -> preferencesHolder.displayPreferences.updateData {
          it.copy(useSystemFontSize = event.useSystemFont)
        }
      }
    }
  }

  return DisplayState(
    display,
    fontScale
  )
}

data class DisplayState(
  val display: DisplayPreferences,
  val fontScale: Float,
)

interface DisplayEvent {
  data class SetUseSystemFontSize(val useSystemFont: Boolean) : DisplayEvent
  data class SetFontScale(val fontScale: Float) : DisplayEvent
  object CommitFontScale : DisplayEvent
  data class SetAvatarStyle(val avatarStyle: DisplayPreferences.AvatarStyle) : DisplayEvent
  data class SetUrlPreview(val value: Boolean) : DisplayEvent
  data class SetMediaPreview(val value: Boolean) : DisplayEvent
  data class SetMuteByDefault(val value: Boolean) : DisplayEvent
  data class SetAutoPlayback(val value: DisplayPreferences.AutoPlayback) : DisplayEvent
}
