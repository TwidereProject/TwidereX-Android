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
package com.twidere.twiderex.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.Dp
import androidx.navigation.compose.navigate
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.settings.AmbientAvatarStyle
import com.twidere.twiderex.settings.AvatarStyle
import com.twidere.twiderex.ui.AmbientInStoryboard
import com.twidere.twiderex.ui.AmbientNavController
import com.twidere.twiderex.ui.profileImageSize

@Composable
fun UserAvatar(
    user: UiUser,
    size: Dp = profileImageSize
) {
    val navController = AmbientNavController.current
    val inStoryBoard = AmbientInStoryboard.current
    val avatarStyle = AmbientAvatarStyle.current
    Box(
        modifier = Modifier
            .let {
                when (avatarStyle) {
                    AvatarStyle.Round -> it.clip(CircleShape)
                    AvatarStyle.Square -> it.clip(MaterialTheme.shapes.medium)
                }
            }
            .clipToBounds()
    ) {
        NetworkImage(
            url = user.profileImage,
            modifier = Modifier
                .clickable(
                    onClick = {
                        if (!inStoryBoard) {
                            navController.navigate("user/${user.screenName}")
                        }
                    }
                )
                .width(size)
                .height(size)
        )
    }
}
