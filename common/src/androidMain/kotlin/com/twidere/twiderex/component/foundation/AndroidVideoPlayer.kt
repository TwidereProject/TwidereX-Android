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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.http.TwidereServiceFactory
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
            nativePLayerView.androidPlayer
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
    httpConfig: HttpConfig,
    zOrderMediaOverlay: Boolean,
    showControls: Boolean,
    keepScreenOn: Boolean,
) {
    actual var playerCallBack: PlayerCallBack? = null

    actual var playerProgressCallBack: PlayerProgressCallBack? = null

    private var job: Job? = null

    private val scope = CoroutineScope(Dispatchers.Main)

    private val context = get<Context>()

    var androidPlayer = StyledPlayerView(context).also { playerView ->
        (playerView.videoSurfaceView as? SurfaceView)?.setZOrderMediaOverlay(zOrderMediaOverlay)
        playerView.useController = showControls
        playerView.keepScreenOn = keepScreenOn
    }.apply {
        player = RemainingTimeExoPlayer(
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

    actual var playWhenReady: Boolean = false
        set(value) {
            androidPlayer.player?.playWhenReady = value && enablePlaying
            field = value
        }

    actual fun resume() {
        androidPlayer.onResume()
    }

    actual fun pause() {
        androidPlayer.onPause()
    }

    actual fun contentPosition(): Long = 0L.coerceAtLeast((androidPlayer.player?.currentPosition) ?: 0)

    actual fun setVolume(volume: Float) {
        androidPlayer.player?.volume = volume
    }

    actual fun release() {
        job?.cancel()
        playerCallBack = null
        playerProgressCallBack = null
        androidPlayer.player?.release()
    }

    actual fun duration(): Long = androidPlayer.player?.duration ?: 0
    actual fun seekTo(time: Long) {
        androidPlayer.player?.seekTo(time)
    }

    actual fun setMute(mute: Boolean) {
        androidPlayer.player?.volume = if (mute) 0f else 1f
    }

    actual var enablePlaying: Boolean = true
        set(value) {
            field = value
            playWhenReady = playWhenReady
        }
}
