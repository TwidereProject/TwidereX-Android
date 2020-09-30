package com.twidere.twiderex.component

import androidx.compose.foundation.Box
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.R
import com.twidere.twiderex.extensions.AmbientNavController
import com.twidere.twiderex.fragment.MediaFragmentArgs
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.model.ui.UiStatus


@Composable
fun StatusMediaComponent(
    status: UiStatus,
) {
    val media = status.media
    val navController = AmbientNavController.current
    val onItemClick = { it: UiMedia ->
        val index = media.indexOf(it)
        navController.navigate(R.id.media_fragment, MediaFragmentArgs(status, index).toBundle())
    }
    if (media.size == 1) {
        val first = media.first()
        Box(
            modifier = Modifier
                .heightIn(max = 400.dp)
                .aspectRatio(first.width.toFloat() / first.height.toFloat())
                .clip(RoundedCornerShape(8.dp))
        ) {
            StatusMediaPreviewItem(
                media = first,
                onClick = onItemClick,
            )
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(270f / 162f)
                .clip(RoundedCornerShape(8.dp))
        ) {
            if (media.size == 3) {
                Row {
                    media.firstOrNull()?.let {
                        StatusMediaPreviewItem(
                            media = it,
                            modifier = Modifier.weight(1f),
                            onClick = onItemClick,
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        media.drop(1).forEach {
                            StatusMediaPreviewItem(
                                media = it,
                                modifier = Modifier.weight(1f),
                                onClick = onItemClick,
                            )
                        }
                    }
                }
            } else {
                Column {
                    for (i in media.indices.filter { it % 2 == 0 }) {
                        Row(
                            modifier = Modifier.weight(1f),
                        ) {
                            for (y in (i until i + 2)) {
                                media.elementAtOrNull(y)?.let {
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
            }
        }
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
    ) {
        media.previewUrl?.let {
            NetworkImage(
                url = it,
                modifier = Modifier.clickable(
                    onClick = {
                        onClick(media)
                    }
                ),
            )
        }
    }
}
