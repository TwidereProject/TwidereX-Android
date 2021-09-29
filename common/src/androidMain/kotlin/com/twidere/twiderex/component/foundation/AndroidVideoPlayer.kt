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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.twidere.services.http.config.HttpConfig
import com.twidere.twiderex.http.TwidereServiceFactory
import com.twidere.twiderex.preferences.LocalHttpConfig
import com.twidere.twiderex.utils.video.CacheDataSourceFactory
import com.twidere.twiderex.utils.video.VideoPool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
actual fun PlatformView(
    modifier: Modifier,
    nativePLayerView: NativePlayerView,
    update: (NativePlayerView) -> Unit
) {
    AndroidView(
        factory = {
            nativePLayerView.player as View
        },
        modifier = modifier,
        update = {
            update.invoke(nativePLayerView)
        }
    )
}

actual class NativePlayerView actual constructor(
    url: String,
    autoPlay: Boolean,
    context: Any,
    httpConfig: Any,
    zOrderMediaOverlay: Boolean,
    showControls: Boolean,
    keepScreenOn: Boolean,
) {
    actual var playerCallBack: PlayerCallBack? = null

    actual var playerProgressCallBack: PlayerProgressCallBack? = null

    private var job: Job? = null

    private val scope = CoroutineScope(Dispatchers.Main)

    actual var player: Any = StyledPlayerView(context as Context).also { playerView ->
        (playerView.videoSurfaceView as? SurfaceView)?.setZOrderMediaOverlay(zOrderMediaOverlay)
        playerView.useController = showControls
        playerView.keepScreenOn = keepScreenOn
    }.apply {
        player = RemainingTimeExoPlayer(
            SimpleExoPlayer.Builder(context)
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
                    playerCallBack?.showThumb(state != Player.STATE_READY)
                    if (state == Player.STATE_READY) {
                        playerCallBack?.onprepare()
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    playerCallBack?.setPlaying(isPlaying)
                    job?.cancel()
                    if (isPlaying) {
                        job = scope.launch {
                            while (true) {
                                delay(1000)
                                playerProgressCallBack?.onTimeChanged(contentPosition())
                            }
                        }
                    }
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

    private fun realPlayerView() = player as StyledPlayerView

    actual var playWhenReady: Boolean = false
        set(value) {
            realPlayerView().player?.playWhenReady = value
            field = value
        }

    actual fun resume() {
        realPlayerView().onResume()
    }

    actual fun pause() {
        realPlayerView().onPause()
    }

    actual fun contentPosition(): Long = 0L.coerceAtLeast((realPlayerView().player?.currentPosition) ?: 0)

    actual fun update() {
    }

    actual fun setVolume(volume: Float) {
        realPlayerView().player?.volume = volume
    }

    actual fun release() {
        job?.cancel()
        playerCallBack = null
        playerProgressCallBack = null
        realPlayerView().player?.release()
    }

    actual fun duration(): Long = realPlayerView().player?.duration ?: 0
    actual fun seekTo(time: Long) {
        realPlayerView().player?.seekTo(time)
    }

    actual fun setMute(mute: Boolean) {
        realPlayerView().player?.volume = if (mute) 0f else 1f
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
