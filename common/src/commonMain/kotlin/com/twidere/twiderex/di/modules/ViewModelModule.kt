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
package com.twidere.twiderex.di.modules

import com.twidere.twiderex.extensions.viewModel
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.ComposeType
import com.twidere.twiderex.model.ui.UiDraft
import com.twidere.twiderex.preferences.PreferencesHolder
import com.twidere.twiderex.viewmodel.ActiveAccountViewModel
import com.twidere.twiderex.viewmodel.DraftViewModel
import com.twidere.twiderex.viewmodel.MediaViewModel
import com.twidere.twiderex.viewmodel.PureMediaViewModel
import com.twidere.twiderex.viewmodel.StatusViewModel
import com.twidere.twiderex.viewmodel.compose.ComposeSearchUserViewModel
import com.twidere.twiderex.viewmodel.compose.ComposeViewModel
import com.twidere.twiderex.viewmodel.compose.DraftComposeViewModel
import com.twidere.twiderex.viewmodel.compose.DraftItemViewModel
import com.twidere.twiderex.viewmodel.compose.MastodonComposeSearchHashtagViewModel
import com.twidere.twiderex.viewmodel.dm.DMConversationViewModel
import com.twidere.twiderex.viewmodel.dm.DMEventViewModel
import com.twidere.twiderex.viewmodel.dm.DMNewConversationViewModel
import com.twidere.twiderex.viewmodel.gif.GifViewModel
import com.twidere.twiderex.viewmodel.lists.ListsAddMemberViewModel
import com.twidere.twiderex.viewmodel.lists.ListsCreateViewModel
import com.twidere.twiderex.viewmodel.lists.ListsModifyViewModel
import com.twidere.twiderex.viewmodel.lists.ListsSearchUserViewModel
import com.twidere.twiderex.viewmodel.lists.ListsTimelineViewModel
import com.twidere.twiderex.viewmodel.lists.ListsUserViewModel
import com.twidere.twiderex.viewmodel.lists.ListsViewModel
import com.twidere.twiderex.viewmodel.mastodon.MastodonHashtagViewModel
import com.twidere.twiderex.viewmodel.mastodon.MastodonSearchHashtagViewModel
import com.twidere.twiderex.viewmodel.mastodon.MastodonSignInViewModel
import com.twidere.twiderex.viewmodel.search.SearchInputViewModel
import com.twidere.twiderex.viewmodel.search.SearchSaveViewModel
import com.twidere.twiderex.viewmodel.search.SearchTweetsViewModel
import com.twidere.twiderex.viewmodel.search.SearchUserViewModel
import com.twidere.twiderex.viewmodel.settings.AccountNotificationViewModel
import com.twidere.twiderex.viewmodel.settings.AppearanceViewModel
import com.twidere.twiderex.viewmodel.settings.DisplayViewModel
import com.twidere.twiderex.viewmodel.settings.LayoutViewModel
import com.twidere.twiderex.viewmodel.settings.MiscViewModel
import com.twidere.twiderex.viewmodel.settings.NotificationViewModel
import com.twidere.twiderex.viewmodel.settings.StorageViewModel
import com.twidere.twiderex.viewmodel.timeline.HomeTimelineViewModel
import com.twidere.twiderex.viewmodel.timeline.MentionsTimelineViewModel
import com.twidere.twiderex.viewmodel.timeline.NotificationTimelineViewModel
import com.twidere.twiderex.viewmodel.timeline.mastodon.FederatedTimelineViewModel
import com.twidere.twiderex.viewmodel.timeline.mastodon.LocalTimelineViewModel
import com.twidere.twiderex.viewmodel.trend.TrendViewModel
import com.twidere.twiderex.viewmodel.twitter.TwitterSignInViewModel
import com.twidere.twiderex.viewmodel.twitter.search.TwitterSearchMediaViewModel
import com.twidere.twiderex.viewmodel.twitter.user.TwitterUserViewModel
import com.twidere.twiderex.viewmodel.user.FollowersViewModel
import com.twidere.twiderex.viewmodel.user.FollowingViewModel
import com.twidere.twiderex.viewmodel.user.UserFavouriteTimelineViewModel
import com.twidere.twiderex.viewmodel.user.UserMediaTimelineViewModel
import com.twidere.twiderex.viewmodel.user.UserTimelineViewModel
import com.twidere.twiderex.viewmodel.user.UserViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { (statusKey: MicroBlogKey) -> StatusViewModel(get(), get(), statusKey) }
    viewModel { (belongKey: MicroBlogKey) -> PureMediaViewModel(get(), belongKey) }
    viewModel { (statusKey: MicroBlogKey) -> MediaViewModel(get(), get(), get(), statusKey) }
    viewModel { DraftViewModel(get(), get()) }
    viewModel { ActiveAccountViewModel(get()) }

    user()
    twitter()
    trend()
    timeline()
    settings()
    search()
    mastodon()
    lists()
    dm()
    compose()
    gif()
}

