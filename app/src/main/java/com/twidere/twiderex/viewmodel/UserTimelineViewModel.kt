package com.twidere.twiderex.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.repository.UserRepository

class UserTimelineViewModel @ViewModelInject constructor(
    private val repository: UserRepository,
) : ViewModel() {
    val loadingMore = MutableLiveData(false)
    val timeline = MutableLiveData<List<UiStatus>>(emptyList())

    suspend fun refresh(user: UiUser) {
        if (!timeline.value.isNullOrEmpty()) {
            return
        }
        val result = repository.loadBetween(
            user.id,
        )
        timeline.postValue(result)
    }

    suspend fun loadMore(user: UiUser) {
        val current = timeline.value ?: emptyList()
        val result = repository.loadBetween(
            user.id,
            max_id = current.lastOrNull()?.statusId
        )
        timeline.postValue(current + result)
    }
}