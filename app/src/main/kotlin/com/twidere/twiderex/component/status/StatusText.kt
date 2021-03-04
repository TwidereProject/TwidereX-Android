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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.model.ui.UiStatus

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ColumnScope.StatusText(
    status: UiStatus,
) {
    val expandable = status.platformType == PlatformType.Mastodon &&
        status.mastodonExtra?.spoilerText != null

    var expanded by rememberSaveable(
        saver = Saver(
            save = {
                it.value
            },
            restore = {
                mutableStateOf(it)
            },
        )
    ) { mutableStateOf(!expandable) }

    if (expandable && status.mastodonExtra?.spoilerText != null) {
        Text(text = status.mastodonExtra.spoilerText)
        Button(
            onClick = {
                expanded = !expanded
            }
        ) {
            Text(text = "expand")
        }
    }
    AnimatedVisibility(visible = expanded) {
        HtmlText(
            htmlText = status.htmlText,
            linkResolver = { href ->
                status.resolveLink(href)
            },
        )
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
