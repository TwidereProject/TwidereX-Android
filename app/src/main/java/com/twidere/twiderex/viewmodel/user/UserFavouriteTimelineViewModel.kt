package com.twidere.twiderex.viewmodel.user

import androidx.hilt.lifecycle.ViewModelInject
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.repository.UserRepository

class UserFavouriteTimelineViewModel @ViewModelInject constructor(private val repository: UserRepository) :
    UserTimelineViewModelBase(repository) {
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