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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.R
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.ui.UiStatus

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ColumnScope.StatusText(
    status: UiStatus,
    maxLines: Int = Int.MAX_VALUE,
    showMastodonPoll: Boolean = true
) {
    val expandable = status.platformType == PlatformType.Mastodon &&
        status.mastodonExtra?.spoilerText != null

    var expanded by rememberSaveable { mutableStateOf(!expandable) }

    if (expandable && status.mastodonExtra?.spoilerText != null) {
        Text(text = status.mastodonExtra.spoilerText)
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
                modifier = Modifier.padding(StatusTextDefaults.Mastodon.SpoilerButtonPadding),
                painter = painterResource(id = R.drawable.ic_expand_more),
                contentDescription = null,
                tint = MaterialTheme.colors.primary,
            )
        }
    }
    AnimatedVisibility(visible = expanded) {
        Column {
            HtmlText(
                htmlText = status.htmlText,
                maxLines = maxLines,
                linkResolver = { href ->
                    status.resolveLink(href)
                },
            )

            if (showMastodonPoll && status.platformType == PlatformType.Mastodon && status.mastodonExtra?.poll != null) {
                Spacer(modifier = Modifier.height(StatusTextDefaults.Mastodon.PollSpacing))
                MastodonPoll(status)
            }
        }
    }
}

object StatusTextDefaults {
    object Mastodon {
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
