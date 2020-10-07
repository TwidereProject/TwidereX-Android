package com.twidere.twiderex.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.zoomable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
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
import androidx.navigation.fragment.navArgs
import com.twidere.twiderex.annotations.IncomingComposeUpdate
import com.twidere.twiderex.component.ActionIconButton
import com.twidere.twiderex.component.NetworkImage
import com.twidere.twiderex.component.Pager
import com.twidere.twiderex.extensions.NavControllerAmbient
import com.twidere.twiderex.extensions.compose
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.ui.TwidereXTheme
import kotlin.math.max

class MediaFragment : JetFragment() {
    private val args by navArgs<MediaFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return compose {
            TwidereXTheme(darkTheme = true) {
                onCompose()
            }
        }
    }

    @OptIn(IncomingComposeUpdate::class)
    @Composable
    override fun onCompose() {
        var lockPager by remember { mutableStateOf(false) }
        val controlPanelColor = MaterialTheme.colors.surface.copy(alpha = 0.6f)
        Scaffold {
            Box {
                Pager(
                    items = args.status.media,
                    startPage = args.selectedIndex,
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
                            .clip(RoundedCornerShape(8.dp))
                            .background(color = controlPanelColor, shape = RoundedCornerShape(8.dp))
                            .clipToBounds()
                    ) {
                        val navController = NavControllerAmbient.current
                        IconButton(onClick = {
                            navController.popBackStack()
                        }) {
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