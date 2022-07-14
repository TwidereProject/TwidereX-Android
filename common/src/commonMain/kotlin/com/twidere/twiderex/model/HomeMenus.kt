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
package com.twidere.twiderex.model

import com.twidere.twiderex.model.enums.PlatformType

enum class HomeMenus(
    val showDefault: Boolean,
    val supportedPlatformType: List<PlatformType>,
) {
    HomeTimeline(
        showDefault = true,
        supportedPlatformType = PlatformType.values().toList(),
    ),
    MastodonNotification(
        showDefault = true,
        supportedPlatformType = listOf(PlatformType.Mastodon),
    ),
    Mention(
        showDefault = true,
        supportedPlatformType = listOf(PlatformType.Twitter),
    ),
    Search(
        showDefault = true,
        supportedPlatformType = PlatformType.values().toList(),
    ),
    Me(
        showDefault = true,
        supportedPlatformType = PlatformType.values().toList(),
    ),
    Message(
        showDefault = false,
        supportedPlatformType = listOf(PlatformType.Twitter),
    ),
    LocalTimeline(
        showDefault = false,
        supportedPlatformType = listOf(PlatformType.Mastodon),
    ),
    FederatedTimeline(
        showDefault = false,
        supportedPlatformType = listOf(PlatformType.Mastodon),
    ),
    Draft(
        showDefault = false,
        supportedPlatformType = PlatformType.values().toList(),
    ),
    Lists(
        showDefault = false,
        supportedPlatformType = PlatformType.values().toList(),
    )
}
