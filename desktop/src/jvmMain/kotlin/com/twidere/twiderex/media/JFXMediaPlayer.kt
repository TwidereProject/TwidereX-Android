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
package com.twidere.twiderex.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import com.sun.javafx.application.PlatformImpl
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.awt.BorderLayout
import java.awt.event.MouseEvent
import javax.swing.JPanel
import javax.swing.event.MouseInputAdapter

class JFXMediaPlayer(
    private val url: String,
    private val backgroundColor: androidx.compose.ui.graphics.Color?,
    private val onClick: (() -> Unit)?
) : DesktopMediaPlayer {
    private val scope = CoroutineScope(Dispatchers.IO)
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
            // JFXPanel() // may cause other crashes on windows
            PlatformImpl.startup {}
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
                setOnStalled {
                    playerCallBack?.onBuffering()
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
            }
    }

    private fun initUiComponent() {
        val videoPanel = JFXPanel().apply {
            addMouseListener(object : MouseInputAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    super.mouseClicked(e)
                    onClick?.invoke()
                }
            })
        }
        Platform.runLater {
            val mediaView = MediaView(mediaPlayer)
            val root = BorderPane(mediaView)
            val scene = backgroundColor?.let {
                Scene(root, javafx.scene.paint.Color(it.red.toDouble(), it.green.toDouble(), it.blue.toDouble(), it.alpha.toDouble()))
            } ?: Scene(root)
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
    }

    override fun pause() {
        mediaPlayer?.pause()
    }

    override fun stop() {
        if (mediaPlayer?.status == MediaPlayer.Status.PLAYING) {
            mediaPlayer?.stop()
        }
    }

    override fun release() {
        // ensure resource released before create next media player
        Platform.runLater {
            mediaPlayer?.dispose()
            isUiReady.value = false
            isMediaReady.value = false
            scope.launch {
                videoLayout.removeAll()
            }
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
        if (uiReady.value && mediaReady.value) {
            // FIXME 2021.11.26 SwingPanel will cover all compose layout https://github.com/JetBrains/compose-jb/issues/1449
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
