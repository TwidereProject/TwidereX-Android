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
package com.twidere.twiderex.component.foundation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.twidere.services.http.config.HttpConfig
import com.twidere.twiderex.MR
import com.twidere.twiderex.compose.LocalResLoader
import com.twidere.twiderex.preferences.LocalHttpConfig
import com.twidere.twiderex.preferences.model.DisplayPreferences
import com.twidere.twiderex.ui.LocalIsActiveNetworkMetered
import com.twidere.twiderex.ui.LocalVideoPlayback
import com.twidere.twiderex.utils.video.VideoController
import com.twidere.twiderex.utils.video.VideoPool
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moe.tlaster.precompose.lifecycle.Lifecycle
import moe.tlaster.precompose.lifecycle.LifecycleObserver
import moe.tlaster.precompose.ui.LocalLifecycleOwner

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    url: String,
    volume: Float = 1f,
    customControl: VideoController? = null,
    zOrderMediaOverlay: Boolean = false,
    keepScreenOn: Boolean = false,
    thumb: @Composable (() -> Unit)? = null,
) {
    var playing by remember { mutableStateOf(false) }
    var shouldShowThumb by remember { mutableStateOf(false) }
    val playInitial = getPlayInitial()
    var autoPlay by remember(url) { mutableStateOf(playInitial) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val resLoder = LocalResLoader.current
    val httpConfig = LocalHttpConfig.current
    // var mediaPrepared by remember { mutableStateOf(false) }

    Box {
        if (playInitial) {

            val nativePlayerView = remember(url) {
                NativePlayerView(
                    url = url,
                    autoPlay = autoPlay,
                    httpConfig = httpConfig,
                    zOrderMediaOverlay = zOrderMediaOverlay,
                    keepScreenOn = keepScreenOn,
                ).apply {
                    playerCallBack = object : PlayerCallBack {
                        override fun isReady(ready: Boolean) {
                            shouldShowThumb = ready
                        }

                        override fun setPlaying(isPlaying: Boolean) {
                            playing = isPlaying
                        }

                        override fun onprepared() {
                            customControl?.prepared()
                        }
                    }
                    customControl?.bind(this)
                }
            }

            nativePlayerView.setVolume(volume)

            fun updateState() {
                autoPlay = nativePlayerView.playWhenReady
                VideoPool.set(url, 0L.coerceAtLeast(nativePlayerView.contentPosition()))
            }

            var isResume by remember {
                mutableStateOf(true)
            }
            val videoKey by remember {
                mutableStateOf(url + System.currentTimeMillis())
            }
            DisposableEffect(Unit) {
                val observer = object : LifecycleObserver {
                    override fun onStateChanged(state: Lifecycle.State) {
                        when (state) {
                            Lifecycle.State.Active -> {
                                isResume = true
                            }
                            Lifecycle.State.InActive -> {
                                isResume = false
                                updateState()
                            }
                            else -> {
                            }
                        }
                    }
                }
                lifecycle.addObserver(observer)
                onDispose {
                    updateState()
                    nativePlayerView.release()
                    VideoPool.removeRect(videoKey)
                    lifecycle.removeObserver(observer)
                }
            }

            var middleLine = 0.0f
            val composableScope = rememberCoroutineScope()

            var isMostCenter by remember(url) {
                mutableStateOf(false)
            }
            var debounceJob: Job? = null
            Box(
                modifier = Modifier.onGloballyPositioned { coordinates ->
                    if (middleLine == 0.0f) {
                        var rootCoordinates = coordinates
                        while (rootCoordinates.parentCoordinates != null) {
                            rootCoordinates = rootCoordinates.parentCoordinates!!
                        }
                        rootCoordinates.boundsInWindow().run {
                            middleLine = (top + bottom) / 2
                        }
                    }
                    coordinates.boundsInWindow().run {
                        VideoPool.setRect(videoKey, this)
                        if (!isMostCenter && VideoPool.fullInScreen(videoKey, coordinates.size.height)) {
                            debounceJob?.cancel()
                            debounceJob = composableScope.launch {
                                delay(VideoPool.DEBOUNCE_DELAY)
                                if (VideoPool.isMostCenter(videoKey, middleLine)) {
                                    isMostCenter = true
                                }
                            }
                        } else if (isMostCenter && !VideoPool.isMostCenter(videoKey, middleLine)) {
                            isMostCenter = false
                        }
                    }
                }
            ) {
                PlatformView(
                    nativePLayerView = nativePlayerView,
                    modifier = modifier,
                ) {
                    if (isResume && isMostCenter) {
                        it.playWhenReady = autoPlay
                        nativePlayerView.resume()
                    } else {
                        it.playWhenReady = false
                        nativePlayerView.pause()
                    }
                }
            }
        }

        if ((shouldShowThumb || !playing) && thumb != null) {
            thumb()
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    tint = Color.White.copy(alpha = LocalContentAlpha.current),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(UserAvatarDefaults.AvatarSize)
                        .background(MaterialTheme.colors.primary, CircleShape),
                    contentDescription = resLoder.getString(
                        MR.strings.accessibility_common_video_play
                    )
                )
            }
        }
    }
}

private fun VideoController.prepared() {
    this.videoPrepared.value = true
}

@Composable
private fun getPlayInitial() = when (LocalVideoPlayback.current) {
    DisplayPreferences.AutoPlayback.Auto -> !LocalIsActiveNetworkMetered.current
    DisplayPreferences.AutoPlayback.Always -> true
    DisplayPreferences.AutoPlayback.Off -> false
}

interface PlayerCallBack {
    fun isReady(ready: Boolean)
    fun setPlaying(isPlaying: Boolean)
    fun onprepared()
}

interface PlayerProgressCallBack {
    fun onTimeChanged(time: Long)
}

expect class NativePlayerView(
    url: String,
    autoPlay: Boolean,
    httpConfig: HttpConfig,
    zOrderMediaOverlay: Boolean,
    keepScreenOn: Boolean,
) {
    var playWhenReady: Boolean
    var enablePlaying: Boolean
    var playerCallBack: PlayerCallBack?
    var playerProgressCallBack: PlayerProgressCallBack?
    fun resume()
    fun pause()
    fun contentPosition(): Long
    fun duration(): Long
    fun seekTo(time: Long)
    fun setVolume(volume: Float)
    fun setMute(mute: Boolean)
    fun release()
}

@Composable
expect fun PlatformView(
    modifier: Modifier,
    nativePLayerView: NativePlayerView,
    update: (NativePlayerView) -> Unit
)

object UserAvatarDefaults {
    val AvatarSize = 44.dp
}
