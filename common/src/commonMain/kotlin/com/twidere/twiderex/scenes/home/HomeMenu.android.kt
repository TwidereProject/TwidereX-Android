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
package com.twidere.twiderex.scenes.home

import com.twidere.twiderex.model.HomeMenus
import com.twidere.twiderex.model.HomeNavigationItem
import com.twidere.twiderex.scenes.home.mastodon.FederatedTimelineItem
import com.twidere.twiderex.scenes.home.mastodon.LocalTimelineItem
import com.twidere.twiderex.scenes.home.mastodon.MastodonNotificationItem

private val itemMap by lazy {
    mutableMapOf(
        HomeMenus.HomeTimeline to HomeTimelineItem(),
        HomeMenus.MastodonNotification to MastodonNotificationItem(),
        HomeMenus.Mention to MentionItem(),
        HomeMenus.Search to SearchItem(),
        HomeMenus.Me to MeItem(),
        HomeMenus.Message to DMConversationListItem(),
        HomeMenus.LocalTimeline to LocalTimelineItem(),
        HomeMenus.FederatedTimeline to FederatedTimelineItem(),
        HomeMenus.Draft to DraftNavigationItem(),
        HomeMenus.Lists to ListsNavigationItem(),
    )
}

val HomeMenus.item: HomeNavigationItem
    get() = itemMap.getValue(this)
