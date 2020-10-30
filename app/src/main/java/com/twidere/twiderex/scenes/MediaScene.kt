package com.twidere.twiderex.scenes

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.zoomable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.annotations.IncomingComposeUpdate
import com.twidere.twiderex.component.ActionIconButton
import com.twidere.twiderex.component.NetworkImage
import com.twidere.twiderex.component.Pager
import com.twidere.twiderex.ui.AmbientNavController
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.model.ui.UiStatus
import kotlin.math.max

@Composable
fun MediaScene(statusId: String, selectedIndex: Int) {
    //TODO: load media
}


@OptIn(IncomingComposeUpdate::class)
@Composable
fun MediaScene(status: UiStatus, selectedIndex: Int) {
    var lockPager by remember { mutableStateOf(false) }
    val controlPanelColor = MaterialTheme.colors.surface.copy(alpha = 0.6f)
    Scaffold {
        Box {
            Pager(
                items = status.media,
                startPage = selectedIndex,
                enableDrag = !lockPager,
            ) {
                MediaItemView(data) {
                    lockPager = it
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(color = controlPanelColor)
            ) {
                Row {
                    Spacer(modifier = Modifier.weight(1f))
                    ActionIconButton(onClick = {}) {
                        Icon(asset = Icons.Default.Reply)
                    }
                    ActionIconButton(onClick = {}) {
                        Icon(asset = Icons.Default.Comment)
                    }
                    ActionIconButton(onClick = {}) {
                        Icon(asset = Icons.Default.Favorite)
                    }
                    ActionIconButton(onClick = {}) {
                        Icon(asset = Icons.Default.Share)
                    }
                    Spacer(modifier = Modifier.weight(1f))
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
                        .background(color = controlPanelColor, shape = MaterialTheme.shapes.small)
                        .clipToBounds()
                ) {
                    val navController = AmbientNavController.current
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
