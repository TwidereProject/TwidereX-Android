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
package com.twidere.twiderex.scenes.home

import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.scenes.home.mastodon.FederatedTimelineItem
import com.twidere.twiderex.scenes.home.mastodon.LocalTimelineItem
import com.twidere.twiderex.scenes.home.mastodon.MastodonNotificationItem

enum class HomeMenus(
    val item: HomeNavigationItem,
    val showDefault: Boolean,
    val supportedPlatformType: List<PlatformType>,
) {
    HomeTimeline(
        item = HomeTimelineItem(),
        showDefault = true,
        supportedPlatformType = PlatformType.values().toList(),
    ),
    MastodonNotification(
        item = MastodonNotificationItem(),
        showDefault = true,
        supportedPlatformType = listOf(PlatformType.Mastodon),
    ),
    Mention(
        item = MentionItem(),
        showDefault = true,
        supportedPlatformType = listOf(PlatformType.Twitter),
    ),
    Search(
        item = SearchItem(),
        showDefault = true,
        supportedPlatformType = PlatformType.values().toList(),
    ),
    Me(
        item = MeItem(),
        showDefault = true,
        supportedPlatformType = PlatformType.values().toList(),
    ),
    Message(
        item = DMConversationListItem(),
        showDefault = false,
        supportedPlatformType = listOf(PlatformType.Twitter),
    ),
    FederatedTimeline(
        item = FederatedTimelineItem(),
        showDefault = false,
        supportedPlatformType = listOf(PlatformType.Mastodon),
    ),
    LocalTimeline(
        item = LocalTimelineItem(),
        showDefault = false,
        supportedPlatformType = listOf(PlatformType.Mastodon),
    ),
    Draft(
        item = DraftNavigationItem(),
        showDefault = false,
        supportedPlatformType = PlatformType.values().toList(),
    ),
    Lists(
        item = ListsNavigationItem(),
        showDefault = false,
        supportedPlatformType = PlatformType.values().toList(),
    )
}
