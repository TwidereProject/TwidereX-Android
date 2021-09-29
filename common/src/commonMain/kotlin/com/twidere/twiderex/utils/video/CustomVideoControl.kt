/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.twidere.twiderex.MR
import com.twidere.twiderex.component.foundation.NativePlayerView
import com.twidere.twiderex.component.foundation.PlayerProgressCallBack
import com.twidere.twiderex.component.foundation.SeekBar
import com.twidere.twiderex.component.foundation.SeekBarState
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.stringResource

@Composable
fun CustomVideoControl(
    player: NativePlayerView,
    playEnabled: Boolean = true,
    mute: Boolean = false,
    modifier: Modifier = Modifier,
    onPlayPause: ((Boolean) -> Unit)? = null
) {
    val seekBarState = remember {
        SeekBarState(
            duration = player.duration(),
            initPosition = player.contentPosition()
        ) { seekTime ->
            player.seekTo(seekTime)
        }
    }
    var isPlaying by remember {
        mutableStateOf(playEnabled)
    }
    var isMute by remember {
        mutableStateOf(mute)
    }
    LaunchedEffect(player) {
        player.playerProgressCallBack = object : PlayerProgressCallBack {
            override fun onTimeChanged(time: Long) {
                seekBarState.onTimeChange(time)
            }
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = {
                isPlaying = (!isPlaying).apply {
                    onPlayPause?.invoke(this)
                }
            },
        ) {
            Icon(
                painter = painterResource(res = if (isPlaying) MR.files.ic_player_pause else MR.files.ic_player_play),
                contentDescription = stringResource(res = MR.strings.accessibility_common_video_play),
                tint = MaterialTheme.colors.onSurface
            )
        }

        Box(modifier.weight(1f)) {
            SeekBar(
                state = seekBarState
            )
        }

        IconButton(
            onClick = {
                isMute = (!isMute).apply {
                    player.setMute(this)
                }
            },
        ) {
            Icon(
                painter = painterResource(res = if (isMute) MR.files.ic_volume_mute else MR.files.ic_volume),
                contentDescription = stringResource(res = MR.strings.accessibility_common_video_play),
                tint = MaterialTheme.colors.onSurface
            )
        }
    }
}
