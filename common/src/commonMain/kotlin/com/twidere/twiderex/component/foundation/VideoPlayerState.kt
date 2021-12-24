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
package com.twidere.twiderex.component.foundation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import com.twidere.twiderex.component.foundation.platform.PlatformPlayerView

@Composable
fun rememberVideoPlayerState(
    url: String,
    isPlaying: Boolean = false,
    volume: Float = 1f,
    isReady: Boolean = false,
    currentPosition: Long = 0L,
    isMute: Boolean = false,
): VideoPlayerState {
    return rememberSaveable(
        saver = VideoPlayerState.Saver(url),
        key = url
    ) {
        VideoPlayerState(
            url = url,
            isPlaying = isPlaying,
            volume = volume,
            isReady = isReady,
            currentPosition = currentPosition,
            isMute = isMute
        )
    }
}

@Stable
class VideoPlayerState(
    val url: String,
    isReady: Boolean,
    isPlaying: Boolean,
    currentPosition: Long,
    volume: Float,
    isMute: Boolean
) {
    private lateinit var player: PlatformPlayerView

    private var _isReady = mutableStateOf(isReady)
    var isReady get() = _isReady.value
        set(value) {
            _isReady.value = value
        }
    private var _isBuffering = mutableStateOf(false)
    var isBuffering get() = _isBuffering.value
        set(value) {
            _isBuffering.value = value
        }
    private var _isPlaying = mutableStateOf(isPlaying)
    var isPlaying get() = _isPlaying.value
        set(value) {
            _isPlaying.value = value
        }

    private var seeking = false

    private var _currentPosition = mutableStateOf(currentPosition)
    var currentPosition get() = _currentPosition.value
        set(value) {
            _currentPosition.value = value
        }

    private val progressCallBack = object : PlayerProgressCallBack {
        override fun onTimeChanged(time: Long) {
            if (!seeking) _currentPosition.value = time
        }
    }

    private val playerCallBack = object : PlayerCallBack {
        override fun onPrepareStart() {
            _isReady.value = false
        }
        override fun onReady() {
            _isReady.value = true
            _isBuffering.value = false
            initPlay()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
            _isBuffering.value = false
        }

        override fun onBuffering() {
            _isBuffering.value = true
        }
    }

    private var _volume = mutableStateOf(volume)
    var volume get() = _volume.value
        set(value) {
            _volume.value = value
            player.setVolume(value)
        }

    private var _isMute = mutableStateOf(isMute)
    var isMute get() = _isMute.value
        set(value) {
            _isMute.value = value
            player.setMute(value)
        }

    val duration get() = player.duration().coerceAtLeast(0)
    val showThumbnail get() = !isReady
    val showLoading get() = !isReady || isBuffering

    companion object {
        fun Saver(url: String): Saver<VideoPlayerState, *> = listSaver(
            save = {
                listOf<Any>(
                    it.isReady,
                    it.isPlaying,
                    it.currentPosition,
                    it.volume,
                    it.isMute
                )
            },
            restore = {
                VideoPlayerState(
                    url = url,
                    isReady = it[0] as Boolean,
                    isPlaying = it[1] as Boolean,
                    currentPosition = it[2] as Long,
                    volume = it[3] as Float,
                    isMute = it[4] as Boolean
                )
            }
        )
    }

    private fun initPlay() {
        player.setVolume(volume)
        player.setMute(isMute)
        if (isReady) player.play()
    }

    fun bind(player: PlatformPlayerView) {
        this.player = player
        initPlay()
    }

    // only for VideoPlayer
    internal fun onResume() {
        player.registerProgressCallback(progressCallBack)
        player.registerPlayerCallback(playerCallBack)
        if (isPlaying) player.play()
    }

    // only for VideoPlayer
    internal fun onPause() {
        player.removeProgressCallback(progressCallBack)
        player.removePlayerCallback(playerCallBack)
        // remove callback first then pause, so state can store the playing state before pause
        player.pause()
    }

    fun playSwitch() {
        if (isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }

    fun seeking() {
        seeking = true
    }

    fun seekTo(time: Long) {
        player.seekTo(time)
        seeking = false
    }

    fun mute() {
        isMute = !isMute
    }
}
