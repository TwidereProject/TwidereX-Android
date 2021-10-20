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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import com.twidere.services.http.config.HttpConfig
import com.twidere.twiderex.utils.video.VideoPool
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import java.util.Locale

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

    var desktopPlayer = (
        if (isMacOS()) {
            CallbackMediaPlayerComponent()
        } else {
            EmbeddedMediaPlayerComponent()
        }
        ).apply {
        mediaPlayer().apply {
            events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
                override fun opening(mediaPlayer: MediaPlayer?) {
                    super.opening(mediaPlayer)
                    mediaPlayer?.controls()?.skipTime(VideoPool.get(url))
                }

                override fun mediaPlayerReady(mediaPlayer: MediaPlayer?) {
                    super.mediaPlayerReady(mediaPlayer)
                    playerCallBack?.onprepare()
                }

                override fun timeChanged(mediaPlayer: MediaPlayer?, newTime: Long) {
                    super.timeChanged(mediaPlayer, newTime)
                    playerProgressCallBack?.onTimeChanged(newTime)
                }
            })
            media().prepare(url)
        }
    }

    actual var playWhenReady: Boolean = false

    actual fun resume() {
        desktopPlayer.mediaPlayer().controls()?.play()
    }

    actual fun pause() {
        desktopPlayer.mediaPlayer().controls()?.pause()
    }

    actual fun contentPosition(): Long = desktopPlayer.mediaPlayer().status().time()

    actual fun setVolume(volume: Float) {
    }

    actual fun release() {
        playerCallBack = null
        playerProgressCallBack = null
        desktopPlayer.mediaPlayer().release()
    }

    // only can get this value after prepare
    actual fun duration(): Long = desktopPlayer.mediaPlayer().media().info().duration()

    actual fun seekTo(time: Long) {
        desktopPlayer.mediaPlayer().controls().setTime(time)
    }

    actual fun setMute(mute: Boolean) {
        desktopPlayer.mediaPlayer().audio().isMute = mute
    }
}

@Composable
actual fun PlatformView(
    modifier: Modifier,
    nativePLayerView: NativePlayerView,
    update: (NativePlayerView) -> Unit
) {
    SwingPanel(
        factory = {
            if (isMacOS()) {
                nativePLayerView.desktopPlayer as CallbackMediaPlayerComponent
            } else {
                nativePLayerView.desktopPlayer as EmbeddedMediaPlayerComponent
            }
        },
        modifier = modifier,
        update = {
            update.invoke(nativePLayerView)
        }
    )
}

/**
 * To return mediaPlayer from player components.
 * The method names are same, but they don't share the same parent/interface.
 * That's why need this method.
 */
private fun Any.mediaPlayer(): MediaPlayer {
    return when (this) {
        is CallbackMediaPlayerComponent -> mediaPlayer()
        is EmbeddedMediaPlayerComponent -> mediaPlayer()
        else -> throw IllegalArgumentException("You can only call mediaPlayer() on vlcj player component")
    }
}

private fun isMacOS(): Boolean {
    val os = System.getProperty("os.name", "generic").lowercase(Locale.ENGLISH)
    return os.indexOf("mac") >= 0 || os.indexOf("darwin") >= 0
}
