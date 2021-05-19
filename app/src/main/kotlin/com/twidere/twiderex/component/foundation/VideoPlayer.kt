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

import android.view.SurfaceView
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.twidere.twiderex.R
import com.twidere.twiderex.component.status.UserAvatarDefaults
import com.twidere.twiderex.preferences.proto.DisplayPreferences
import com.twidere.twiderex.ui.LocalIsActiveNetworkMetered
import com.twidere.twiderex.ui.LocalVideoPlayback
import com.twidere.twiderex.utils.video.CacheDataSourceFactory
import com.twidere.twiderex.utils.video.VideoPool

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    url: String,
    volume: Float = 1f,
    customControl: PlayerControlView? = null,
    showControls: Boolean = customControl == null,
    zOrderMediaOverlay: Boolean = false,
    thumb: @Composable (() -> Unit)? = null,
) {
    var playing by remember { mutableStateOf(false) }
    val playBackMode = LocalVideoPlayback.current
    val isActiveNetworkMetered = LocalIsActiveNetworkMetered.current
    var shouldShowThumb by remember { mutableStateOf(false) }
    val playInitial = when (playBackMode) {
        DisplayPreferences.AutoPlayback.Auto -> !isActiveNetworkMetered
        DisplayPreferences.AutoPlayback.Always -> true
        DisplayPreferences.AutoPlayback.Off -> false
        DisplayPreferences.AutoPlayback.UNRECOGNIZED -> true
    }
    var autoPlay by remember(url) { mutableStateOf(playInitial) }
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    Box {
        if (playInitial) {
            val player = remember(url) {
                SimpleExoPlayer.Builder(context).build().apply {
                    repeatMode = Player.REPEAT_MODE_ALL
                    playWhenReady = autoPlay
                    addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(state: Int) {
                            shouldShowThumb = state != Player.STATE_READY
                        }

                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                            playing = isPlaying
                        }
                    })

                    setVolume(volume)
                    ProgressiveMediaSource.Factory(
                        CacheDataSourceFactory(
                            context,
                            5 * 1024 * 1024,
                        )
                    ).createMediaSource(MediaItem.fromUri(url)).also {
                        setMediaSource(it)
                    }
                    prepare()
                    seekTo(VideoPool.get(url))
                }
            }

            fun updateState() {
                autoPlay = player.playWhenReady
                VideoPool.set(url, 0L.coerceAtLeast(player.contentPosition))
            }

            LaunchedEffect(customControl) {
                if (customControl != null) {
                    customControl.player = player
                }
            }

            DisposableEffect(Unit) {
                onDispose {
                    updateState()
                    player.release()
                }
            }

            AndroidView(
                modifier = modifier,
                factory = { context ->
                    StyledPlayerView(context).also { playerView ->
                        playerView.videoSurfaceView?.let {
                            if (it is SurfaceView) it.setZOrderMediaOverlay(zOrderMediaOverlay)
                        }
                        playerView.useController = showControls
                        lifecycle.addObserver(object : LifecycleObserver {
                            @OnLifecycleEvent(Lifecycle.Event.ON_START)
                            fun onStart() {
                                playerView.onResume()
                                player.playWhenReady = autoPlay
                            }

                            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
                            fun onStop() {
                                updateState()
                                playerView.onPause()
                                player.playWhenReady = false
                            }
                        })
                    }
                }
            ) {
                it.player = player
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
                    contentDescription = stringResource(id = R.string.accessibility_common_video_play)
                )
            }
        }
    }
}
