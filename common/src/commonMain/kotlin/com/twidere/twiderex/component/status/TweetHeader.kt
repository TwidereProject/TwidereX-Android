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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.model.ui.UiStatus

@Composable
fun RetweetHeader(
    data: UiStatus,
) {
    TweetHeader(
        icon = {
            Icon(
                painter = painterResource(res = com.twidere.twiderex.MR.files.ic_repeat),
                contentDescription = stringResource(res = com.twidere.twiderex.MR.strings.accessibility_common_status_retweeted),
                tint = Color(0xFF4C9EEB)
            )
        },
        text = {
            HtmlText(
                textStyle = MaterialTheme.typography.caption,
                htmlText = stringResource(
                    res = com.twidere.twiderex.MR.strings.common_controls_status_user_retweeted,
                    data.user.displayName
                ),
            )
        },
    )
}

@Composable
fun TweetHeader(
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
) {
    ProvideTextStyle(value = MaterialTheme.typography.caption) {
        CompositionLocalProvider(
            LocalContentAlpha provides ContentAlpha.medium
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .width(UserAvatarDefaults.AvatarSize),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    Box(modifier = Modifier.size(MaterialTheme.typography.body1.fontSize.value.dp)) {
                        icon.invoke()
                    }
                }
                Spacer(modifier = Modifier.width(TweetHeaderDefaults.IconSpacing))
                text.invoke()
            }
        }
    }
}

object TweetHeaderDefaults {
    val IconSpacing = 8.dp
}
