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

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.twidere.twiderex.component.foundation.NativePlayerView
import com.twidere.twiderex.component.foundation.PlayerProgressCallBack
import com.twidere.twiderex.component.foundation.SeekBar
import com.twidere.twiderex.component.foundation.SeekBarState

@Composable
fun CustomVideoControl(
    player: NativePlayerView
) {
    val seekBarState = remember {
        SeekBarState(
            duration = player.duration(),
            initPosition = player.contentPosition()
        ) { seekTime ->
            player.seekTo(seekTime)
        }
    }
    var playerState = true
    player.playerProgressCallBack = object : PlayerProgressCallBack {
        override fun onTimeChanged(time: Long) {
            seekBarState.onTimeChange(time)
        }
    }
    Row {
        Button(
            onClick = {
                if (!playerState) {
                    player.resume()
                } else {
                    player.pause()
                }
                playerState = !playerState
            }
        ) {
            Text("play/pause")
        }
        SeekBar(
            state = seekBarState
        )
    }
}
