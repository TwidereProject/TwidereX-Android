package com.twidere.twiderex.viewmodel.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.repository.UserRepository

abstract class UserTimelineViewModelBase(
    private val repository: UserRepository,
) : ViewModel() {
    val loadingMore = MutableLiveData(false)
    val timeline = liveData {
        emitSource(
            repository.getUserTimelineLiveData().switchMap { result ->
                liveData {
                    emit(
                        timelineIds.mapNotNull { id ->
                            result.firstOrNull { it.statusId == id }
                        }
                    )
                }
            }
        )
    }
    private val timelineIds = arrayListOf<String>()

    suspend fun refresh(user: UiUser) {
        if (!timelineIds.isNullOrEmpty()) {
            timelineIds.clear()
        }
        loadingMore.postValue(true)
        val result = loadBetween(user)
        timelineIds.addAll(result.map { it.statusId })
        loadingMore.postValue(false)
    }

    suspend fun loadMore(user: UiUser) {
        loadingMore.postValue(true)
        val result = loadBetween(user, max_id = timelineIds.lastOrNull())
        timelineIds.addAll(result.map { it.statusId })
        loadingMore.postValue(false)
    }

    protected abstract suspend fun loadBetween(
        user: UiUser,
        max_id: String? = null,
        since_Id: String? = null,
    ): List<UiStatus>
}