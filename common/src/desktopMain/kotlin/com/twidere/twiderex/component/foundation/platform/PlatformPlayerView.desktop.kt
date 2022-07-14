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
package com.twidere.twiderex.component.foundation.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.twidere.services.http.config.HttpConfig
import com.twidere.twiderex.component.foundation.DesktopMediaPlayerHelper
import com.twidere.twiderex.component.foundation.PlayerCallBack
import com.twidere.twiderex.component.foundation.PlayerProgressCallBack

actual class PlatformPlayerView actual constructor(
    url: String,
    httpConfig: HttpConfig,
    zOrderMediaOverlay: Boolean,
    keepScreenOn: Boolean,
    backgroundColor: Color?,
    onClick: (() -> Unit)?
) {
    private val mediaPlayer = DesktopMediaPlayerHelper.create(url, backgroundColor, onClick)

    actual fun registerPlayerCallback(callBack: PlayerCallBack) {
        mediaPlayer.registerPlayerCallback(callBack)
    }

    actual fun registerProgressCallback(callBack: PlayerProgressCallBack) {
        mediaPlayer.registerProgressCallback(callBack)
    }

    actual fun removePlayerCallback(callback: PlayerCallBack) {
        mediaPlayer.removePlayerCallback(callback)
    }

    actual fun removeProgressCallback(callback: PlayerProgressCallBack) {
        mediaPlayer.removeProgressCallback(callback)
    }

    actual fun play() {
        mediaPlayer.play()
    }

    actual fun pause() {
        mediaPlayer.pause()
    }

    actual fun contentPosition(): Long {
        return mediaPlayer.currentPosition()
    }

    actual fun duration(): Long {
        return mediaPlayer.duration()
    }

    actual fun seekTo(time: Long) {
        mediaPlayer.seekTo(time)
    }

    actual fun setVolume(volume: Float) {
        mediaPlayer.setVolume(volume)
    }

    actual fun setMute(mute: Boolean) {
        mediaPlayer.setMute(mute)
    }

    actual fun release() {
        mediaPlayer.release()
    }

    @Composable
    actual fun Content(modifier: Modifier, update: () -> Unit) {
        mediaPlayer.Content(
            modifier = modifier,
            update = update
        )
    }
}
