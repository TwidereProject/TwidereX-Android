/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.component.status

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Reply
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.ui.mediumEmphasisContentContentColor
import com.twidere.twiderex.ui.profileImageSize
import com.twidere.twiderex.ui.standardPadding

@Composable
fun RetweetHeader(
    data: UiStatus,
) {
    Row {
        Box(
            modifier = Modifier
                .width(profileImageSize),
            alignment = Alignment.CenterEnd
        ) {
            Icon(asset = Icons.Default.Reply, tint = mediumEmphasisContentContentColor)
        }
        Spacer(modifier = Modifier.width(standardPadding))
        Text(
            text = data.user.name + "retweet this tweet",
            color = mediumEmphasisContentContentColor
        )
    }
}
