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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
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
import com.twidere.twiderex.MR
import com.twidere.twiderex.compose.LocalResLoader
import com.twidere.twiderex.preferences.model.DisplayPreferences
import com.twidere.twiderex.ui.LocalIsActiveNetworkMetered
import com.twidere.twiderex.ui.LocalVideoPlayback
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
    customControl: @Composable ((NativePlayerView) -> Unit)? = null,
    showControls: Boolean = customControl == null,
    zOrderMediaOverlay: Boolean = false,
    keepScreenOn: Boolean = false,
    isListItem: Boolean = true,
    thumb: @Composable (() -> Unit)? = null,
) {
    var playing by remember { mutableStateOf(false) }
    var shouldShowThumb by remember { mutableStateOf(false) }
    val playInitial = getPlayInitial()
    var autoPlay by remember(url) { mutableStateOf(playInitial) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val resLoder = LocalResLoader.current
    val context = getContext()
    val httpConfig = httpConfig()
    var mediaPrepared by remember { mutableStateOf(false) }
    Box {
        if (playInitial) {

            val nativePlayerView = remember(url) {
                NativePlayerView(
                    url = url,
                    autoPlay = autoPlay,
                    httpConfig = httpConfig,
                    context = context,
                    zOrderMediaOverlay = zOrderMediaOverlay,
                    showControls = showControls,
                    keepScreenOn = keepScreenOn,
                    playerCallBack = object : PlayerCallBack {
                        override fun showThumb(showThunb: Boolean) {
                            shouldShowThumb = showThunb
                        }

                        override fun setPlaying(isPlaying: Boolean) {
                            playing = isPlaying
                        }

                        override fun onprepare() {
                            mediaPrepared = true
                        }
                    }
                )
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
                                nativePlayerView.playWhenReady = autoPlay
                            }
                            Lifecycle.State.InActive -> {
                                isResume = false
                                updateState()
                                nativePlayerView.playWhenReady = false
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
            Column(
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
                        if (!isMostCenter && VideoPool.containsMiddleLine(videoKey, middleLine)) {
                            debounceJob?.cancel()
                            debounceJob = composableScope.launch {
                                delay(VideoPool.DEBOUNCE_DELAY)
                                if (VideoPool.containsMiddleLine(videoKey, middleLine)) {
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
                        if (isListItem) {
                            it.playWhenReady = autoPlay
                        }
                        it.resume()
                    } else {
                        if (isListItem) {
                            it.playWhenReady = false
                        }
                        it.pause()
                    }
                }
                if (mediaPrepared) {
                    Divider(Modifier.height(30.dp))
                    customControl?.invoke(nativePlayerView)
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

@Composable
internal fun getPlayInitial() = when (LocalVideoPlayback.current) {
    DisplayPreferences.AutoPlayback.Auto -> !LocalIsActiveNetworkMetered.current
    DisplayPreferences.AutoPlayback.Always -> true
    DisplayPreferences.AutoPlayback.Off -> false
}

interface PlayerCallBack {
    fun showThumb(showThunb: Boolean)
    fun setPlaying(isPlaying: Boolean)
    fun onprepare()
}

expect class NativePlayerView(
    url: String,
    autoPlay: Boolean,
    context: Any,
    httpConfig: Any,
    zOrderMediaOverlay: Boolean,
    showControls: Boolean,
    keepScreenOn: Boolean,
    playerCallBack: PlayerCallBack? = null
) {
    var player: Any
    var playWhenReady: Boolean
    fun resume()
    fun pause()
    fun contentPosition(): Long
    fun duration(): Long
    fun seekTo(time: Long)
    fun update()
    fun setVolume(volume: Float)
    fun release()
}

@Composable
expect fun PlatformView(
    modifier: Modifier,
    nativePLayerView: NativePlayerView,
    update: (NativePlayerView) -> Unit
)

@Composable
expect fun getContext(): Any

@Composable
expect fun httpConfig(): Any

object UserAvatarDefaults {
    val AvatarSize = 44.dp
}
