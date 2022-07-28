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
package com.twidere.twiderex.scenes.settings.notification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.preferences.PreferencesHolder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Composable
fun NotificationPresenter(
  event: Flow<NotificationEvent>,
  holder: PreferencesHolder = get(),
): NotificationState {
  val enabled by holder.notificationPreferences.data.map { it.enableNotification }.collectAsState(initial = false)

  LaunchedEffect(Unit) {
    event.collect { event ->
      when (event) {
        is NotificationEvent.SetEnabled -> {
          holder.notificationPreferences.updateData {
            it.copy(enableNotification = event.enabled)
          }
        }
      }
    }
  }

  return NotificationState(
    enabled = enabled
  )
}

data class NotificationState(
  val enabled: Boolean,
)

sealed interface NotificationEvent {
  data class SetEnabled(val enabled: Boolean) : NotificationEvent
}
