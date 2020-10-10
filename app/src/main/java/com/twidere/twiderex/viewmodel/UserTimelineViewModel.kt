package com.twidere.twiderex.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.repository.UserRepository

class UserTimelineViewModel @ViewModelInject constructor(
    private val repository: UserRepository,
) : ViewModel() {
    val loadingMore = MutableLiveData(false)
    val timeline = liveData {
        emitSource(repository.getUserTimelineLiveData().switchMap {
            liveData {
                emit(it.filter {
                    timelineIds.contains(it.statusId)
                })
            }
        })
    }
    private val timelineIds = arrayListOf<String>()

    suspend fun refresh(user: UiUser) {
        if (!timelineIds.isNullOrEmpty()) {
            return
        }
        loadingMore.postValue(true)
        repository.loadTimelineBetween(
            user.id,
        ).map { it.statusId }.let {
            timelineIds.addAll(it)
        }
        loadingMore.postValue(false)
    }

    suspend fun loadMore(user: UiUser) {
        loadingMore.postValue(true)
        val result = repository.loadTimelineBetween(
            user.id,
            max_id = timelineIds.lastOrNull()
        )
        timelineIds.addAll(result.map { it.statusId })
        loadingMore.postValue(false)
    }
}