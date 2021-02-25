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

import androidx.compose.runtime.Composable
import com.twidere.twiderex.model.ui.UiStatus

@Composable
fun StatusText(
    status: UiStatus,
) {
    HtmlText(
        htmlText = status.htmlText,
    ) { href ->
        status.resolveLink(href)
    }
}

fun UiStatus.resolveLink(href: String): ResolvedLink {
    val entity = url.firstOrNull { it.url == href }
    val media = media.firstOrNull { it.url == href }
    return when {
        entity != null -> {
            if (!entity.displayUrl.contains("pic.twitter.com") &&
                !(quote != null && entity.expandedUrl.endsWith(quote.statusId))
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
