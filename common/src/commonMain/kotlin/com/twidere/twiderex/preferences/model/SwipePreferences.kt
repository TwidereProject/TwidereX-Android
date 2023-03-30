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
package com.twidere.twiderex.preferences.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.dataprovider.mapper.Files
import com.twidere.twiderex.dataprovider.mapper.Strings
import dev.icerock.moko.resources.FileResource
import kotlinx.serialization.Serializable

@Serializable
data class SwipePreferences(
  val useSwipeGestures: Boolean = false,
  val rightShort: SwipeGesture.RightShort = SwipeGesture.RightShort(),
  val rightLong: SwipeGesture.RightLong = SwipeGesture.RightLong(),
  val leftShort: SwipeGesture.LeftShort = SwipeGesture.LeftShort(),
  val leftLong: SwipeGesture.LeftLong = SwipeGesture.LeftLong(),
)

sealed interface SwipeGesture {
  @Serializable
  data class RightShort(val action: SwipeActionType = SwipeActionType.Reply) : SwipeGesture

  @Serializable
  data class RightLong(val action: SwipeActionType = SwipeActionType.Repost) : SwipeGesture

  @Serializable
  data class LeftShort(val action: SwipeActionType = SwipeActionType.Share) : SwipeGesture

  @Serializable
  data class LeftLong(val action: SwipeActionType = SwipeActionType.Like) : SwipeGesture
}

@Composable
fun SwipeGesture.tittle() = when (this) {
  is SwipeGesture.RightShort -> stringResource(res = Strings.scene_settings_swipe_gestures_gestures_right_short)
  is SwipeGesture.LeftLong -> stringResource(res = Strings.scene_settings_swipe_gestures_gestures_left_long)
  is SwipeGesture.LeftShort -> stringResource(res = Strings.scene_settings_swipe_gestures_gestures_left_short)
  is SwipeGesture.RightLong -> stringResource(res = Strings.scene_settings_swipe_gestures_gestures_right_long)
}

@Serializable
enum class SwipeActionType {
  None,
  Reply,
  Repost,
  Like,
  Share,
  Detail,
}

@Immutable
data class ActionUi(
  val tittle: String,
  val icon: FileResource? = null,
)

@Composable
fun SwipeActionType.toUi() = when (this) {
  SwipeActionType.None -> ActionUi(tittle = stringResource(res = Strings.scene_settings_swipe_gestures_actions_none))
  SwipeActionType.Reply -> ActionUi(
    tittle = stringResource(res = Strings.scene_settings_swipe_gestures_actions_reply),
    icon = Files.ic_corner_up_left
  )
  SwipeActionType.Repost -> ActionUi(
    tittle = stringResource(res = Strings.scene_settings_swipe_gestures_actions_repost),
    icon = Files.ic_repeat
  )
  SwipeActionType.Like -> ActionUi(
    tittle = stringResource(res = Strings.scene_settings_swipe_gestures_actions_like),
    icon = Files.ic_heart,
  )
  SwipeActionType.Share -> ActionUi(
    tittle = stringResource(res = Strings.scene_settings_swipe_gestures_actions_share),
    icon = Files.ic_share,
  )
  SwipeActionType.Detail -> ActionUi(
    tittle = stringResource(res = Strings.scene_settings_swipe_gestures_actions_detail),
    icon = Files.ic_template,
  )
}
