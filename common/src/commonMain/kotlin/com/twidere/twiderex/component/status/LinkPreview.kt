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
package com.twidere.twiderex.component.status

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.foundation.NetworkImage
import com.twidere.twiderex.component.painterResource

@Composable
fun LinkPreview(
    link: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    image: String? = null,
    desc: String? = null,
    maxLines: Int = Int.MAX_VALUE
) {
    val styledModifier = Modifier
        .background(LinkPreviewDefaults.BackgroundColor, MaterialTheme.shapes.medium)
        .padding(1.dp)
        .clip(MaterialTheme.shapes.medium)
        .then(modifier)
    when {
        title == null && image == null && desc == null -> LinkOnlyPreview(
            modifier = styledModifier,
            link = link,
        )
        title != null && image == null && desc == null -> LinkWithTitlePreview(
            modifier = styledModifier,
            title = title,
            link = link,
        )
        title != null && image != null && desc == null -> LinkWithTitleAndSmallImagePreview(
            modifier = styledModifier,
            title = title,
            image = image,
            link = link,
        )
        title != null && image != null && desc != null -> LinkWithTitleAndLargeImagePreview(
            modifier = styledModifier,
            title = title,
            image = image,
            desc = desc,
            link = link,
            maxLines = maxLines,
        )
        title != null && image == null && desc != null -> LinkWithTitleAndDescPreview(
            modifier = styledModifier,
            title = title,
            desc = desc,
            link = link,
            maxLines = maxLines,
        )
    }
}

object LinkPreviewDefaults {
    val ContentPadding = PaddingValues(
        horizontal = 16.dp,
        vertical = 12.dp,
    )
    val TextPaddingStart = 16.dp

    val BackgroundColor
        @Composable
        get() = MaterialTheme.colors.onBackground.copy(alpha = 0.04f)

    val TitleStyle
        @Composable
        get() = MaterialTheme.typography.subtitle2

    val DescStyle
        @Composable
        get() = MaterialTheme.typography.caption
}

@Composable
private fun LinkOnlyPreview(
    link: String,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colors.primary
    ) {
        Box(
            modifier = modifier.padding(LinkPreviewDefaults.ContentPadding),
            contentAlignment = Alignment.CenterStart
        ) {
            Column {
                Text(text = "", style = LinkPreviewDefaults.TitleStyle)
                Text(text = "")
            }
            Row {
                Icon(
                    painter = painterResource(res = com.twidere.twiderex.MR.files.ic_planet),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(LinkPreviewDefaults.TextPaddingStart))
                Text(text = link, maxLines = 1)
            }
        }
    }
}

@Composable
private fun LinkWithTitlePreview(
    title: String,
    link: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(LinkPreviewDefaults.ContentPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colors.primary
        ) {
            Icon(painter = painterResource(res = com.twidere.twiderex.MR.files.ic_planet), contentDescription = null)
        }
        Spacer(modifier = Modifier.width(LinkPreviewDefaults.TextPaddingStart))
        Column {
            Text(text = title, style = LinkPreviewDefaults.TitleStyle)
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colors.primary
            ) {
                Text(text = link, maxLines = 1)
            }
        }
    }
}

@Composable
private fun LinkWithTitleAndSmallImagePreview(
    image: String,
    title: String,
    link: String,
    modifier: Modifier = Modifier,
) {
    Layout(
        modifier = modifier,
        content = {
            NetworkImage(
                data = image
            )
            Column(
                modifier = Modifier
                    .padding(LinkPreviewDefaults.ContentPadding)
            ) {
                Text(text = title, style = LinkPreviewDefaults.TitleStyle)
                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colors.primary
                ) {
                    Text(text = link, maxLines = 1)
                }
            }
        }
    ) { measurables, constraints ->
        val textPlaceable = measurables[1].measure(constraints = constraints)
        val imagePlaceable = measurables[0].measure(
            Constraints.fixed(
                textPlaceable.measuredHeight.ensureSizeNotInfinity(),
                textPlaceable.measuredHeight.ensureSizeNotInfinity()
            )
        )
        layout(
            width = minOf(
                textPlaceable.measuredWidth + imagePlaceable.measuredWidth,
                constraints.maxWidth
            ),
            height = textPlaceable.measuredHeight
        ) {
            imagePlaceable.placeRelative(0, 0)
            textPlaceable.placeRelative(textPlaceable.measuredHeight, 0)
        }
    }
}

@Composable
private fun LinkWithTitleAndDescPreview(
    title: String,
    desc: String,
    link: String,
    modifier: Modifier = Modifier,
    maxLines: Int,
) {
    Column(
        modifier = modifier.padding(LinkPreviewDefaults.ContentPadding),
    ) {
        Text(text = title, style = LinkPreviewDefaults.TitleStyle)
        Text(text = desc, style = LinkPreviewDefaults.DescStyle, maxLines = maxLines)
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colors.primary
        ) {
            Text(text = link, maxLines = 1)
        }
    }
}

@Composable
private fun LinkWithTitleAndLargeImagePreview(
    title: String,
    image: String,
    desc: String,
    link: String,
    modifier: Modifier = Modifier,
    maxLines: Int,
) {
    Layout(
        modifier = modifier,
        content = {
            NetworkImage(
                modifier = Modifier.aspectRatio(270f / 135f),
                data = image
            )
            Column(
                modifier = Modifier.padding(LinkPreviewDefaults.ContentPadding),
            ) {
                Text(text = title, style = LinkPreviewDefaults.TitleStyle)
                Text(text = desc, style = LinkPreviewDefaults.DescStyle, maxLines = maxLines)
                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colors.primary
                ) {
                    Text(text = link, maxLines = 1)
                }
            }
        }
    ) { measurables, constraints ->
        val textPlaceable = measurables[1].measure(constraints = constraints)
        val imagePlaceable =
            measurables[0].measure(constraints = constraints.copy(minWidth = textPlaceable.measuredWidth.ensureSizeNotInfinity()))
        layout(
            width = textPlaceable.measuredWidth,
            height = textPlaceable.measuredHeight + imagePlaceable.measuredHeight
        ) {
            imagePlaceable.placeRelative(0, 0)
            textPlaceable.placeRelative(0, imagePlaceable.measuredHeight)
        }
    }
}

private fun Int.ensureSizeNotInfinity(): Int {
    return if (this == Constraints.Infinity) {
        0
    } else {
        this
    }
}
