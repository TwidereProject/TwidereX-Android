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

import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.NetworkImage
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.preferences.LocalDisplayPreferences
import com.twidere.twiderex.preferences.proto.DisplayPreferences
import moe.tlaster.placeholder.Placeholder

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
        Box(
            modifier = modifier
                .let {
                    if (withPlatformIcon) {
                        it.padding(bottom = 4.dp, end = 4.dp)
                    } else {
                        it
                    }
                }
                .withAvatarClip()
                .clipToBounds()
        ) {
            NetworkImage(
                data = user.profileImage,
                modifier = Modifier
                    .clickable(
                        onClick = {
                            onClick?.invoke() ?: run {
                                navigator.user(user)
                            }
                        }
                    )
                    .size(size),
                placeholder = {
                    Placeholder(modifier = Modifier.size(size))
                },
            )
        }
        if (withPlatformIcon) {
            val icon = when (user.platformType) {
                PlatformType.Twitter -> painterResource(id = R.drawable.ic_twitter_badge)
                PlatformType.StatusNet -> TODO()
                PlatformType.Fanfou -> TODO()
                PlatformType.Mastodon -> painterResource(id = R.drawable.ic_mastodon_badge)
            }
            Image(
                painter = icon,
                contentDescription = user.platformType.name,
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
                DisplayPreferences.AvatarStyle.Square, DisplayPreferences.AvatarStyle.UNRECOGNIZED, null -> 10
            }
        }
        this.clip(RoundedCornerShape(percent = percent))
    }
}

object UserAvatarDefaults {
    val AvatarSize = 44.dp
}
