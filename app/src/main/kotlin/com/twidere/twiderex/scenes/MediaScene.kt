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

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.android.exoplayer2.ui.PlayerControlView
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.LoadingProgress
import com.twidere.twiderex.component.foundation.NetworkImage
import com.twidere.twiderex.component.foundation.VideoPlayer
import com.twidere.twiderex.component.status.LikeButton
import com.twidere.twiderex.component.status.ReplyButton
import com.twidere.twiderex.component.status.RetweetButton
import com.twidere.twiderex.component.status.ShareButton
import com.twidere.twiderex.component.status.StatusText
import com.twidere.twiderex.component.status.UserAvatar
import com.twidere.twiderex.component.status.UserName
import com.twidere.twiderex.component.status.UserScreenName
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.extensions.hideControls
import com.twidere.twiderex.extensions.setOnSystemBarsVisibilityChangeListener
import com.twidere.twiderex.extensions.showControls
import com.twidere.twiderex.model.MediaType
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.preferences.proto.DisplayPreferences
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.LocalVideoPlayback
import com.twidere.twiderex.ui.LocalWindow
import com.twidere.twiderex.ui.TwidereDialog
import com.twidere.twiderex.viewmodel.MediaViewModel
import moe.tlaster.swiper.Swiper
import moe.tlaster.swiper.SwiperState
import moe.tlaster.swiper.rememberSwiperState
import moe.tlaster.zoomable.Zoomable
import moe.tlaster.zoomable.rememberZoomableState

