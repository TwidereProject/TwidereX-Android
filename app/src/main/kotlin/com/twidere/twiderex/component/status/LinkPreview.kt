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
package com.twidere.twiderex.component.status

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.NetworkImage

@Composable
fun LinkPreview(
    link: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    image: String? = null,
    desc: String? = null,
) {
    val styledModifier = Modifier
        .clip(MaterialTheme.shapes.medium)
        .background(Color.Black.copy(alpha = 0.04f))
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
        )
        title != null && image == null && desc != null -> LinkWithTitleAndDescPreview(
            modifier = styledModifier,
            title = title,
            desc = desc,
            link = link,
        )
    }
}

object LinkPreviewDefaults {
    val ContentPadding = PaddingValues(
        horizontal = 16.dp,
        vertical = 12.dp,
    )
    val TextPaddingStart = 16.dp
}

@Composable
private fun LinkOnlyPreview(
    link: String,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colors.primary
    ) {
        Row(
            modifier.padding(LinkPreviewDefaults.ContentPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_planet),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(LinkPreviewDefaults.TextPaddingStart))
            Text(text = link)
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
        modifier = modifier.padding(LinkPreviewDefaults.ContentPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colors.primary
        ) {
            Icon(painter = painterResource(id = R.drawable.ic_planet), contentDescription = null)
        }
        Spacer(modifier = Modifier.width(LinkPreviewDefaults.TextPaddingStart))
        Column {
            Text(text = title, style = MaterialTheme.typography.subtitle2)
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colors.primary
            ) {
                Text(text = link)
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
    Row(
        modifier = modifier,
    ) {
        NetworkImage(
            data = image
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(LinkPreviewDefaults.ContentPadding)
        ) {
            Text(text = title, style = MaterialTheme.typography.subtitle2)
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colors.primary
            ) {
                Text(text = link)
            }
        }
    }
}

@Composable
private fun LinkWithTitleAndDescPreview(
    title: String,
    desc: String,
    link: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(LinkPreviewDefaults.ContentPadding),
    ) {
        Text(text = title, style = MaterialTheme.typography.subtitle2)
        CompositionLocalProvider(
            LocalContentAlpha provides ContentAlpha.medium
        ) {
            Text(text = desc)
        }
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colors.primary
        ) {
            Text(text = link)
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
) {
    Column(
        modifier = modifier,
    ) {
        NetworkImage(
            modifier = Modifier.aspectRatio(270f / 135f),
            data = image
        )
        Column(
            modifier = Modifier.padding(LinkPreviewDefaults.ContentPadding),
        ) {
            Text(text = title, style = MaterialTheme.typography.subtitle2)
            CompositionLocalProvider(
                LocalContentAlpha provides ContentAlpha.medium
            ) {
                Text(text = desc)
            }
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colors.primary
            ) {
                Text(text = link)
            }
        }
    }
}
