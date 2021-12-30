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
package com.twidere.twiderex.scenes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import com.mxalbert.zoomable.Zoomable
import com.twidere.twiderex.component.bottomInsetsHeight
import com.twidere.twiderex.component.bottomInsetsPadding
import com.twidere.twiderex.component.foundation.DropdownMenuItem
import com.twidere.twiderex.component.foundation.LoadingProgress
import com.twidere.twiderex.component.foundation.NetworkImage
import com.twidere.twiderex.component.foundation.Pager
import com.twidere.twiderex.component.foundation.PagerState
import com.twidere.twiderex.component.foundation.VideoPlayer
import com.twidere.twiderex.component.foundation.VideoPlayerState
import com.twidere.twiderex.component.foundation.platform.HorizontalPagerIndicator
import com.twidere.twiderex.component.foundation.rememberPagerState
import com.twidere.twiderex.component.foundation.rememberVideoPlayerState
import com.twidere.twiderex.component.navigation.INavigator
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.status.LikeButton
import com.twidere.twiderex.component.status.ReplyButton
import com.twidere.twiderex.component.status.RetweetButton
import com.twidere.twiderex.component.status.ShareButton
import com.twidere.twiderex.component.status.StatusText
import com.twidere.twiderex.component.status.UserAvatar
import com.twidere.twiderex.component.status.UserName
import com.twidere.twiderex.component.status.UserScreenName
import com.twidere.twiderex.component.status.renderContentAnnotatedString
import com.twidere.twiderex.component.status.resolveLink
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.component.topInsetsPadding
import com.twidere.twiderex.di.ext.getViewModel
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.extensions.playEnable
import com.twidere.twiderex.kmp.LocalPlatformWindow
import com.twidere.twiderex.kmp.Platform
import com.twidere.twiderex.kmp.currentPlatform
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.MediaType
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.preferences.LocalDisplayPreferences
import com.twidere.twiderex.preferences.model.DisplayPreferences
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.LocalVideoPlayback
import com.twidere.twiderex.ui.TwidereDialog
import com.twidere.twiderex.utils.video.CustomVideoControl
import com.twidere.twiderex.viewmodel.MediaViewModel
import kotlinx.coroutines.launch
import moe.tlaster.kfilepicker.FilePicker
import moe.tlaster.precompose.navigation.NavController
import moe.tlaster.swiper.Swiper
import moe.tlaster.swiper.SwiperState
import moe.tlaster.swiper.rememberSwiperState
import org.koin.core.parameter.parametersOf

