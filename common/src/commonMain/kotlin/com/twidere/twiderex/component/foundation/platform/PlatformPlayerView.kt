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
import com.twidere.twiderex.component.foundation.PlayerCallBack
import com.twidere.twiderex.component.foundation.PlayerProgressCallBack

expect class PlatformPlayerView(
    url: String,
    httpConfig: HttpConfig,
    zOrderMediaOverlay: Boolean,
    keepScreenOn: Boolean,
    backgroundColor: Color?,
    onClick: (() -> Unit)?
) {
    fun registerPlayerCallback(callBack: PlayerCallBack)
    fun registerProgressCallback(callBack: PlayerProgressCallBack)
    fun removePlayerCallback(callback: PlayerCallBack)
    fun removeProgressCallback(callback: PlayerProgressCallBack)
    fun play()
    fun pause()
    fun contentPosition(): Long
    fun duration(): Long
    fun seekTo(time: Long)
    fun setVolume(volume: Float)
    fun setMute(mute: Boolean)
    fun release()
    @Composable
    fun Content(modifier: Modifier, update: () -> Unit)
}
