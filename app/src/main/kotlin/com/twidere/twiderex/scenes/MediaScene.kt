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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.compose.navigate
import com.google.android.exoplayer2.ui.PlayerControlView
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.LoadingProgress
import com.twidere.twiderex.component.foundation.NetworkImage
import com.twidere.twiderex.component.foundation.Pager
import com.twidere.twiderex.component.foundation.PagerState
import com.twidere.twiderex.component.foundation.Swiper
import com.twidere.twiderex.component.foundation.VideoPlayer
import com.twidere.twiderex.component.foundation.Zoomable
import com.twidere.twiderex.component.foundation.rememberPagerState
import com.twidere.twiderex.component.status.LikeButton
import com.twidere.twiderex.component.status.ReplyButton
import com.twidere.twiderex.component.status.RetweetButton
import com.twidere.twiderex.component.status.ShareButton
import com.twidere.twiderex.component.status.UserAvatar
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.extensions.hideControls
import com.twidere.twiderex.extensions.setOnSystemBarsVisibilityChangeListener
import com.twidere.twiderex.extensions.showControls
import com.twidere.twiderex.model.MediaType
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.navigation.Route
import com.twidere.twiderex.preferences.proto.DisplayPreferences
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.LocalVideoPlayback
import com.twidere.twiderex.ui.LocalWindow
import com.twidere.twiderex.ui.TwidereXTheme
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.viewmodel.MediaViewModel
import dev.chrisbanes.accompanist.glide.LocalRequestManager
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import dev.chrisbanes.accompanist.insets.statusBarsPadding

@Composable
fun StatusMediaScene(statusKey: MicroBlogKey, selectedIndex: Int) {
    val account = LocalActiveAccount.current ?: return
    val viewModel = assistedViewModel<MediaViewModel.AssistedFactory, MediaViewModel> {
        it.create(account, statusKey)
    }
    val loading by viewModel.loading.observeAsState(initial = false)
    val status by viewModel.status.observeAsState()
    TwidereXTheme(
        requireDarkTheme = true,
        extendViewIntoStatusBar = true,
        extendViewIntoNavigationBar = true,
    ) {
        if (loading) {
            Scaffold {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LoadingProgress()
                }
            }
        }
        status?.let {
            CompositionLocalProvider(
                LocalVideoPlayback provides DisplayPreferences.AutoPlayback.Always
            ) {
                StatusMediaScene(status = it, selectedIndex = selectedIndex)
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StatusMediaScene(status: UiStatus, selectedIndex: Int) {
    var controlVisibility by remember { mutableStateOf(true) }
    val controlPanelColor = MaterialTheme.colors.surface.copy(alpha = 0.6f)
    val navController = LocalNavController.current
    Scaffold {
        Box {
            val pagerState = rememberPagerState(
                currentPage = selectedIndex,
                maxPage = status.media.lastIndex,
            )
            val context = LocalContext.current
            val videoControl = remember(pagerState.currentPage) {
                if (status.media[pagerState.currentPage].type == MediaType.video) {
                    PlayerControlView(context).apply {
                        showTimeoutMs = 0
                    }
                } else {
                    null
                }
            }
            val window = LocalWindow.current
            MediaView(
                modifier = Modifier
                    .clickable(
                        onClick = {
                            if (controlVisibility) {
                                window.hideControls()
                            } else {
                                window.showControls()
                            }
                        },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ),
                media = status.media.mapNotNull {
                    it.mediaUrl?.let { it1 ->
                        MediaData(
                            it1,
                            it.type
                        )
                    }
                },
                onSwipeEnd = {
                    controlVisibility = true
                },
                onSwipeStart = {
                    controlVisibility = false
                },
                customControl = videoControl,
                pagerState = pagerState,
            )
            DisposableEffect(Unit) {
                window.setOnSystemBarsVisibilityChangeListener { visibility ->
                    controlVisibility = visibility
                }
                onDispose {
                    window.showControls()
                }
            }
            // val transition = updateTransition(targetState = controlVisibility)
            // val alpha by transition.animateFloat {
            //     if (it) 1f else 0f
            // }
            InAppNotificationScaffold(
                backgroundColor = Color.Transparent,
                topBar = {
                    AnimatedVisibility(
                        visible = controlVisibility,
                        enter = fadeIn() + expandVertically(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Box(
                            modifier = Modifier
                                .statusBarsPadding()
                                .padding(16.dp),
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
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_x),
                                        contentDescription = stringResource(
                                            id = R.string.accessibility_common_close
                                        )
                                    )
                                }
                            }
                        }
                    }
                },
                bottomBar = {
                    Box(
                        modifier = Modifier
                            .navigationBarsPadding(),
                    ) {
                        AnimatedVisibility(
                            visible = controlVisibility,
                            enter = fadeIn() + expandVertically(),
                            exit = shrinkVertically() + fadeOut()
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
                                        AndroidView(factory = { videoControl })
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
                                            CompositionLocalProvider(
                                                LocalContentAlpha provides ContentAlpha.medium
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
                        }
                    }
                }
            ) {
            }
        }
    }
}

@Composable
fun RawMediaScene(url: String) {
    TwidereXTheme(
        requireDarkTheme = true,
        extendViewIntoStatusBar = true,
        extendViewIntoNavigationBar = true,
    ) {
        Scaffold {
            MediaView(media = listOf(MediaData(url, MediaType.photo)))
        }
    }
}

data class MediaData(
    val url: String,
    val type: MediaType,
)

@Composable
fun MediaView(
    modifier: Modifier = Modifier,
    media: List<MediaData>,
    pagerState: PagerState = rememberPagerState(
        currentPage = 0,
        maxPage = media.lastIndex,
    ),
    customControl: PlayerControlView? = null,
    onSwipeStart: () -> Unit = {},
    onSwipeEnd: () -> Unit = {},
) {
    var lockPager by remember { mutableStateOf(false) }
    val navController = LocalNavController.current
    val requestManager = LocalRequestManager.current
    LaunchedEffect(Unit) {
        requestManager?.let {
            if (requestManager.isPaused) {
                requestManager.resumeRequests()
            }
        }
    }
    Swiper(
        modifier = modifier,
        enabled = !lockPager,
        onDismiss = {
            navController.popBackStack()
        },
        onStart = {
            onSwipeStart.invoke()
        },
        onEnd = {
            onSwipeEnd.invoke()
        }
    ) {
        Pager(
            state = pagerState,
            dragEnabled = !lockPager,
        ) {
            val data = media[this.page]
            when (data.type) {
                MediaType.photo ->
                    Zoomable(
                        onZooming = {
                            lockPager = it != 1F
                        }
                    ) {
                        NetworkImage(
                            data = data.url,
                            contentScale = ContentScale.Fit,
                            placeholder = {
                                CircularProgressIndicator()
                            }
                        )
                    }
                MediaType.video, MediaType.animated_gif ->
                    Box {
                        VideoPlayer(
                            url = data.url,
                            customControl = customControl,
                            showControls = false
                        )
                    }
            }
        }
    }
}