@Composable
fun StatusMediaScene(statusKey: MicroBlogKey, selectedIndex: Int) {
    val account = LocalActiveAccount.current ?: return
    val viewModel = assistedViewModel<MediaViewModel.AssistedFactory, MediaViewModel> {
        it.create(account, statusKey)
    }
    val status by viewModel.status.observeAsState()
    val loading by viewModel.loading.observeAsState(initial = false)
    TwidereDialog(
        requireDarkTheme = true,
        extendViewIntoStatusBar = true,
        extendViewIntoNavigationBar = true,
    ) {
        if (loading && status == null) {
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
                StatusMediaScene(
                    status = it,
                    selectedIndex = selectedIndex.coerceIn(0, it.media.lastIndex),
                    viewModel = viewModel,
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalPagerApi::class)
@Composable
fun StatusMediaScene(status: UiStatus, selectedIndex: Int, viewModel: MediaViewModel) {
    var controlVisibility by remember { mutableStateOf(true) }
    val controlPanelColor = MaterialTheme.colors.surface.copy(alpha = 0.6f)
    val navController = LocalNavController.current
    val swiperState = rememberSwiperState(
        onDismiss = {
            navController.popBackStack()
        },
        onStart = {
            controlVisibility = false
        },
        onEnd = {
            controlVisibility = true
        }
    )
    Scaffold(
        backgroundColor = Color.Transparent,
        contentColor = contentColorFor(backgroundColor = MaterialTheme.colors.background)
    ) {
        Box {
            val pagerState = rememberPagerState(
                initialPage = selectedIndex,
                pageCount = status.media.size,
            )
            val currentMedia = status.media[pagerState.currentPage]
            val context = LocalContext.current
            val videoControl = remember(pagerState.currentPage) {
                if (currentMedia.type == MediaType.video) {
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
                swiperState = swiperState,
                customControl = videoControl,
                pagerState = pagerState,
            )
            DisposableEffect(Unit) {
                window.setOnSystemBarsVisibilityChangeListener { visibility ->
                    controlVisibility = visibility
                }
                // make sure the system bars color is same to control panel
                window.navigationBarColor = android.graphics.Color.argb(
                    controlPanelColor.toArgb().alpha,
                    controlPanelColor.toArgb().red,
                    controlPanelColor.toArgb().green,
                    controlPanelColor.toArgb().blue
                )
                onDispose {
                    window.showControls()
                    // restore it to transparent
                    window.navigationBarColor = android.graphics.Color.TRANSPARENT
                }
            }
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
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                        ) {
                            if (status.media.size > 1) {
                                HorizontalPagerIndicator(
                                    pagerState = pagerState,
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .align(Alignment.CenterHorizontally),
                                )
                            }
                            AnimatedVisibility(
                                visible = controlVisibility,
                                enter = fadeIn() + expandVertically(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(color = controlPanelColor),
                                ) {
                                    StatusMediaInfo(videoControl, status, viewModel, currentMedia)
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
private fun StatusMediaInfo(
    videoControl: PlayerControlView?,
    status: UiStatus,
    viewModel: MediaViewModel,
    currentMedia: UiMedia
) {
    Column(
        modifier = Modifier
            .padding(StatusMediaInfoDefaults.ContentPadding),
    ) {
        if (videoControl != null) {
            AndroidView(factory = { videoControl })
        }
        StatusText(status = status, maxLines = 2)
        Spacer(modifier = Modifier.height(StatusMediaInfoDefaults.TextSpacing))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                UserAvatar(user = status.user)
                Spacer(modifier = Modifier.width(StatusMediaInfoDefaults.AvatarSpacing))
                UserName(user = status.user)
                Spacer(modifier = Modifier.width(StatusMediaInfoDefaults.NameSpacing))
                UserScreenName(user = status.user)
            }
            ReplyButton(status = status, withNumber = false)
            RetweetButton(status = status, withNumber = false)
            LikeButton(status = status, withNumber = false)
            val saveFileLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.CreateDocument()
            ) {
                it?.let {
                    viewModel.saveFile(currentMedia, it)
                }
            }
            ShareButton(status = status) { callback ->
                DropdownMenuItem(
                    onClick = {
                        callback.invoke()
                        currentMedia.fileName?.let {
                            saveFileLauncher.launch(it)
                        }
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.common_controls_actions_save),
                    )
                }
            }
        }
    }
}

private object StatusMediaInfoDefaults {
    val ContentPadding = PaddingValues(8.dp)
    val TextSpacing = 8.dp
    val AvatarSpacing = 8.dp
    val NameSpacing = 8.dp
}

@Composable
fun RawMediaScene(url: String) {
    TwidereDialog(
        requireDarkTheme = true,
        extendViewIntoStatusBar = true,
        extendViewIntoNavigationBar = true,
    ) {
        Scaffold(
            backgroundColor = Color.Transparent
        ) {
            val navController = LocalNavController.current
            val swiperState = rememberSwiperState(
                onDismiss = {
                    navController.popBackStack()
                },
            )
            MediaView(media = listOf(MediaData(url, MediaType.photo)), swiperState = swiperState)
        }
    }
}

data class MediaData(
    val url: String,
    val type: MediaType,
)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MediaView(
    modifier: Modifier = Modifier,
    media: List<MediaData>,
    swiperState: SwiperState = rememberSwiperState(),
    pagerState: PagerState = rememberPagerState(
        initialPage = 0,
        pageCount = media.size,
    ),
    customControl: PlayerControlView? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background.copy(alpha = 1f - swiperState.progress)),
    )
    Swiper(
        modifier = modifier,
        state = swiperState,
    ) {
        HorizontalPager(
            state = pagerState,
        ) { page ->
            val data = media[page]
            when (data.type) {
                MediaType.photo ->
                    Zoomable(
                        state = rememberZoomableState()
                    ) {
                        NetworkImage(
                            modifier = Modifier.fillMaxSize(),
                            data = data.url,
                            contentScale = ContentScale.Fit,
                            placeholder = {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        )
                    }
                MediaType.video, MediaType.animated_gif, MediaType.audio ->
                    Box {
                        VideoPlayer(
                            url = data.url,
                            customControl = customControl,
                            showControls = false,
                            zOrderMediaOverlay = true
                        )
                    }
                MediaType.other -> Unit
            }
        }
    }
}
