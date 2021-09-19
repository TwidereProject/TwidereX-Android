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
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.MediaPlayerComponent
import java.util.Locale

actual class NativePlayerView  {

    actual var player: Any?= null

    actual var playWhenReady: Boolean = false

    private fun realPlayerView() = player as? MediaPlayerComponent

    actual fun resume() {
        realPlayerView()?.mediaPlayer()?.controls()?.play()
    }

    actual fun pause() {
        realPlayerView()?.mediaPlayer()?.controls()?.pause()
    }

    actual fun contentPosition(): Long = 0L

    actual fun update() {
    }

    actual fun setVolume(volume: Float) {
    }

    actual fun release() {
        realPlayerView()?.mediaPlayer()?.release()
    }
}

actual fun getNativePlayerView(
    url: String,
    autoPlay: Boolean,
    context: Any,
    httpConfig: Any,
    zOrderMediaOverlay: Boolean,
    showControls: Boolean,
    keepScreenOn: Boolean,
    setShowThumb: (Boolean) -> Unit,
    setPLaying: (Boolean) -> Unit,
): NativePlayerView {
    return NativePlayerView().apply {
        player = (
            if (isMacOS()) {
                CallbackMediaPlayerComponent()
            } else {
                EmbeddedMediaPlayerComponent()
            }
            ).apply {
            mediaPlayer().apply {
                media().prepare(url)
            }
        }
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
                nativePLayerView.player as CallbackMediaPlayerComponent
            } else {
                nativePLayerView.player as EmbeddedMediaPlayerComponent
            }
        },
        modifier = modifier,
        update = {
            update.invoke(nativePLayerView)
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
