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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.MediaPlayerComponent
import java.awt.Component
import java.util.Locale

actual class NativePlayer {
    actual var player: Any? = null

    actual var playWhenReady: Boolean = false

    actual fun contentPosition(): Long {
        return 0L
    }

    actual fun setCustomControl(customControl: Any?) {
    }

    actual fun resume() {
    }

    actual fun pause() {
    }

    actual fun update() {
    }

    actual fun setVolume(volume: Float) {
    }

    actual fun release() {
    }
}

actual class NativePlayerView actual constructor() {
    actual var playerView: Any? = null
    actual var player: NativePlayer? = null

    private fun realPlayerView() = playerView as? MediaPlayerComponent

    actual fun resume() = realPlayerView()?.mediaPlayer()?.controls()?.play()

    actual fun pause() = realPlayerView()?.mediaPlayer()?.controls()?.pause()
}

actual fun nativeViewFactory(
    zOrderMediaOverlay: Boolean,
    showControls: Boolean,
    keepScreenOn: Boolean,
    context: Any,
    player: Any?,
): NativePlayerView {
    return NativePlayerView().apply {
        playerView = player
    }
}

actual fun getNativePlayer(
    url: String,
    autoPlay: Boolean,
    context: Any,
    httpConfig: Any,
    setShowThumb: (Boolean) -> Unit,
    setPLaying: (Boolean) -> Unit
): NativePlayer {
    return NativePlayer().apply {
        player = if (isMacOS()) {
            CallbackMediaPlayerComponent()
        } else {
            EmbeddedMediaPlayerComponent()
        }
    }
}

@Composable
actual fun PlatformView(
    zOrderMediaOverlay: Boolean,
    showControls: Boolean,
    keepScreenOn: Boolean,
    modifier: Modifier,
    player: Any?,
    update: (NativePlayerView) -> Unit
) {
    val nativePlayer = remember {
        nativeViewFactory(
            zOrderMediaOverlay,
            showControls,
            keepScreenOn,
            Any(),
            player
        )
    }
    SwingPanel(
        factory = {
            nativePlayer.playerView as CallbackMediaPlayerComponent
        },
        modifier = modifier,
        update = {
            update.invoke(nativePlayer)
        }
    )
}

@Composable
actual fun getContext(): Any {
    return Any()
}

@Composable
actual fun httpConfig(): Any {
    return Any()
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
