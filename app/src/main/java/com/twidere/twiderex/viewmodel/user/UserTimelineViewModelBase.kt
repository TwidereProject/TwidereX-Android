package com.twidere.twiderex.viewmodel.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiUser
import java.util.ArrayList

abstract class UserTimelineViewModelBase : ViewModel() {
    val loadingMore = MutableLiveData(false)
    private val timelineIds = arrayListOf<String>()
    val timeline = liveData {
        emitSource(source.switchMap { result ->
            liveData {
                emit(
                    timelineIds.mapNotNull { id ->
                        result.firstOrNull { it.statusId == id }
                    }
                )
            }
        })
    }
    abstract val source: LiveData<List<UiStatus>>

    fun clear() {
        timelineIds.clear()
    }

    suspend fun refresh(user: UiUser) {
        if (!timelineIds.isNullOrEmpty()) {
            timelineIds.clear()
        }
        loadingMore.postValue(true)
        val result = loadBetween(user)
        timelineIds.addAll(result.map { it.statusId }.filter { !timelineIds.contains(it) })
        loadingMore.postValue(false)
    }

    suspend fun loadMore(user: UiUser) {
        loadingMore.postValue(true)
        val result = loadBetween(user, max_id = timelineIds.lastOrNull())
        timelineIds.addAll(result.map { it.statusId }.filter { !timelineIds.contains(it) })
        loadingMore.postValue(false)
    }

    protected abstract suspend fun loadBetween(
        user: UiUser,
        max_id: String? = null,
        since_Id: String? = null,
    ): List<UiStatus>
}