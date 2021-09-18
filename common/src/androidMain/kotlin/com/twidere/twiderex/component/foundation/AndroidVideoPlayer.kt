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

import android.content.Context
import android.view.SurfaceView
import android.view.View
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.NoOpUpdate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.twidere.twiderex.MR.strings.accessibility_common_video_play
import com.twidere.twiderex.compose.LocalResLoader
import com.twidere.twiderex.http.TwidereServiceFactory
import com.twidere.twiderex.preferences.LocalHttpConfig
import com.twidere.twiderex.preferences.model.DisplayPreferences
import com.twidere.twiderex.ui.LocalIsActiveNetworkMetered
import com.twidere.twiderex.ui.LocalVideoPlayback
import com.twidere.twiderex.utils.video.CacheDataSourceFactory
import com.twidere.twiderex.utils.video.VideoPool
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
actual fun VideoPlayerImpl(
    modifier: Modifier,
    url: String,
    volume: Float,
    customControl: Any?,
    showControls: Boolean,
    zOrderMediaOverlay: Boolean,
    keepScreenOn: Boolean,
    isListItem: Boolean,
    thumb: @Composable (() -> Unit)?
) {
    var playing by remember { mutableStateOf(false) }
    var shouldShowThumb by remember { mutableStateOf(false) }
    val playInitial = getPlayInitial()
    var autoPlay by remember(url) { mutableStateOf(playInitial) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val resLoder = LocalResLoader.current
    Box {
        if (playInitial) {
            val player = remember(url) {
                getPlater(
                    url = url,
                    autoPlay = autoPlay,
                    setShowThumb = {
                        shouldShowThumb = it
                    },
                    setPLaying = {
                        playing = it
                    }
                )
            }
            player.volume = volume

            fun updateState() {
                autoPlay = player.playWhenReady
                VideoPool.set(url, 0L.coerceAtLeast(player.contentPosition))
            }

            LaunchedEffect(customControl) {
                (customControl as? PlayerControlView)?.player = player
            }
            var isResume by remember {
                mutableStateOf(true)
            }
            val videoKey by remember {
                mutableStateOf(url + System.currentTimeMillis())
            }
            DisposableEffect(Unit) {
                val observer = object : LifecycleObserver {
                    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
                    fun onResume() {
                        isResume = true
                        player.playWhenReady = autoPlay
                    }
                    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                    fun onPause() {
                        isResume = false
                        updateState()
                        player.playWhenReady = false
                    }
                }
                lifecycle.addObserver(observer)
                onDispose {
                    updateState()
                    player.release()
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
            PlatformView(
                zOrderMediaOverlay = zOrderMediaOverlay,
                showControls = showControls,
                keepScreenOn = keepScreenOn,
                modifier = modifier.onGloballyPositioned { coordinates ->
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
                },
            ) {
                it.player = player
                if (isResume && isMostCenter) {
                    if (isListItem) {
                        player.playWhenReady = autoPlay
                    }
                    it.resume()
                } else {
                    if (isListItem) {
                        player.playWhenReady = false
                    }
                    it.pause()
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
                    contentDescription = resLoder.getString(accessibility_common_video_play)
                )
            }
        }
    }
}

@Composable
fun PlatformView(
    zOrderMediaOverlay: Boolean,
    showControls: Boolean,
    keepScreenOn: Boolean,
    modifier: Modifier = Modifier,
    update: (NativePlayer) -> Unit = {}
) {
    val nativePlayer = remember {
        nativeViewFactory(
            zOrderMediaOverlay,
            showControls,
            keepScreenOn
        )
    }
    AndroidView(
        factory = {
            nativePlayer.player as View
        },
        modifier = modifier,
        update = {
            update.invoke(nativePlayer)
        }
    )
}

class  NativePlayer {
    var player: Any?= null

    fun setP(p: Any) {

    }

    fun resume() {

    }
    fun pause() {

    }

    fun update() {

    }
}

fun nativeViewFactory(
    zOrderMediaOverlay: Boolean,
    showControls: Boolean,
    keepScreenOn: Boolean
): NativePlayer {
    val context = LocalContext.current
    return NativePlayer().apply {
        player = StyledPlayerView(context).also { playerView ->
            (playerView.videoSurfaceView as? SurfaceView)?.setZOrderMediaOverlay(zOrderMediaOverlay)
            playerView.useController = showControls
            playerView.keepScreenOn = keepScreenOn
        }
    }
}

fun getPlater(
    url: String,
    autoPlay: Boolean,
    setShowThumb: (Boolean) -> Unit,
    setPLaying: (Boolean) -> Unit,
): RemainingTimeExoPlayer {
    val context = LocalContext.current
    val httpConfig = LocalHttpConfig.current
    return RemainingTimeExoPlayer(
        SimpleExoPlayer.Builder(context)
            .apply {
                if (httpConfig.proxyConfig.enable) {
                    // replace DataSource
                    OkHttpDataSource.Factory(
                        TwidereServiceFactory
                            .createHttpClientFactory()
                            .createHttpClientBuilder()
                            .build()
                    )
                        .let {
                            DefaultDataSourceFactory(context, it)
                        }.let {
                            DefaultMediaSourceFactory(it)
                        }.let {
                            setMediaSourceFactory(it)
                        }
                }
            }
    ).apply {
        repeatMode = Player.REPEAT_MODE_ALL
        playWhenReady = autoPlay
        addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                setShowThumb(state != Player.STATE_READY)
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                setPLaying(isPlaying)
            }
        })

        ProgressiveMediaSource.Factory(
            CacheDataSourceFactory(
                context,
                5L * 1024L * 1024L,
            )
        ).createMediaSource(MediaItem.fromUri(url)).also {
            setMediaSource(it)
        }
        prepare()
        seekTo(VideoPool.get(url))
    }
}

object UserAvatarDefaults {
    val AvatarSize = 44.dp
}
