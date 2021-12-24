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
package com.twidere.twiderex.utils.video

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.twidere.twiderex.component.foundation.VideoPlayerState
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.stringResource
@Composable
fun CustomVideoControl(
    state: VideoPlayerState,
    modifier: Modifier = Modifier,
) {
    if (!state.isReady) {
        return
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = {
                state.playSwitch()
            },
        ) {
            Icon(
                painter = painterResource(res = if (state.isPlaying) com.twidere.twiderex.MR.files.ic_player_pause else com.twidere.twiderex.MR.files.ic_player_play),
                contentDescription = stringResource(res = com.twidere.twiderex.MR.strings.accessibility_common_video_play),
                tint = MaterialTheme.colors.onSurface
            )
        }

        Box(modifier.weight(1f)) {
            Slider(
                valueRange = 0f..state.duration.toFloat(),
                value = state.currentPosition.toFloat(),
                onValueChange = {
                    state.seeking()
                    state.currentPosition = it.toLong()
                },
                onValueChangeFinished = {
                    state.seekTo(state.currentPosition)
                }
            )
        }

        IconButton(
            onClick = {
                state.mute()
            },
        ) {
            Icon(
                painter = painterResource(res = if (state.isMute) com.twidere.twiderex.MR.files.ic_volume_mute else com.twidere.twiderex.MR.files.ic_volume),
                contentDescription = stringResource(res = com.twidere.twiderex.MR.strings.accessibility_common_video_play),
                tint = MaterialTheme.colors.onSurface
            )
        }
    }
}
