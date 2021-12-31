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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.ui.UiStatus

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ColumnScope.StatusText(
    status: UiStatus,
    maxLines: Int = Int.MAX_VALUE,
    showMastodonPoll: Boolean = true,
    isSelectionAble: Boolean = true,
) {
    val expandable = status.platformType == PlatformType.Mastodon &&
        status.spoilerText != null

    var expanded by rememberSaveable { mutableStateOf(!expandable) }

    if (expandable && status.spoilerText != null) {
        Text(text = status.spoilerText)
        Spacer(modifier = Modifier.height(StatusTextDefaults.Mastodon.SpoilerSpacing))
        Row(
            modifier = Modifier
                .background(
                    LocalContentColor.current.copy(alpha = 0.04f),
                    shape = MaterialTheme.shapes.medium,
                )
                .clip(MaterialTheme.shapes.medium)
                .clickable {
                    expanded = !expanded
                },
        ) {
            Icon(
                modifier = Modifier.size(width = StatusTextDefaults.Mastodon.MoreButton.Width, height = StatusTextDefaults.Mastodon.MoreButton.Height).padding(StatusTextDefaults.Mastodon.SpoilerButtonPadding),
                painter = painterResource(res = com.twidere.twiderex.MR.files.ic_expand_more),
                contentDescription = null,
                tint = MaterialTheme.colors.primary,
            )
        }
    }
    AnimatedVisibility(visible = expanded) {
        Column {
            SelectionContainer(enable = isSelectionAble) {
                HtmlText(
                    modifier = Modifier.fillMaxWidth(),
                    htmlText = status.htmlText,
                    maxLines = maxLines,
                    linkResolver = { href ->
                        status.resolveLink(href)
                    },
                    positionWrapper = it
                )
            }
            if (showMastodonPoll && status.platformType == PlatformType.Mastodon && status.poll != null) {
                Spacer(modifier = Modifier.height(StatusTextDefaults.Mastodon.PollSpacing))
                MastodonPoll(status)
            }
        }
    }
}

object StatusTextDefaults {
    object Mastodon {
        object MoreButton {
            val Width = 46.dp
            val Height = 20.dp
        }
        val SpoilerSpacing = 2.dp
        val SpoilerButtonPadding = PaddingValues(
            2.dp
        )
        val PollSpacing = 10.dp
    }
}

fun UiStatus.resolveLink(
    href: String,
): ResolvedLink {
    val entity = url.firstOrNull { it.url == href }
    val media = media.firstOrNull { it.url == href }
    return when {
        entity != null -> {
            if (!entity.displayUrl.contains("pic.twitter.com") &&
                quote?.let { entity.expandedUrl.endsWith(it.statusId) } != true
            ) {
                ResolvedLink(
                    expanded = entity.expandedUrl,
                    display = entity.displayUrl,
                )
            } else {
                ResolvedLink(expanded = null, skip = true)
            }
        }
        media != null -> {
            ResolvedLink(expanded = null, skip = true)
        }
        else -> {
            ResolvedLink(expanded = null)
        }
    }
}
