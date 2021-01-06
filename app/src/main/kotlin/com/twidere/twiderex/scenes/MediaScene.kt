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
package com.twidere.twiderex.scenes

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
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
import androidx.compose.material.AmbientContentAlpha
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.DragObserver
import androidx.compose.ui.gesture.rawDragGestureFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.AmbientAnimationClock
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.compose.navigate
import com.google.android.exoplayer2.ui.PlayerControlView
import com.twidere.twiderex.R
import com.twidere.twiderex.annotations.IncomingComposeUpdate
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.LoadingProgress
import com.twidere.twiderex.component.foundation.NetworkImage
import com.twidere.twiderex.component.foundation.Pager
import com.twidere.twiderex.component.foundation.PagerState
import com.twidere.twiderex.component.foundation.VideoPlayer
import com.twidere.twiderex.component.status.LikeButton
import com.twidere.twiderex.component.status.ReplyButton
import com.twidere.twiderex.component.status.RetweetButton
import com.twidere.twiderex.component.status.ShareButton
import com.twidere.twiderex.component.status.UserAvatar
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.model.MediaType
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.navigation.Route
import com.twidere.twiderex.preferences.proto.DisplayPreferences
import com.twidere.twiderex.ui.AmbientActiveAccount
import com.twidere.twiderex.ui.AmbientNavController
import com.twidere.twiderex.ui.AmbientVideoPlayback
import com.twidere.twiderex.ui.TwidereXTheme
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.viewmodel.MediaViewModel

@Composable
fun MediaScene(statusKey: MicroBlogKey, selectedIndex: Int) {
    val account = AmbientActiveAccount.current ?: return
    val viewModel = assistedViewModel<MediaViewModel.AssistedFactory, MediaViewModel> {
        it.create(account, statusKey)
    }
    val loading by viewModel.loading.observeAsState(initial = false)
    val status by viewModel.status.observeAsState()
    TwidereXTheme(
        requireDarkTheme = true,
        pureStatusBarColor = true,
    ) {
        if (loading) {
            InAppNotificationScaffold {
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
            Providers(
                AmbientVideoPlayback provides DisplayPreferences.AutoPlayback.Always
            ) {
                MediaScene(status = it, selectedIndex = selectedIndex)
            }
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
    InAppNotificationScaffold {
        Box {
            val clock = AmbientAnimationClock.current
            val pagerState = remember(clock) {
                PagerState(
                    clock,
                    currentPage = selectedIndex,
                    maxPage = status.media.lastIndex,
                )
            }
            val context = AmbientContext.current
            val videoControl = remember(pagerState.currentPage) {
                if (status.media[pagerState.currentPage].type == MediaType.video) {
                    PlayerControlView(context).apply {
                        showTimeoutMs = 0
                    }
                } else {
                    null
                }
            }
            Pager(
                modifier = Modifier
                    .clickable(
                        onClick = {
                            hideControls = !hideControls
                        },
                        indication = null,
                    ),
                state = pagerState,
                dragEnabled = !lockPager,
            ) {
                val data = status.media[this.page]
                MediaItemView(data, customControl = videoControl) {
                    lockPager = it
                }
            }
            val transition = updateTransition(targetState = !hideControls)
            val alpha by transition.animateFloat {
                if (it) 1f else 0f
            }
            if (alpha != 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(alpha),
                ) {
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
                            if (videoControl != null) {
                                AndroidView(viewBlock = { videoControl })
                            }
                            Text(
                                modifier = Modifier
                                    .clickable(
                                        onClick = {
                                            navController.navigate(Route.Status(status.statusKey))
                                        }
                                    ),
                                text = status.rawText,
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
                                    Providers(
                                        AmbientContentAlpha provides ContentAlpha.medium
                                    ) {
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
                                ShareButton(status = status)
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
                                Icon(imageVector = vectorResource(id = R.drawable.ic_x))
                            }
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
    customControl: PlayerControlView? = null,
    requestLock: (Boolean) -> Unit,
) {
    var scale by remember { mutableStateOf(1f) }
    var translate by remember { mutableStateOf(Offset(0f, 0f)) }
    var looked by remember { mutableStateOf(false) }
    val observer = remember {
        object : DragObserver {
            override fun onDrag(dragDistance: Offset): Offset {
                if (looked) {
                    translate = translate.plus(dragDistance)
                }
                return super.onDrag(dragDistance)
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            data.mediaUrl?.let {
                when (data.type) {
                    MediaType.photo ->
                        Box(
                            modifier = Modifier
                                .zoomable(
                                    onZoomDelta = { scale = (scale * it).coerceAtLeast(1F) },
                                    onZoomStarted = {
                                        looked = true
                                        requestLock(looked)
                                    },
                                    onZoomStopped = {
                                        looked = scale != 1f
                                        requestLock(looked)
                                    },
                                )
                                .rawDragGestureFilter(observer)
                                .layout { measurable, constraints ->
                                    val placeable =
                                        measurable.measure(constraints = constraints)
                                    layout(
                                        width = constraints.maxWidth,
                                        height = constraints.maxHeight
                                    ) {
                                        placeable.placeRelativeWithLayer(
                                            (constraints.maxWidth - placeable.width) / 2,
                                            (constraints.maxHeight - placeable.height) / 2
                                        ) {
                                            scaleX = scale
                                            scaleY = scale
                                            val x = (placeable.width * scale - constraints.maxWidth)
                                                .coerceAtLeast(0F) / 2F
                                            val y =
                                                (placeable.height * scale - constraints.maxHeight)
                                                    .coerceAtLeast(0F) / 2F
                                            translationX = translate.x.coerceIn(
                                                -x,
                                                x,
                                            )
                                            translationY = translate.y.coerceIn(
                                                -y,
                                                y,
                                            )
                                        }
                                    }
                                }
                        ) {
                            NetworkImage(
                                url = it,
                                contentScale = ContentScale.Fit,
                                placeholder = {
                                    CircularProgressIndicator()
                                }
                            )
                        }
                    MediaType.video, MediaType.animated_gif ->
                        Box {
                            VideoPlayer(
                                url = it,
                                customControl = customControl
                            )
                        }
                }
            }
        }
    }
}
