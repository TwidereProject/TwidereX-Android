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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import com.twidere.twiderex.utils.video.VideoPool
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import java.util.Locale

class VLCJMediaPlayer(
    private val url: String,
) : DesktopMediaPlayer {
    private var playerProgressCallBack: PlayerProgressCallBack? = null
    private var playerCallBack: PlayerCallBack? = null
    private var currentVolume: Float = 1f
    private var isMediaReady = mutableStateOf(false)

    private val desktopPlayer = (
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
                    isMediaReady.value = true
                    playerCallBack?.onReady()
                }

                override fun timeChanged(mediaPlayer: MediaPlayer?, newTime: Long) {
                    super.timeChanged(mediaPlayer, newTime)
                    playerProgressCallBack?.onTimeChanged(newTime)
                }

                override fun error(mediaPlayer: MediaPlayer?) {
                    super.error(mediaPlayer)
                }

                override fun volumeChanged(mediaPlayer: MediaPlayer?, volume: Float) {
                    // set volume might only works when video is playing
                    // while playing callback is not accurate, and when video start to playing
                    // the player automatically set volume to 1.0
                    if (volume != currentVolume) mediaPlayer?.let {
                        setVolume(currentVolume, it)
                    }
                }
            })
            playerCallBack?.onPrepareStart()
            media().prepare(url)
        }
    }
    override fun play() {
        desktopPlayer.mediaPlayer().controls()?.play()
    }

    override fun pause() {
        desktopPlayer.mediaPlayer().controls()?.pause()
    }

    override fun stop() {
        desktopPlayer.mediaPlayer().controls()?.stop()
    }

    override fun release() {
        desktopPlayer.mediaPlayer().release()
    }

    override fun setMute(mute: Boolean) {
        desktopPlayer.mediaPlayer().audio().isMute = mute
    }

    override fun setVolume(volume: Float) {
        this.currentVolume = volume
        setVolume(currentVolume, desktopPlayer.mediaPlayer())
    }

    private fun setVolume(volume: Float, mediaPlayer: MediaPlayer) {
        mediaPlayer.audio().setVolume(
            // range 0-100
            (volume * 100).toInt()
        )
    }

    override fun seekTo(time: Long) {
        desktopPlayer.mediaPlayer().controls().setTime(time)
    }

    override fun duration(): Long {
        return desktopPlayer.mediaPlayer().media().info().duration()
    }

    override fun currentPosition(): Long {
        return desktopPlayer.mediaPlayer().status().time()
    }

    override fun registerPlayerCallback(callBack: PlayerCallBack) {
        playerCallBack = callBack
    }

    override fun registerProgressCallback(callBack: PlayerProgressCallBack) {
        playerProgressCallBack = callBack
    }

    override fun removePlayerCallback(callback: PlayerCallBack) {
        playerCallBack = null
    }

    override fun removeProgressCallback(callback: PlayerProgressCallBack) {
        playerProgressCallBack = null
    }

    @Composable
    override fun Content(modifier: Modifier, update: () -> Unit) {
        if (isMediaReady.value) {
            SwingPanel(
                factory = {
                    if (isMacOS()) {
                        desktopPlayer as CallbackMediaPlayerComponent
                    } else {
                        desktopPlayer as EmbeddedMediaPlayerComponent
                    }
                },
                modifier = modifier,
                update = {
                    update.invoke()
                }
            )
        }
    }
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
