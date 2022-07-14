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

import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.preferences.LocalDisplayPreferences
import com.twidere.twiderex.preferences.model.DisplayPreferences

@Composable
fun UserAvatar(
    modifier: Modifier = Modifier,
    user: UiUser,
    size: Dp = UserAvatarDefaults.AvatarSize,
    withPlatformIcon: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val navigator = LocalNavigator.current
    Box(
        contentAlignment = Alignment.BottomEnd
    ) {
        RoundAvatar(
            modifier = modifier,
            avatar = user.profileImage.takeIf { it.isNotEmpty() } ?: painterResource(com.twidere.twiderex.MR.images.ic_profile_image_twidere),
            size = size,
            onClick = {
                onClick?.invoke() ?: run { navigator.user(user) }
            }
        )
        if (withPlatformIcon) {
            val icon = when (user.platformType) {
                PlatformType.Twitter -> painterResource(res = com.twidere.twiderex.MR.files.ic_twitter_badge)
                PlatformType.StatusNet -> TODO()
                PlatformType.Fanfou -> TODO()
                PlatformType.Mastodon -> painterResource(res = com.twidere.twiderex.MR.files.ic_mastodon_badge)
            }
            Image(
                painter = icon,
                contentDescription = user.platformType.name,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

fun Modifier.withAvatarClip(): Modifier {
    return composed {
        val transition =
            updateTransition(targetState = LocalDisplayPreferences.current.avatarStyle)
        val percent by transition.animateInt {
            when (it) {
                DisplayPreferences.AvatarStyle.Round -> 50
                DisplayPreferences.AvatarStyle.Square -> 10
            }
        }
        this.clip(RoundedCornerShape(percent = percent))
    }
}

object UserAvatarDefaults {
    val AvatarSize = 44.dp
}
