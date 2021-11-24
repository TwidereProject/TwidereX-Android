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
package com.twidere.twiderex.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import com.twidere.twiderex.component.foundation.DesktopMediaPlayer
import com.twidere.twiderex.component.foundation.DesktopMediaPlayerFactory
import com.twidere.twiderex.component.foundation.PlayerCallBack
import com.twidere.twiderex.component.foundation.PlayerProgressCallBack
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.embed.swing.JFXPanel
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.scene.media.MediaView
import javafx.util.Duration
import java.awt.BorderLayout
import javax.swing.JPanel

class JFXMediaPlayerFactory : DesktopMediaPlayerFactory {
    override fun create(url: String): DesktopMediaPlayer {
        return JFXMediaPlayer(url)
    }
}

class JFXMediaPlayer(url: String) : DesktopMediaPlayer {

    private var playerCallBack: PlayerCallBack? = null
    private var playerProgressCallBack: PlayerProgressCallBack? = null

    val mediaPlayer = MediaPlayer(Media(url))
        .apply {
            isAutoPlay = false
            setOnReady {
                playerCallBack?.onReady()
            }
            setOnPlaying {
                playerCallBack?.onIsPlayingChanged(true)
            }
            setOnPaused {
                playerCallBack?.onIsPlayingChanged(false)
            }
            setOnStopped {
                playerCallBack?.onIsPlayingChanged(false)
            }
            totalDurationProperty().addListener(object : ChangeListener<Duration> {
                override fun changed(
                    observable: ObservableValue<out Duration>?,
                    oldValue: Duration?,
                    newValue: Duration?
                ) {
                    newValue?.let { duraton ->
                        playerProgressCallBack?.onTimeChanged(duraton.toMillis().toLong())
                    }
                }
            })
        }

    override fun play() {
        mediaPlayer.play()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun stop() {
        mediaPlayer.stop()
    }

    override fun release() {
        mediaPlayer.dispose()
    }

    override fun setMute(mute: Boolean) {
        mediaPlayer.isMute = mute
    }

    override fun setVolume(volume: Float) {
        mediaPlayer.volume = volume.toDouble()
    }

    override fun seekTo(time: Long) {
        mediaPlayer.seek(Duration.millis(time.toDouble()))
    }

    override fun duration(): Long {
        return mediaPlayer.totalDuration.toMillis().toLong()
    }

    override fun currentPosition(): Long {
        return mediaPlayer.currentTime.toMillis().toLong()
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

    private val videoLayout = JPanel().apply {
        layout = BorderLayout()
    }

    @Composable
    override fun Content(modifier: Modifier, update: () -> Unit) {
        val videoPanel = JFXPanel()
        val mediaView = MediaView(mediaPlayer)
        val root = BorderPane(mediaView)
        val scene = Scene(root)
        // TODO Crash here
        videoPanel.scene = scene
        videoLayout.add(videoPanel, BorderLayout.CENTER)
        SwingPanel(
            factory = {
                videoLayout
            },
            modifier = modifier,
            update = {
                update.invoke()
            }
        )
    }
}
