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
import com.sun.media.jfxmedia.logging.Logger
import com.twidere.twiderex.component.foundation.DesktopMediaPlayer
import com.twidere.twiderex.component.foundation.PlayerCallBack
import com.twidere.twiderex.component.foundation.PlayerProgressCallBack
import com.twidere.twiderex.extensions.observeAsState
import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.scene.media.MediaView
import javafx.util.Duration
import kotlinx.coroutines.flow.MutableStateFlow
import java.awt.BorderLayout
import javax.swing.JPanel

class JFXMediaPlayer(private val url: String) : DesktopMediaPlayer {
    private var playerCallBack: PlayerCallBack? = null
    private var playerProgressCallBack: PlayerProgressCallBack? = null

    private var _mediaPlayerDelegate: MediaPlayer? = null

    private val mediaPlayer: MediaPlayer?
        get() {
            if (_mediaPlayerDelegate?.status == MediaPlayer.Status.DISPOSED) {
                initMediaPlayer(url)
            }
            return _mediaPlayerDelegate
        }
    private val videoLayout = JPanel().apply { layout = BorderLayout() }

    private val isUiReady = MutableStateFlow(false)
    private val isMediaReady = MutableStateFlow(false)

    init {
        Logger.setLevel(Logger.DEBUG)
        // Keep Platform thread alive so release can be executed
        Platform.setImplicitExit(false)
        try {
            // the reason why we init media player in Platform.runLater is because runLater keeps all
            // runnable in order, so we can ensure previous MediaPlayer already released
            Platform.runLater {
                initMediaPlayer(url)
            }
        } catch (e: Throwable) {
            // java.lang.IllegalStateException: Toolkit not initialized
            // the FX runtime is initialized when the first JFXPanel instance is constructed
            JFXPanel()
            Platform.runLater {
                initMediaPlayer(url)
            }
        }
    }

    private fun initMediaPlayer(url: String) {
        _mediaPlayerDelegate = MediaPlayer(Media(url))
            .apply {
                // loop video
                cycleCount = MediaPlayer.INDEFINITE
                setOnReady {
                    playerCallBack?.onReady()
                    isMediaReady.value = true
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
                setOnError {
                    println("error occurred:${mediaPlayer?.error}")
                }
                currentTimeProperty().addListener { _, _, newValue ->
                    newValue?.let { duration ->
                        playerProgressCallBack?.onTimeChanged(duration.toMillis().toLong())
                    }
                }
                playerCallBack?.onPrepareStart()
                initUiComponent()
                println("prepare for:$url")
            }
    }

    private fun initUiComponent() {
        val videoPanel = JFXPanel()
        Platform.runLater {
            val mediaView = MediaView(mediaPlayer)
            val root = BorderPane(mediaView)
            val scene = Scene(root)
            videoPanel.scene = scene
            videoLayout.add(videoPanel, BorderLayout.CENTER)
            mediaView.parent.layoutBoundsProperty().addListener { _, _, newValue ->
                // Update media view width
                if (mediaView.fitWidth != newValue.width) mediaView.fitWidth = newValue.width
                if (mediaView.fitHeight != newValue.height) mediaView.fitHeight = newValue.height
            }
            isUiReady.value = true
        }
    }

    override fun play() {
        mediaPlayer?.play()
        println("play for:$url")
    }

    override fun pause() {
        mediaPlayer?.pause()
        println("pause for:$url")
    }

    override fun stop() {
        if (mediaPlayer?.status == MediaPlayer.Status.PLAYING) {
            mediaPlayer?.stop()
            println("stop for:$url")
        }
    }

    override fun release() {
        // ensure resource released before create next media player
        Platform.runLater {
            mediaPlayer?.dispose()
            isUiReady.value = false
            isMediaReady.value = false
            videoLayout.removeAll()
            println("release for:$url")
        }
    }

    override fun setMute(mute: Boolean) {
        mediaPlayer?.isMute = mute
    }

    override fun setVolume(volume: Float) {
        mediaPlayer?.volume = volume.toDouble()
    }

    override fun seekTo(time: Long) {
        mediaPlayer?.seek(Duration.millis(time.toDouble()))
    }

    override fun duration(): Long {
        return if (mediaPlayer?.cycleCount == MediaPlayer.INDEFINITE) {
            mediaPlayer?.cycleDuration?.toMillis()
        } else {
            mediaPlayer?.totalDuration?.toMillis()
        }?.toLong() ?: 0
    }

    override fun currentPosition(): Long {
        return mediaPlayer?.currentTime?.toMillis()?.toLong() ?: 0
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
        val uiReady = isUiReady.observeAsState(false)
        val mediaReady = isMediaReady.observeAsState(false)
        println("show Content${uiReady.value} and${mediaReady.value}  for:$url")
        if (uiReady.value && mediaReady.value) {
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
}
