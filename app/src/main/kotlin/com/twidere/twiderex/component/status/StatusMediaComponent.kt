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
package com.twidere.twiderex.component.status

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.GridLayout
import com.twidere.twiderex.component.foundation.NetworkImage
import com.twidere.twiderex.component.foundation.VideoPlayer
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.extensions.withElevation
import com.twidere.twiderex.model.MediaType
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.ui.TwidereTheme

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

    val aspectRatio = if (media.size > 1) {
        270f / 162f
    } else {
        val first = media.first()
        (first.width.toFloat() / first.height.toFloat()).let {
            if (it.isNaN()) {
                270f / 162f
            } else {
                it
            }
        }
    }
    Box(
        modifier = Modifier
            .let {
                if (media.size == 1) {
                    it.heightIn(max = 400.dp)
                } else {
                    it
                }
            }
            .aspectRatio(aspectRatio),
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
                Column {
                    GridLayout(
                        modifier = Modifier.aspectRatio(aspectRatio),
                        spacing = StatusMediaDefaults.MediaSpacing
                    ) {
                        media.forEach {
                            StatusMediaPreviewItem(
                                media = it,
                                modifier = Modifier.weight(1f),
                                onClick = onItemClick,
                            )
                        }
                    }
                }
            }
        }

        if (status.platformType == PlatformType.Mastodon && status.mastodonExtra != null) {
            var sensitive by rememberSaveable(status.statusKey.toString()) {
                mutableStateOf(status.mastodonExtra.sensitive)
            }
            TwidereTheme(darkTheme = true) {
                AnimatedVisibility(
                    visible = sensitive,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colors.surface.withElevation())
                            .clickable {
                                sensitive = false
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_alert_triangle),
                            contentDescription = null,
                            tint = MaterialTheme.colors.onSurface,
                        )
                    }
                }
                AnimatedVisibility(
                    visible = !sensitive,
                ) {
                    Box(
                        modifier = Modifier
                            .padding(StatusMediaDefaults.Mastodon.IconSpacing)
                            .alpha(0.5f),
                    ) {
                        IconButton(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colors.surface,
                                    shape = MaterialTheme.shapes.medium,
                                )
                                .align(Alignment.TopStart),
                            onClick = { sensitive = true }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_eye_off),
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

    object Mastodon {
        val IconSpacing = 8.dp
    }
}

@Composable
fun StatusMediaPreviewItem(
    media: UiMedia,
    modifier: Modifier = Modifier,
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
                    )
                }
            MediaType.video, MediaType.animated_gif -> media.mediaUrl?.let {
                VideoPlayer(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            onClick = {
                                onClick(media)
                            }
                        ),
                    url = it,
                    showControls = false,
                    volume = 0F
                ) {
                    media.previewUrl?.let {
                        NetworkImage(
                            data = it,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    onClick = {
                                        onClick(media)
                                    }
                                ),
                        )
                    }
                }
            }
            MediaType.audio -> {
            }
            MediaType.other -> {
            }
        }
    }
}
