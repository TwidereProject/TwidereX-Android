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
package com.twidere.twiderex.component.placeholder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.twidere.twiderex.component.status.NormalStatusDefaults
import com.twidere.twiderex.component.status.StatusBodyMediaDefaults
import com.twidere.twiderex.component.status.StatusContentDefaults
import com.twidere.twiderex.component.status.StatusMediaDefaults
import moe.tlaster.placeholder.Placeholder
import moe.tlaster.placeholder.TextPlaceHolder

@Composable
fun UiStatusPlaceholder(
    delayMillis: Long = 0,
) {
    Box(
        modifier = Modifier.padding(vertical = NormalStatusDefaults.ContentSpacing)
    ) {
        Row(
            modifier = Modifier.padding(NormalStatusDefaults.ContentPadding)
        ) {
            AvatarPlaceHolder(
                delayMillis = delayMillis,
            )
            Spacer(modifier = Modifier.width(StatusContentDefaults.AvatarSpacing))
            Column {
                TextPlaceHolder(
                    length = 5,
                    delayMillis = delayMillis,
                )
                Spacer(modifier = Modifier.height(StatusContentDefaults.Normal.BodySpacing))
                TextPlaceHolder(
                    length = 24,
                    delayMillis = delayMillis,
                )
                Spacer(modifier = Modifier.height(StatusBodyMediaDefaults.Spacing))
                Placeholder(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .aspectRatio(StatusMediaDefaults.DefaultAspectRatio)
                        .heightIn(max = StatusMediaDefaults.DefaultMaxHeight)
                )
            }
        }
    }
}
