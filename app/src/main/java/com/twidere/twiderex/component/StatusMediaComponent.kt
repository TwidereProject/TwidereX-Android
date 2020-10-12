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
package com.twidere.twiderex.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.R
import com.twidere.twiderex.extensions.NavControllerAmbient
import com.twidere.twiderex.fragment.MediaFragmentArgs
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.model.ui.UiStatus

@Composable
fun StatusMediaComponent(
    status: UiStatus,
) {
    val media = status.media
    val navController = NavControllerAmbient.current
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
