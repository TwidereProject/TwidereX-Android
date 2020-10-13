package com.twidere.twiderex.viewmodel.user

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.repository.UserRepository
import java.util.ArrayList

class UserFavouriteTimelineViewModel @ViewModelInject constructor(private val repository: UserRepository) :
    UserTimelineViewModelBase() {
    override val source: LiveData<List<UiStatus>>
        get() = repository.getUserTimelineLiveData(timelineType = TimelineType.UserFavourite)

    override suspend fun loadBetween(
        user: UiUser,
        max_id: String?,
        since_Id: String?
    ) = repository.loadFavouriteTimelineBetween(
        user.id,
        max_id = max_id,
        since_id = since_Id,
    )
}