package com.twidere.twiderex.viewmodel.user

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.twidere.services.microblog.TimelineService
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.repository.TimelineRepository
import com.twidere.twiderex.scenes.CurrentAccountPresenter
import com.twidere.twiderex.scenes.CurrentAccountState

@Composable
fun UserFavouriteTimelinePresenter(
    repository: TimelineRepository = get(),
    userKey: MicroBlogKey
): UserFavouriteTimelineState {
    val currentAccount = CurrentAccountPresenter()

    if (currentAccount !is CurrentAccountState.Account) {
        return UserFavouriteTimelineState.NoAccount
    }

    val source = remember(currentAccount) {
        repository.favouriteTimeline(
            userKey = userKey,
            accountKey = currentAccount.account.accountKey,
            platformType = currentAccount.account.type,
            service = currentAccount.account.service as TimelineService
        )
    }.collectAsLazyPagingItems()
    return UserFavouriteTimelineState.Data(source = source)
}

interface UserFavouriteTimelineState {
    data class Data(
        val source: LazyPagingItems<UiStatus>
    ) : UserFavouriteTimelineState

    object NoAccount : UserFavouriteTimelineState
}