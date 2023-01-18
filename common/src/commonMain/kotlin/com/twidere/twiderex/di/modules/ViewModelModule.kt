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
import com.twidere.twiderex.viewmodel.ActiveAccountViewModel
import com.twidere.twiderex.viewmodel.DraftViewModel
import com.twidere.twiderex.viewmodel.PureMediaViewModel
import com.twidere.twiderex.viewmodel.StatusViewModel
import com.twidere.twiderex.viewmodel.compose.ComposeSearchUserViewModel
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
import com.twidere.twiderex.viewmodel.lists.ListsViewModel
import com.twidere.twiderex.viewmodel.mastodon.MastodonHashtagViewModel
import com.twidere.twiderex.viewmodel.mastodon.MastodonSignInViewModel
import com.twidere.twiderex.viewmodel.twitter.TwitterSignInViewModel
import com.twidere.twiderex.viewmodel.twitter.user.TwitterUserViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val viewModelModule = module {
  viewModel { (statusKey: MicroBlogKey) -> StatusViewModel(get(), get(), statusKey) }
  viewModel { (belongKey: MicroBlogKey) -> PureMediaViewModel(get(), belongKey) }
  viewModel { DraftViewModel(get(), get()) }
  viewModel { ActiveAccountViewModel(get()) }

  twitter()
  mastodon()
  lists()
  dm()
  compose()
  gif()
}

private fun Module.compose() {
  viewModel { MastodonComposeSearchHashtagViewModel(get()) }
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
  viewModel { MastodonSignInViewModel(get(), get(), get()) }
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
}

private fun Module.gif() {
  viewModel { GifViewModel(get(), get(), get()) }
}
