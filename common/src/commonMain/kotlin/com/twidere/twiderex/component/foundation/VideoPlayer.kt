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
package com.twidere.twiderex.component.foundation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.MR
import com.twidere.twiderex.component.foundation.platform.PlatformPlayerView
import com.twidere.twiderex.compose.LocalResLoader
import com.twidere.twiderex.preferences.LocalHttpConfig
import moe.tlaster.precompose.lifecycle.Lifecycle
import moe.tlaster.precompose.lifecycle.LifecycleObserver
import moe.tlaster.precompose.ui.LocalLifecycleOwner

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier.fillMaxSize(), // must set video player size
    videoState: VideoPlayerState,
    playEnable: Boolean = true,
    zOrderMediaOverlay: Boolean = false,
    keepScreenOn: Boolean = false,
    thumb: @Composable() (() -> Unit)? = null,
    backgroundColor: Color? = null,
    onClick: (() -> Unit)? = null
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val resLoder = LocalResLoader.current
    val httpConfig = LocalHttpConfig.current
    Box {
        if (playEnable) {
            val platformPlayerView = remember(videoState.url) {
                PlatformPlayerView(
                    url = videoState.url,
                    httpConfig = httpConfig,
                    zOrderMediaOverlay = zOrderMediaOverlay,
                    keepScreenOn = keepScreenOn,
                    backgroundColor = backgroundColor,
                    onClick = onClick
                ).apply {
                    videoState.bind(this)
                }
            }
            DisposableEffect(Unit) {
                val observer = object : LifecycleObserver {
                    override fun onStateChanged(state: Lifecycle.State) {
                        when (state) {
                            Lifecycle.State.Active -> {
                                videoState.onResume()
                            }
                            Lifecycle.State.InActive -> {
                                videoState.onPause()
                            }
                            else -> {
                            }
                        }
                    }
                }
                lifecycle.addObserver(observer)
                onDispose {
                    videoState.onPause()
                    platformPlayerView.release()
                    lifecycle.removeObserver(observer)
                }
            }

            Box {
                platformPlayerView.Content(modifier = modifier) {}
            }
        }
        if ((videoState.showThumbnail || !playEnable) && thumb != null) {
            thumb()
        }
        if (videoState.showLoading && playEnable) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        if (!playEnable) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    tint = Color.White.copy(alpha = LocalContentAlpha.current),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(UserAvatarDefaults.AvatarSize)
                        .background(MaterialTheme.colors.primary, CircleShape),
                    contentDescription = resLoder.getString(
                        MR.strings.accessibility_common_video_play
                    )
                )
            }
        }
    }
}

interface PlayerCallBack {
    fun onPrepareStart() // start to prepare video
    fun onReady() // ready to play video
    fun onIsPlayingChanged(isPlaying: Boolean) // video play/pause
    fun onBuffering() // video buffering
}

interface PlayerProgressCallBack {
    fun onTimeChanged(time: Long)
}

object UserAvatarDefaults {
    val AvatarSize = 44.dp
}
