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
import com.twidere.twiderex.preferences.model.DisplayPreferences
import com.twidere.twiderex.ui.LocalIsActiveNetworkMetered
import com.twidere.twiderex.ui.LocalVideoPlayback

expect fun VideoPlayerImpl(
    modifier: Modifier = Modifier,
    url: String,
    volume: Float = 1f,
    customControl: Any? = null,
    showControls: Boolean = customControl == null,
    zOrderMediaOverlay: Boolean = false,
    keepScreenOn: Boolean = false,
    isListItem: Boolean = true,
    thumb: @Composable (() -> Unit)? = null,
)

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    url: String,
    volume: Float = 1f,
    customControl: Any? = null,
    showControls: Boolean = customControl == null,
    zOrderMediaOverlay: Boolean = false,
    keepScreenOn: Boolean = false,
    isListItem: Boolean = true,
    thumb: @Composable (() -> Unit)? = null,
) {
    VideoPlayerImpl(
        modifier = modifier,
        url = url,
        volume = volume,
        customControl = customControl,
        showControls = showControls,
        zOrderMediaOverlay = zOrderMediaOverlay,
        keepScreenOn = keepScreenOn,
        isListItem = isListItem,
        thumb = thumb
    )
}

internal fun getPlayInitial() = when(LocalVideoPlayback.current) {
    DisplayPreferences.AutoPlayback.Auto -> !LocalIsActiveNetworkMetered.current
    DisplayPreferences.AutoPlayback.Always -> true
    DisplayPreferences.AutoPlayback.Off -> false
}

expect class NativePlayerView() {
    var playerView: Any?
    var player: NativePlayer?
    fun resume(): Unit?
    fun pause(): Unit?
}

expect class NativePlayer {

    // var playWhenReady = realPlayer as
    var player: Any?
    var playWhenReady: Boolean
    fun contentPosition(): Long
    fun setCustomControl(customControl: Any?)
    fun resume()
    fun pause()
    fun update()
    fun setVolume(volume: Float)
    fun release()
}

expect fun nativeViewFactory(
    zOrderMediaOverlay: Boolean,
    showControls: Boolean,
    keepScreenOn: Boolean
): NativePlayerView

expect fun getNativePlayer(
    url: String,
    autoPlay: Boolean,
    setShowThumb: (Boolean) -> Unit,
    setPLaying: (Boolean) -> Unit
): NativePlayer

@Composable
expect fun PlatformView(
    zOrderMediaOverlay: Boolean,
    showControls: Boolean,
    keepScreenOn: Boolean,
    modifier: Modifier,
    update: (NativePlayerView) -> Unit
)