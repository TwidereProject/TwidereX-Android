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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.twidere.services.http.config.HttpConfig
import com.twidere.twiderex.http.TwidereServiceFactory
import com.twidere.twiderex.preferences.LocalHttpConfig
import com.twidere.twiderex.utils.video.CacheDataSourceFactory
import com.twidere.twiderex.utils.video.VideoPool

@Composable
actual fun PlatformView(
    zOrderMediaOverlay: Boolean,
    showControls: Boolean,
    keepScreenOn: Boolean,
    modifier: Modifier,
    update: (NativePlayerView) -> Unit
) {
    val context = LocalContext.current
    val nativePlayer = remember {
        nativeViewFactory(
            zOrderMediaOverlay,
            showControls,
            keepScreenOn,
            context
        )
    }
    AndroidView(
        factory = {
            nativePlayer.playerView as View
        },
        modifier = modifier,
        update = {
            update.invoke(nativePlayer)
        }
    )
}


actual class NativePlayerView actual constructor() {
    actual var playerView: Any?= null
    actual var player: NativePlayer?= null

    private fun realPlayerView() = playerView as? StyledPlayerView

    actual fun resume() = realPlayerView()?.onResume()

    actual fun pause() = realPlayerView()?.onPause()
}

actual class  NativePlayer {
    actual var player: Any?= null

    fun realPlayer() = player as? RemainingTimeExoPlayer

    actual var playWhenReady: Boolean
        get() = realPlayer()?.playWhenReady ?: false
        set(value) {
            realPlayer()?.playWhenReady = value
        }

    actual fun contentPosition(): Long = realPlayer()?.contentPosition?:0L

    actual fun setCustomControl(customControl: Any?) {
        (customControl as? PlayerControlView)?.player = realPlayer()
    }

    actual fun resume() {
        // getRealPlayer()?.res
    }

    actual fun pause() {
        realPlayer()?.pause()
    }

    actual fun update() {

    }

    actual fun setVolume(volume: Float) {
        realPlayer()?.volume = volume
    }

    actual fun release() {
        realPlayer()?.release()
    }
}

actual fun nativeViewFactory(
    zOrderMediaOverlay: Boolean,
    showControls: Boolean,
    keepScreenOn: Boolean,
    context: Any
): NativePlayerView {
    return NativePlayerView().apply {
        playerView = StyledPlayerView(context as Context).also { playerView ->
            (playerView.videoSurfaceView as? SurfaceView)?.setZOrderMediaOverlay(zOrderMediaOverlay)
            playerView.useController = showControls
            playerView.keepScreenOn = keepScreenOn
        }
    }
}

@Composable
actual fun getContext(): Any {
    return LocalContext.current
}

@Composable
actual fun httpConfig(): Any {
    return LocalHttpConfig.current
}

actual fun getNativePlayer(
    url: String,
    autoPlay: Boolean,
    context: Any,
    httpConfig: Any,
    setShowThumb: (Boolean) -> Unit,
    setPLaying: (Boolean) -> Unit,
): NativePlayer {
    return NativePlayer().apply {
        player = RemainingTimeExoPlayer(
            SimpleExoPlayer.Builder(context as Context)
                .apply {
                    if ((httpConfig as HttpConfig).proxyConfig.enable) {
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
}