@Composable
fun StatusMediaScene(statusKey: MicroBlogKey, selectedIndex: Int) {
    val viewModel = getViewModel<MediaViewModel> {
        parametersOf(statusKey)
    }
    val status by viewModel.status.observeAsState(null)
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

@Composable
fun StatusMediaScene(status: UiStatus, selectedIndex: Int, viewModel: MediaViewModel) {
    val window = LocalPlatformWindow.current
    var controlVisibility by remember { mutableStateOf(true) }
    val navigator = LocalNavigator.current
    val controlPanelColor = MaterialTheme.colors.surface.copy(alpha = 0.6f)
    val navController = LocalNavController.current
    val pagerState = rememberPagerState(
        initialPage = selectedIndex,
        pageCount = status.media.size,
    )
    val currentMedia = status.media[pagerState.currentPage]

    val videoPlayerState = mutableStateOf<VideoPlayerState?>(null)

    val swiperState = rememberSwiperState(
        onDismiss = {
            navController.popBackStack()
        },
    )
    StatusMediaSceneLayout(
        backgroundColor = Color.Transparent,
        contentColor = contentColorFor(backgroundColor = MaterialTheme.colors.background),
        bottomView = {
            StatusMediaBottomContent(
                status = status,
                visible = controlVisibility && swiperState.progress == 0f,
                controlPanelColor = controlPanelColor,
                navigator = navigator,
                videoPlayerState = videoPlayerState.value,
                viewModel = viewModel,
                currentMedia = currentMedia,
                pagerState = pagerState
            )
        },
        closeButton = {
            StatusMediaCloseButton(
                visible = controlVisibility && swiperState.progress == 0f,
                backgroundColor = controlPanelColor,
                navController = navController
            )
        },
        mediaView = {
            MediaView(
                media = status.media.mapNotNull {
                    it.mediaUrl?.let { it1 ->
                        MediaData(
                            it1,
                            it.type
                        )
                    }
                },
                swiperState = swiperState,
                onVideoPlayerStateSet = { videoPlayerState.value = it },
                pagerState = pagerState,
                volume = 1f,
                onClick = {
                    if (controlVisibility) {
                        window.hideControls()
                    } else {
                        window.showControls()
                    }
                },
                backgroundColor = MaterialTheme.colors.background
            )
            val windowBarVisibility by window.windowBarVisibility.observeAsState(true)
            LaunchedEffect(windowBarVisibility) {
                controlVisibility = windowBarVisibility
            }
            DisposableEffect(Unit) {
                onDispose {
                    window.showControls()
                }
            }
        },
        backgroundView = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background.copy(alpha = 1f - swiperState.progress)),
            )
        }
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun StatusMediaCloseButton(
    visible: Boolean,
    backgroundColor: Color,
    navController: NavController
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .topInsetsPadding()
                .padding(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .clip(MaterialTheme.shapes.small)
                    .background(
                        color = backgroundColor,
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
                        painter = painterResource(res = com.twidere.twiderex.MR.files.ic_x),
                        contentDescription = stringResource(
                            res = com.twidere.twiderex.MR.strings.accessibility_common_close
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun StatusMediaBottomContent(
    status: UiStatus,
    visible: Boolean,
    controlPanelColor: Color,
    navigator: INavigator,
    videoPlayerState: VideoPlayerState?,
    viewModel: MediaViewModel,
    currentMedia: UiMedia,
    pagerState: PagerState
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (status.media.size > 1) {
            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
            )
            AnimatedVisibility(
                visible = !(visible),
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                Spacer(modifier = Modifier.bottomInsetsHeight())
            }
        }
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + expandVertically(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .background(color = controlPanelColor)
                    .bottomInsetsPadding()
                    .clickable { navigator.status(status = status) },
            ) {
                StatusMediaInfo(
                    videoPlayerState, status, viewModel, currentMedia,
                )
            }
        }
    }
}

@OptIn(ExperimentalUnitApi::class)
@Composable
private fun StatusMediaInfo(
    videoPlayerState: VideoPlayerState?,
    status: UiStatus,
    viewModel: MediaViewModel,
    currentMedia: UiMedia,
) {
    val scope = rememberCoroutineScope()

    val text = renderContentAnnotatedString(
        htmlText = status.htmlText,
        linkResolver = { status.resolveLink(it) },
    )
    Column(
        modifier = Modifier
            .padding(StatusMediaInfoDefaults.ContentPadding),
    ) {
        if (videoPlayerState != null) {
            CustomVideoControl(state = videoPlayerState)
        }
        StatusText(status = status, maxLines = 2, showMastodonPoll = false)
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
            ShareButton(status = status) { callback ->
                DropdownMenuItem(
                    onClick = {
                        scope.launch {
                            callback.invoke()
                            viewModel.saveFile(currentMedia, target = {
                                FilePicker.createFile(it)?.path
                            })
                        }
                    }
                ) {
                    Text(
                        text = stringResource(res = com.twidere.twiderex.MR.strings.common_controls_actions_save),
                    )
                }
                DropdownMenuItem(
                    onClick = {
                        callback.invoke()
                        currentMedia.fileName?.let {
                            scope.launch {
                                viewModel.shareMedia(
                                    currentMedia = currentMedia,
                                    extraText = buildString {
                                        append(text)
                                        append(System.lineSeparator())
                                        append(System.lineSeparator())
                                        append(status.generateShareLink())
                                    }
                                )
                            }
                        }
                    }
                ) {
                    Text(
                        text = stringResource(res = com.twidere.twiderex.MR.strings.common_controls_actions_share_media),
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
fun RawMediaScene(url: String, type: MediaType) {
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
            Box {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background.copy(alpha = 1f - swiperState.progress)),
                )
                MediaView(media = listOf(MediaData(url, type)), swiperState = swiperState, onClick = {
                    navController.popBackStack()
                }, backgroundColor = MaterialTheme.colors.background)
            }
        }
    }
}

data class MediaData(
    val url: String,
    val type: MediaType,
)

@Composable
fun MediaView(
    backgroundColor: Color? = null,
    modifier: Modifier = Modifier,
    media: List<MediaData>,
    swiperState: SwiperState = rememberSwiperState(),
    pagerState: PagerState = rememberPagerState(
        initialPage = 0,
        pageCount = media.size,
    ),
    onVideoPlayerStateSet: (VideoPlayerState?) -> Unit = {},
    volume: Float = 1f,
    onClick: () -> Unit = {}
) {
    Swiper(
        modifier = modifier,
        state = swiperState,
    ) {
        Pager(
            state = pagerState,
        ) {
            val data = media[page]
            when (data.type) {
                MediaType.photo ->
                    Zoomable(
                        onClick = onClick
                    ) {
                        onVideoPlayerStateSet(null)
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
                            },
                            zoomable = true
                        )
                    }

                MediaType.video, MediaType.animated_gif, MediaType.audio ->
                    Box(
                        modifier = Modifier.clickable {
                            onClick.invoke()
                        }
                    ) {
                        val state = rememberVideoPlayerState(
                            url = data.url,
                            volume = volume,
                            isMute = LocalDisplayPreferences.current.muteByDefault
                        )
                        if (data.type == MediaType.animated_gif) {
                            onVideoPlayerStateSet(null)
                        } else {
                            onVideoPlayerStateSet(state)
                        }
                        VideoPlayer(
                            playEnable = LocalVideoPlayback.current.playEnable(),
                            videoState = state,
                            zOrderMediaOverlay = true,
                            keepScreenOn = true,
                            backgroundColor = backgroundColor,
                            // Pass the click event to swing on JVM
                            onClick = if (
                                currentPlatform == Platform.JVM
                            ) {
                                onClick
                            } else {
                                null
                            }
                        )
                    }
                MediaType.other -> Unit
            }
        }
    }
}