private fun Module.compose() {
    viewModel { MastodonComposeSearchHashtagViewModel(get()) }
    viewModel { (draftId: String) -> DraftItemViewModel(get(), draftId) }
    viewModel { (draft: UiDraft) ->
        DraftComposeViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            draft
        )
    }
    viewModel { (statusKey: MicroBlogKey?, composeType: ComposeType) ->
        ComposeViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            statusKey,
            composeType,
        )
    }
    viewModel { ComposeSearchUserViewModel(get()) }
}

private fun Module.dm() {
    viewModel { DMConversationViewModel(get(), get()) }
    viewModel { (conversationKey: MicroBlogKey) ->
        DMEventViewModel(
            get(),
            get(),
            get(),
            conversationKey
        )
    }
    viewModel { DMNewConversationViewModel(get(), get()) }
}

private fun Module.lists() {
    viewModel { (following: Boolean) -> ListsSearchUserViewModel(get(), following) }
    viewModel { (listKey: MicroBlogKey) -> ListsTimelineViewModel(get(), get(), listKey) }
    viewModel { (listId: String) -> ListsAddMemberViewModel(get(), get(), get(), listId) }
    viewModel { (listId: String, viewMembers: Boolean) ->
        ListsUserViewModel(
            get(),
            get(),
            listId,
            viewMembers
        )
    }
    viewModel { ListsViewModel(get(), get()) }
    viewModel {
        ListsCreateViewModel(
            get(),
            get(),
            get(),
        )
    }
    viewModel { (listKey: MicroBlogKey) -> ListsModifyViewModel(get(), get(), get(), listKey) }
}

private fun Module.mastodon() {
    viewModel { (keyword: String) -> MastodonHashtagViewModel(get(), get(), keyword) }
    viewModel { (keyword: String) -> MastodonSearchHashtagViewModel(get(), keyword) }
    viewModel { MastodonSignInViewModel(get(), get(), get()) }
}

private fun Module.search() {
    viewModel { (keyword: String) -> SearchInputViewModel(get(), get(), keyword) }
    viewModel { (content: String) -> SearchSaveViewModel(get(), get(), content) }
    viewModel { (keyword: String) -> SearchTweetsViewModel(get(), get(), keyword) }
    viewModel { (keyword: String) -> SearchUserViewModel(get(), keyword) }
}

private fun Module.settings() {
    viewModel { AccountNotificationViewModel(get()) }
    viewModel { AppearanceViewModel(get<PreferencesHolder>().appearancePreferences) }
    viewModel { DisplayViewModel(get<PreferencesHolder>().displayPreferences) }
    viewModel { LayoutViewModel(get()) }
    viewModel { MiscViewModel(get<PreferencesHolder>().miscPreferences, get(), get()) }
    viewModel { NotificationViewModel(get<PreferencesHolder>().notificationPreferences) }
    viewModel { StorageViewModel(get()) }
}

private fun Module.timeline() {
    viewModel { NotificationTimelineViewModel(get(), get(), get(), get()) }
    viewModel { MentionsTimelineViewModel(get(), get(), get(), get()) }
    viewModel { HomeTimelineViewModel(get(), get(), get()) }
    viewModel { LocalTimelineViewModel(get(), get(), get()) }
    viewModel { FederatedTimelineViewModel(get(), get(), get()) }
}

private fun Module.trend() {
    viewModel { TrendViewModel(get(), get()) }
}

private fun Module.twitter() {
    viewModel { (
        consumerKey: String,
        consumerSecret: String,
        pinCodeProvider: suspend (url: String) -> String?,
        onResult: (success: Boolean) -> Unit,
    ) ->
        TwitterSignInViewModel(
            get(),
            get(),
            consumerKey,
            consumerSecret,
            get(),
            pinCodeProvider,
            onResult,
        )
    }
    viewModel { (screenName: String) -> TwitterUserViewModel(get(), get(), get(), screenName) }
    viewModel { (keyword: String) -> TwitterSearchMediaViewModel(get(), get(), keyword) }
}

private fun Module.user() {
    viewModel { (userKey: MicroBlogKey) -> UserViewModel(get(), get(), get(), userKey) }
    viewModel { (userKey: MicroBlogKey) -> UserTimelineViewModel(get(), get(), userKey) }
    viewModel { (userKey: MicroBlogKey) -> UserMediaTimelineViewModel(get(), get(), userKey) }
    viewModel { (userKey: MicroBlogKey) -> UserFavouriteTimelineViewModel(get(), get(), userKey) }
    viewModel { (userKey: MicroBlogKey) -> FollowingViewModel(get(), get(), userKey) }
    viewModel { (userKey: MicroBlogKey) -> FollowersViewModel(get(), get(), userKey) }
}

private fun Module.gif() {
    viewModel { GifViewModel(get(), get(), get()) }
}
