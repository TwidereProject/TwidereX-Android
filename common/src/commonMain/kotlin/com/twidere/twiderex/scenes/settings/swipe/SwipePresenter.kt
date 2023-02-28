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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.preferences.PreferencesHolder
import com.twidere.twiderex.preferences.model.SwipeAction
import com.twidere.twiderex.preferences.model.SwipeGesture
import com.twidere.twiderex.preferences.model.SwipePreferences
import kotlinx.coroutines.flow.Flow

@Composable
fun SwipePresenter(
  event: Flow<SwipeEvent>,
  preferencesHolder: PreferencesHolder = get(),
): SwipeState {
  val swipePreferences by remember {
    preferencesHolder.swipePreferences.data
  }.collectAsState(SwipePreferences())

  suspend fun update(
    block: (SwipePreferences) -> SwipePreferences
  ) {
    preferencesHolder.swipePreferences.updateData {
      block(it)
    }
  }

  LaunchedEffect(Unit) {
    event.collect { event ->
      when (event) {
        is SwipeEvent.SetUseSwipe -> update {
          it.copy(useSwipeGestures = event.value)
        }
        is SwipeEvent.SetLeftShortSwipeAction -> update {
          it.copy(leftShort = SwipeGesture.LeftShort(event.action))
        }
        is SwipeEvent.SetLeftLongSwipeAction -> update {
          it.copy(leftLong = SwipeGesture.LeftLong(event.action))
        }
        is SwipeEvent.SetRightLongSwipeAction -> update {
          it.copy(rightLong = SwipeGesture.RightLong(event.action))
        }
        is SwipeEvent.SetRightShortSwipeAction -> update {
          it.copy(rightShort = SwipeGesture.RightShort(event.action))
        }
      }
    }
  }

  return SwipeState(swipePreferences = swipePreferences)
}

@Immutable
data class SwipeState(
  val swipePreferences: SwipePreferences
)

sealed interface SwipeEvent {
  data class SetUseSwipe(val value: Boolean) : SwipeEvent
  data class SetLeftShortSwipeAction(val action: SwipeAction) : SwipeEvent
  data class SetLeftLongSwipeAction(val action: SwipeAction) : SwipeEvent
  data class SetRightShortSwipeAction(val action: SwipeAction) : SwipeEvent
  data class SetRightLongSwipeAction(val action: SwipeAction) : SwipeEvent
}
