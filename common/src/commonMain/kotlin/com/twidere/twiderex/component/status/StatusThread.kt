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

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.model.ui.UiStatus

@Composable
fun StatusThreadWithAvatar(modifier: Modifier = Modifier, data: UiStatus, onClick: () -> Unit) {
    Row(modifier = modifier) {
        UserAvatar(
            user = data.user,
            size = StatusThreadDefaults.AvatarSize,
            modifier = Modifier.padding(start = StatusThreadDefaults.HorizontalSpacing)
        )
        TextButton(
            onClick = onClick,
            modifier = Modifier
                .padding(start = StatusThreadDefaults.HorizontalSpacing)
                .height(StatusThreadDefaults.AvatarSize)
        ) {
            Text(
                text = stringResource(res = com.twidere.twiderex.MR.strings.common_controls_status_thread_show),
                style = MaterialTheme.typography.body2
            )
        }
    }
}

@Composable
fun StatusThreadTextOnly(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(modifier = modifier) {
        TextButton(
            onClick = onClick,
        ) {
            Text(
                text = stringResource(res = com.twidere.twiderex.MR.strings.common_controls_status_thread_show),
                style = MaterialTheme.typography.body2
            )
        }
    }
}

object StatusThreadDefaults {
    val AvatarSize = 32.dp
    val HorizontalSpacing = (UserAvatarDefaults.AvatarSize - AvatarSize) / 2
}

enum class StatusThreadStyle(val lineDown: Boolean) {
    NONE(false),
    WITH_AVATAR(true),
    TEXT_ONLY(false)
}
