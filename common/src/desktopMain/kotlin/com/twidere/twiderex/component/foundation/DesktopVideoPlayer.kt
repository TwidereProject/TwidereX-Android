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

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import com.twidere.twiderex.utils.video.VideoPool
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moe.tlaster.precompose.lifecycle.Lifecycle
import moe.tlaster.precompose.lifecycle.LifecycleObserver
import moe.tlaster.precompose.ui.LocalLifecycleOwner
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import java.util.Locale

@Composable
actual fun VideoPlayerImpl(
    modifier: Modifier,
    url: String,
    volume: Float,
    customControl: Any?,
    showControls: Boolean,
    zOrderMediaOverlay: Boolean,
    keepScreenOn: Boolean,
    isListItem: Boolean,
    thumb: @Composable (() -> Unit)?
) {
    NativeDiscovery().discover()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var active by remember(url) {
        mutableStateOf(true)
    }
    val mediaPlayerComponent = remember(url) {
        // see https://github.com/caprica/vlcj/issues/887#issuecomment-503288294
        // for why we're using CallbackMediaPlayerComponent for macOS.
        if (isMacOS()) {
            CallbackMediaPlayerComponent()
        } else {
            EmbeddedMediaPlayerComponent()
        }
    }

    var middleLine = 0.0f
    val composableScope = rememberCoroutineScope()

    val videoKey = remember {
        url + System.nanoTime()
    }

    var isMostCenter by remember(url) {
        mutableStateOf(false)
    }
    DisposableEffect(url) {

        mediaPlayerComponent.mediaPlayer().media().prepare(url)

        val lifecycleObserver = object : LifecycleObserver {
            override fun onStateChanged(state: Lifecycle.State) {
                when (state) {
                    Lifecycle.State.Active -> {
                        active = true
                    }
                    Lifecycle.State.InActive -> {
                        active = false
                    }
                    else -> {}
                }
            }
        }
        lifecycle.addObserver(lifecycleObserver)

        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
            mediaPlayerComponent.mediaPlayer().release()
        }
    }

    var debounceJob: Job? = remember {
        null
    }

    return Box(
        modifier = modifier.onGloballyPositioned { coordinates ->
            if (middleLine == 0.0f) {
                var rootCoordinates = coordinates
                while (rootCoordinates.parentCoordinates != null) {
                    rootCoordinates = rootCoordinates.parentCoordinates!!
                }
                rootCoordinates.boundsInWindow().run {
                    middleLine = (top + bottom) / 2
                }
            }
            coordinates.boundsInWindow().run {
                VideoPool.setRect(videoKey, this)
                if (!isMostCenter && VideoPool.containsMiddleLine(videoKey, middleLine)) {
                    debounceJob?.cancel()
                    debounceJob = composableScope.launch {
                        delay(VideoPool.DEBOUNCE_DELAY)
                        if (VideoPool.containsMiddleLine(videoKey, middleLine)) {
                            isMostCenter = true
                        }
                    }
                } else if (isMostCenter && !VideoPool.isMostCenter(videoKey, middleLine)) {
                    isMostCenter = false
                }
            }
        },
    ) {
        SwingPanel(
            background = Color.Transparent,
            factory = {
                mediaPlayerComponent
            }
        ) {
            val controls = it.mediaPlayer().controls()
            if (isMostCenter && active) {
                controls.play()
            } else {
                controls.setPause(true)
            }
        }
    }
}

actual class NativePlayer {
    actual var player: Any? = null
        get() = TODO("Not yet implemented")
    actual var playWhenReady: Boolean = false
        get() = TODO("Not yet implemented")

    actual fun contentPosition(): Long {
        TODO("Not yet implemented")
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
        get() = TODO("Not yet implemented")
    actual var player: NativePlayer? = null
        get() = TODO("Not yet implemented")

    actual fun resume(): Unit? {
        TODO("Not yet implemented")
    }

    actual fun pause(): Unit? {
        TODO("Not yet implemented")
    }
}

actual fun nativeViewFactory(
    zOrderMediaOverlay: Boolean,
    showControls: Boolean,
    keepScreenOn: Boolean,
    context: Any
): NativePlayerView {
    TODO("Not yet implemented")
}

actual fun getNativePlayer(
    url: String,
    autoPlay: Boolean,
    context: Any,
    httpConfig: Any,
    setShowThumb: (Boolean) -> Unit,
    setPLaying: (Boolean) -> Unit
): NativePlayer {
    TODO("Not yet implemented")
}

@Composable
actual fun PlatformView(
    zOrderMediaOverlay: Boolean,
    showControls: Boolean,
    keepScreenOn: Boolean,
    modifier: Modifier,
    update: (NativePlayerView) -> Unit
) {
}

@Composable
actual fun getContext(): Any {
    TODO("Not yet implemented")
}

@Composable
actual fun httpConfig(): Any {
    TODO("Not yet implemented")
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
