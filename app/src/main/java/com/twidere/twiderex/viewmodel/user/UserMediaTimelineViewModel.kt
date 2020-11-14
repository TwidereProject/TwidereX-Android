package com.twidere.twiderex.viewmodel.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.twidere.services.microblog.TimelineService
import com.twidere.twiderex.di.assisted.IAssistedFactory
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.repository.timeline.user.UserMediaTimelineRepository
import kotlinx.coroutines.flow.Flow

class UserMediaTimelineViewModel @AssistedInject constructor(
    private val factory: UserMediaTimelineRepository.AssistedFactory,
    @Assisted private val account: AccountDetails,
    @Assisted screenName: String,
) : ViewModel() {

    @AssistedInject.Factory
    interface AssistedFactory : IAssistedFactory {
        fun create(account: AccountDetails, screenName: String): UserMediaTimelineViewModel
    }

    private val repository by lazy {
        account.service.let {
            it as TimelineService
        }.let {
            factory.create(account.key, it)
        }
    }

    val source: Flow<PagingData<UiStatus>> by lazy {
        repository.getPager(screenName).cachedIn(viewModelScope)
    }
}
