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

import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.style.TextOverflow
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.ui.LocalActiveAccount

@Composable
fun UserScreenName(user: UiUser) {
    val currentAccount = LocalActiveAccount.current ?: return
    CompositionLocalProvider(
        LocalContentAlpha provides ContentAlpha.medium
    ) {
        val text = if (currentAccount.accountKey.host != user.userKey.host) {
            "@${user.screenName}@${user.userKey.host}"
        } else {
            "@${user.screenName}"
        }
        Text(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun UserName(user: UiUser) {
    Text(
        text = user.name.takeIf { it.isNotEmpty() } ?: user.screenName,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}
