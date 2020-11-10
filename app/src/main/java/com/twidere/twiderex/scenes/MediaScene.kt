/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.scenes

import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.zoomable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.AmbientEmphasisLevels
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideEmphasis
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedTask
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.drawLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.DragObserver
import androidx.compose.ui.gesture.rawDragGestureFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.navigate
import com.twidere.twiderex.annotations.IncomingComposeUpdate
import com.twidere.twiderex.component.foundation.ActionIconButton
import com.twidere.twiderex.component.foundation.LoadingProgress
import com.twidere.twiderex.component.foundation.NetworkImage
import com.twidere.twiderex.component.foundation.Pager
import com.twidere.twiderex.component.status.LikeButton
import com.twidere.twiderex.component.status.ReplyButton
import com.twidere.twiderex.component.status.RetweetButton
import com.twidere.twiderex.component.status.UserAvatar
import com.twidere.twiderex.extensions.navViewModel
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.ui.AmbientNavController
import com.twidere.twiderex.ui.TwidereXTheme
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.viewmodel.MediaViewModel
import kotlin.math.max

@Composable
fun MediaScene(statusId: String, selectedIndex: Int) {
    val viewModel = navViewModel<MediaViewModel>()
    val loading by viewModel.loading.observeAsState(initial = false)
    val status by viewModel.status.observeAsState()
    LaunchedTask {
        viewModel.init(statusId)
    }
    TwidereXTheme(
        requireDarkTheme = true,
        pureStatusBarColor = true,
    ) {
        if (loading) {
            Scaffold {
                Column(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LoadingProgress()
                }
            }
        }
        status?.let {
            MediaScene(status = it, selectedIndex = selectedIndex)
        }
    }
}

@OptIn(IncomingComposeUpdate::class)
@Composable
fun MediaScene(status: UiStatus, selectedIndex: Int) {
    var lockPager by remember { mutableStateOf(false) }
    var hideControls by remember { mutableStateOf(false) }
    val controlPanelColor = MaterialTheme.colors.surface.copy(alpha = 0.6f)
    val navController = AmbientNavController.current
    Scaffold {
        Box {
            Pager(
                modifier = Modifier
                    .clickable(
                        onClick = {
                            hideControls = !hideControls
                        },
                        indication = null,
                    ),
                items = status.media,
                startPage = selectedIndex,
                enableDrag = !lockPager,
            ) {
                MediaItemView(data) {
                    lockPager = it
                }
            }
            if (!hideControls) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(color = controlPanelColor),
                ) {
                    Column(
                        modifier = Modifier
                            .padding(standardPadding),
                    ) {
                        Text(
                            modifier = Modifier
                                .clickable(
                                    onClick = {
                                        navController.navigate("status/${status.statusId}")
                                    }
                                ),
                            text = status.text,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(modifier = Modifier.height(standardPadding))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(
                                modifier = Modifier
                                    .weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                UserAvatar(user = status.user)
                                Spacer(modifier = Modifier.width(standardPadding))
                                Text(
                                    text = status.user.name,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Spacer(modifier = Modifier.width(standardPadding))
                                ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.medium) {
                                    Text(
                                        text = "@${status.user.screenName}",
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                            ReplyButton(status = status, withNumber = false)
                            RetweetButton(status = status, withNumber = false)
                            LikeButton(status = status, withNumber = false)
                            ActionIconButton(onClick = {}) {
                                Icon(asset = Icons.Default.Share)
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .clip(MaterialTheme.shapes.small)
                            .background(
                                color = controlPanelColor,
                                shape = MaterialTheme.shapes.small
                            )
                            .clipToBounds()
                    ) {
                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            Icon(asset = Icons.Default.Close)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MediaItemView(
    data: UiMedia,
    requestLock: (Boolean) -> Unit,
) {
    var scale by remember { mutableStateOf(1f) }
    var translate by remember { mutableStateOf(Offset(0f, 0f)) }
    var looked by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
    ) {
        Box(
            modifier = Modifier
                .zoomable(
                    onZoomDelta = { scale = max(1f, scale * it) },
                    onZoomStarted = {
                        looked = true
                        requestLock(looked)
                    },
                    onZoomStopped = {
                        looked = scale != 1f
                        requestLock(looked)
                    },
                )
                .rawDragGestureFilter(
                    object : DragObserver {
                        override fun onDrag(dragDistance: Offset): Offset {
                            if (looked) {
                                translate = translate.plus(dragDistance)
                            }
                            return super.onDrag(dragDistance)
                        }
                    })
                .fillMaxSize(),
            alignment = Alignment.Center,
        ) {
            data.mediaUrl?.let {
                NetworkImage(
                    url = it,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .drawLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = translate.x,
                            translationY = translate.y
                        ),
                    placeholder = {
                        CircularProgressIndicator()
                    }
                )
            }
        }
    }
}
