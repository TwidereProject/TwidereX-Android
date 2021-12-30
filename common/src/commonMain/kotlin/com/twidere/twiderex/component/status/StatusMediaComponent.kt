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
package com.twidere.twiderex.component.status

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.foundation.GifTag
import com.twidere.twiderex.component.foundation.GridLayout
import com.twidere.twiderex.component.foundation.MostCenterInListLayout
import com.twidere.twiderex.component.foundation.NetworkImage
import com.twidere.twiderex.component.foundation.VideoPlayer
import com.twidere.twiderex.component.foundation.rememberVideoPlayerState
import com.twidere.twiderex.component.image.ImageBlur
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.extensions.playEnable
import com.twidere.twiderex.model.enums.MediaType
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.ui.LocalVideoPlayback
import com.twidere.twiderex.ui.TwidereTheme
import moe.tlaster.placeholder.Placeholder

@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
fun StatusMediaComponent(
    status: UiStatus,
) {
    val navigator = LocalNavigator.current
    val media = status.media
    if (!media.any() || media.any { it.type == MediaType.audio }) {
        return
    }
    val onItemClick = { it: UiMedia ->
        val index = media.indexOf(it)
        navigator.media(statusKey = status.statusKey, selectedIndex = index)
    }
    var sensitive by rememberSaveable(status.statusKey.toString()) {
        mutableStateOf(status.sensitive)
    }

    val aspectRatio = when (media.size) {
        in 2..4 -> {
            StatusMediaDefaults.DefaultAspectRatio
        }
        1 -> {
            val first = media.first()
            (first.width.toFloat() / first.height.toFloat()).let {
                if (it.isNaN()) {
                    StatusMediaDefaults.DefaultAspectRatio
                } else {
                    it
                }
            }
        }
        else -> {
            null
        }
    }
    Box(
        modifier = Modifier
            .let {
                if (media.size == 1) {
                    it.heightIn(max = StatusMediaDefaults.DefaultMaxHeight)
                } else {
                    it
                }
            }
            .let { modifier ->
                aspectRatio?.let {
                    modifier.aspectRatio(aspectRatio)
                } ?: modifier
            }
            .clip(MaterialTheme.shapes.medium)
    ) {
        when (media.size) {
            3 -> {
                Row {
                    media.firstOrNull()?.let {
                        StatusMediaPreviewItem(
                            media = it,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize(),
                            sensitive = sensitive && status.platformType === PlatformType.Mastodon,
                            onClick = onItemClick,
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .width(StatusMediaDefaults.MediaSpacing)
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        media.drop(1).forEach {
                            StatusMediaPreviewItem(
                                media = it,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize(),
                                sensitive = sensitive && status.platformType === PlatformType.Mastodon,
                                onClick = onItemClick,
                            )
                            if (it != media.last()) {
                                Spacer(
                                    modifier = Modifier
                                        .height(StatusMediaDefaults.MediaSpacing)
                                )
                            }
                        }
                    }
                }
            }
            else -> {
                GridLayout(
                    spacing = StatusMediaDefaults.MediaSpacing
                ) {
                    media.forEach {
                        StatusMediaPreviewItem(
                            media = it,
                            onClick = onItemClick,
                            sensitive = sensitive && status.platformType === PlatformType.Mastodon
                        )
                    }
                }
            }
        }

        if (status.platformType == PlatformType.Mastodon && status.mastodonExtra != null) {
            TwidereTheme(darkTheme = true) {
                AnimatedVisibility(
                    modifier = Modifier
                        .matchParentSize(),
                    visible = sensitive,
                ) {
                    Box(
                        modifier = Modifier
                            .clickable {
                                sensitive = false
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colors.surface.copy(alpha = 0.25f),
                                    shape = CircleShape
                                )
                                .size(StatusMediaDefaults.Sensitive.BackgroundSize)
                        ) {
                            Icon(
                                painter = painterResource(res = com.twidere.twiderex.MR.files.ic_alert_triangle),
                                contentDescription = null,
                                tint = MaterialTheme.colors.onSurface,
                                modifier = Modifier
                                    .size(StatusMediaDefaults.Sensitive.IconSize)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                }
                AnimatedVisibility(
                    visible = !sensitive,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .padding(StatusMediaDefaults.Mastodon.IconSpacing)
                            .alpha(0.5f),
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colors.surface,
                                    shape = MaterialTheme.shapes.small,
                                )
                                .align(Alignment.TopStart)
                                .clickable { sensitive = true }
                                .padding(StatusMediaDefaults.Icon.ContentPadding),
                        ) {
                            Icon(
                                painter = painterResource(res = com.twidere.twiderex.MR.files.ic_eye_off),
                                contentDescription = null,
                                tint = MaterialTheme.colors.onSurface,
                            )
                        }
                    }
                }
            }
        }
    }
}

object StatusMediaDefaults {
    val MediaSpacing = 8.dp
    val DefaultAspectRatio = 270f / 162f
    val DefaultMaxHeight = 400.dp

    object Mastodon {
        val IconSpacing = 8.dp
    }

    object Icon {
        val ContentPadding = 6.dp
    }

    object Sensitive {
        val BackgroundSize = 48.dp
        val IconSize = 30.dp
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StatusMediaPreviewItem(
    media: UiMedia,
    modifier: Modifier = Modifier,
    sensitive: Boolean = false,
    onClick: (UiMedia) -> Unit,
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
    ) {
        when (media.type) {
            MediaType.photo ->
                media.previewUrl?.let {
                    NetworkImage(
                        data = it,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                onClick = {
                                    onClick(media)
                                }
                            ),
                        effects = { if (sensitive) blur(ImageBlur.Sensitive) },
                        placeholder = {
                            Placeholder(modifier = Modifier.fillMaxSize())
                        },
                    )
                }
            MediaType.video, MediaType.animated_gif -> media.mediaUrl?.let {
                val previewUrl = media.previewUrl
                if (sensitive && previewUrl != null) {
                    NetworkImage(
                        data = previewUrl,
                        effects = { blur(ImageBlur.Sensitive) },
                        modifier = Modifier
                            .fillMaxSize(),
                        placeholder = {
                            Placeholder(modifier = Modifier.fillMaxSize())
                        },
                    )
                } else {
                    MostCenterInListLayout(
                        videoKey = it
                    ) { isMostCenter ->
                        VideoPlayer(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable(
                                    onClick = {
                                        onClick(media)
                                    }
                                ),
                            videoState = rememberVideoPlayerState(
                                url = it,
                                isMute = true
                            ),
                            playEnable = LocalVideoPlayback.current.playEnable() && isMostCenter,
                            thumb = {
                                previewUrl?.let {
                                    NetworkImage(
                                        data = it,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(
                                                onClick = {
                                                    onClick(media)
                                                }
                                            ),
                                        placeholder = {
                                            Placeholder(modifier = Modifier.fillMaxSize())
                                        },
                                    )
                                }
                            },
                            backgroundColor = MaterialTheme.colors.background
                        )
                    }
                }
                if (media.type == MediaType.animated_gif) {
                    GifTag(Modifier.align(Alignment.BottomStart))
                }
            }
            MediaType.audio -> {
            }
            MediaType.other -> {
            }
        }
    }
}
