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
import androidx.compose.material.Slider
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
import com.twidere.twiderex.component.foundation.PlayerCallBack
import com.twidere.twiderex.component.foundation.PlayerProgressCallBack
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.stringResource

class VideoController {

    private var player: NativePlayerView? = null

    var videoPraparedCallBack: (() -> Unit)? = null

    var videoReadyCallBack: ((ready: Boolean) -> Unit)? = null

    var videoPlayingCallBack: ((isplaying: Boolean) -> Unit)? = null

    fun bind(player: NativePlayerView) {
        val playerCallBack = object : PlayerCallBack {
            override fun isReady(ready: Boolean) {
                videoReadyCallBack?.invoke(ready)
            }

            override fun setPlaying(isPlaying: Boolean) {
                videoPlayingCallBack?.invoke(isPlaying)
            }

            override fun onprepared() {
                videoPraparedCallBack?.invoke()
            }
        }
        player.playerCallBack = playerCallBack
        this.player = player
    }

    fun isAutoPlay(): Boolean {
        return player?.playWhenReady ?: false
    }

    fun setProgressCallBack(progressCallBack: PlayerProgressCallBack) {
        player?.playerProgressCallBack = progressCallBack
    }

    fun enable(enable: Boolean) {
        player?.enablePlaying = enable
    }

    fun resume(autoPlay: Boolean) {
        player?.playWhenReady = autoPlay
        player?.resume()
    }

    fun pause() {
        player?.playWhenReady = false
        player?.pause()
    }

    fun contentPosition(): Long {
        return player?.contentPosition() ?: 0
    }

    fun duration(): Long {
        return player?.duration()?.coerceAtLeast(0) ?: 0
    }

    fun seekTo(time: Long) {
        player?.seekTo(time)
    }

    fun setVolume(volume: Float) {
        player?.setVolume(volume)
    }

    fun setMute(mute: Boolean) {
        player?.setMute(mute)
    }

    fun release() {
        player?.release()
    }
}

@Composable
fun CustomVideoControl(
    realController: VideoController,
    playEnabled: Boolean = true,
    mute: Boolean = false,
    modifier: Modifier = Modifier,
) {
    var videoPrapared by remember {
        mutableStateOf(false)
    }
    var isPlaying by remember {
        mutableStateOf(playEnabled)
    }
    var isMute by remember {
        mutableStateOf(mute)
    }
    var isSeeking by remember {
        mutableStateOf(false)
    }
    var sliderValue by remember {
        mutableStateOf(realController.contentPosition().toFloat())
    }
    LaunchedEffect(realController) {
        realController.videoPraparedCallBack = {
            videoPrapared = true
        }
        realController.setProgressCallBack(object : PlayerProgressCallBack {
            override fun onTimeChanged(time: Long) {
                if (!isSeeking) {
                    sliderValue = time.toFloat()
                }
            }
        })
    }
    if (!videoPrapared) {
        return
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = {
                isPlaying = (!isPlaying).apply {
                    realController.enable(this)
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
            Slider(
                valueRange = 0f..realController.duration().toFloat(),
                value = sliderValue,
                onValueChange = {
                    isSeeking = true
                    sliderValue = it
                },
                onValueChangeFinished = {
                    realController.seekTo(sliderValue.toLong())
                    isSeeking = false
                }
            )
        }

        IconButton(
            onClick = {
                isMute = (!isMute).apply {
                    realController.setMute(this)
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
