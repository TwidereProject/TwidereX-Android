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
package com.twidere.twiderex.di.assisted

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.twidere.twiderex.viewmodel.MediaViewModel
import com.twidere.twiderex.viewmodel.compose.ComposeViewModel
import com.twidere.twiderex.viewmodel.compose.DraftComposeViewModel
import com.twidere.twiderex.viewmodel.compose.DraftItemViewModel
import com.twidere.twiderex.viewmodel.search.SearchInputViewModel
import com.twidere.twiderex.viewmodel.timeline.HomeTimelineViewModel
import com.twidere.twiderex.viewmodel.timeline.MentionsTimelineViewModel
import com.twidere.twiderex.viewmodel.twitter.TwitterSignInViewModel
import com.twidere.twiderex.viewmodel.twitter.TwitterStatusViewModel
import com.twidere.twiderex.viewmodel.twitter.search.TwitterSearchMediaViewModel
import com.twidere.twiderex.viewmodel.twitter.search.TwitterSearchTweetsViewModel
import com.twidere.twiderex.viewmodel.twitter.user.TwitterUserViewModel
import com.twidere.twiderex.viewmodel.user.UserFavouriteTimelineViewModel
import com.twidere.twiderex.viewmodel.user.UserMediaTimelineViewModel
import com.twidere.twiderex.viewmodel.user.UserTimelineViewModel
import com.twidere.twiderex.viewmodel.user.UserViewModel
import javax.inject.Inject

@Composable
inline fun <reified AF : IAssistedFactory, reified VM : ViewModel> assistedViewModel(
    vararg dependsOn: Any,
    noinline creator: ((AF) -> VM)? = null,
): VM {
    val factories = LocalAssistedFactories.current
    val factory = factories.firstOrNull { AF::class.java.isInstance(it) } as? AF
    return viewModel(
        if (dependsOn.any()) {
            dependsOn.joinToString { it.hashCode().toString() } + VM::class.java.canonicalName
        } else {
            null
        },
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return factory?.let { creator?.invoke(it) } as T
            }
        }
    )
}

interface IAssistedFactory

data class AssistedViewModelFactoryHolder @Inject constructor(
    val homeTimelineViewModelFactory: HomeTimelineViewModel.AssistedFactory,
    val twitterStatusViewModelFactory: TwitterStatusViewModel.AssistedFactory,
    val mentionsTimelineViewModelFactory: MentionsTimelineViewModel.AssistedFactory,
    val twitterSearchMediaViewModelFactory: TwitterSearchMediaViewModel.AssistedFactory,
    val twitterSearchTweetsViewModelFactory: TwitterSearchTweetsViewModel.AssistedFactory,
    val userFavouriteTimelineViewModelFactory: UserFavouriteTimelineViewModel.AssistedFactory,
    val userTimelineViewModelFactory: UserTimelineViewModel.AssistedFactory,
    val userMediaTimelineViewModelFactory: UserMediaTimelineViewModel.AssistedFactory,
    val userViewModelFactory: UserViewModel.AssistedFactory,
    val composeViewModelFactory: ComposeViewModel.AssistedFactory,
    val mediaViewModelFactory: MediaViewModel.AssistedFactory,
    val searchInputViewModelFactory: SearchInputViewModel.AssistedFactory,
    val draftItemViewModelFactory: DraftItemViewModel.AssistedFactory,
    val draftComposeViewModelFactory: DraftComposeViewModel.AssistedFactory,
    val twitterSignInViewModelViewModelFactory: TwitterSignInViewModel.AssistedFactory,
    val twitterUserViewModelFactory: TwitterUserViewModel.AssistedFactory,
)

@Composable
fun ProvideAssistedFactory(
    factoryHolder: AssistedViewModelFactoryHolder,
    content: @Composable () -> Unit,
) {
    val factory = remember {
        listOf(
            factoryHolder.homeTimelineViewModelFactory,
            factoryHolder.twitterStatusViewModelFactory,
            factoryHolder.mentionsTimelineViewModelFactory,
            factoryHolder.twitterSearchMediaViewModelFactory,
            factoryHolder.twitterSearchTweetsViewModelFactory,
            factoryHolder.userFavouriteTimelineViewModelFactory,
            factoryHolder.userTimelineViewModelFactory,
            factoryHolder.userMediaTimelineViewModelFactory,
            factoryHolder.userViewModelFactory,
            factoryHolder.composeViewModelFactory,
            factoryHolder.mediaViewModelFactory,
            factoryHolder.searchInputViewModelFactory,
            factoryHolder.draftItemViewModelFactory,
            factoryHolder.draftComposeViewModelFactory,
            factoryHolder.twitterSignInViewModelViewModelFactory,
            factoryHolder.twitterUserViewModelFactory,
        )
    }
    Providers(
        LocalAssistedFactories provides factory
    ) {
        content.invoke()
    }
}

val LocalAssistedFactories = staticCompositionLocalOf<List<IAssistedFactory>>()
