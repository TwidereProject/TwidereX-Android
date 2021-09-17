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
package com.twidere.twiderex.component.video

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.media.MediaEventAdapter
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import java.util.Locale

@Composable
actual fun VideoPlayerImpl(
    url: String,
    width: Int,
    height: Int,
    isPlaying: Boolean
) {
//    println("Video player for $url")
    NativeDiscovery().discover()
    // var resumed by remember(url) {
    //     mutableStateOf(false)
    // }
    val mediaPlayerComponent = remember(url) {
        // see https://github.com/caprica/vlcj/issues/887#issuecomment-503288294
        // for why we're using CallbackMediaPlayerComponent for macOS.
        if (isMacOS()) {
            CallbackMediaPlayerComponent()
        } else {
            EmbeddedMediaPlayerComponent()
        }
    }
    var windowInfo = LocalWindowInfo.current

    var windowState = rememberWindowState()

    LaunchedEffect(url) {
        while (true) {
            delay(1000)
            println(windowState.isMinimized)
            println(windowInfo.isWindowFocused)
        }
    }
//    rememberWindowState.

//    Local

//    val windowFocus = Listener
//    current.isWindowFocused
    var middleLine = 0.0f
    val composableScope = rememberCoroutineScope()

    val videoKey = remember {
        url + System.nanoTime()
    }

    var isMostCenter by remember(url) {
        mutableStateOf(false)
    }
    DisposableEffect(url) {
        val listener = object : MediaPlayerEventAdapter() {
            override fun positionChanged(mediaPlayer: MediaPlayer?, newPosition: Float) {
                super.positionChanged(mediaPlayer, newPosition)
            }

            override fun playing(mediaPlayer: MediaPlayer?) {
                super.playing(mediaPlayer)
            }

            override fun mediaPlayerReady(mediaPlayer: MediaPlayer?) {
                super.mediaPlayerReady(mediaPlayer)
            }
        }
        mediaPlayerComponent.mediaPlayer().events()
            .addMediaEventListener(object : MediaEventAdapter() {
            })
        mediaPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(listener)
        mediaPlayerComponent.mediaPlayer().media().prepare(url)
        onDispose {
            mediaPlayerComponent.mediaPlayer().events().removeMediaPlayerEventListener(listener)
            mediaPlayerComponent.mediaPlayer().release()
        }
    }

    var debounceJob: Job? = remember {
        null
    }

    return Box(
        modifier = Modifier.width(500.dp).height(500.dp).onGloballyPositioned { coordinates ->
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
            val player = it.mediaPlayer()
            val controls = player.controls()
            if (isPlaying && isMostCenter && !windowState.isMinimized) {
                controls.play()
            } else {
                controls.setPause(true)
            }
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
