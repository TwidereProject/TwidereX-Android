package com.twidere.twiderex.viewmodel.twitter.timeline

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.model.DbTimelineWithStatus
import com.twidere.twiderex.repository.timeline.TimelineRepository

abstract class TimelineViewModel constructor(
    database: AppDatabase,
) : ViewModel() {

    abstract val repository: TimelineRepository

    val source by lazy {
        repository.liveData
    }

    val loadingBetween = MutableLiveData(listOf<String>())
    val loadingMore = MutableLiveData(false)

    suspend fun refresh() {
        repository.refresh(source.value?.firstOrNull()?.status?.status?.statusId)
    }

    suspend fun loadBetween(
        max_id: String,
        since_id: String,
        item: DbTimelineWithStatus,
    ) {
        loadingBetween.postValue((loadingBetween.value ?: listOf()) + max_id)
        repository.loadBetween(max_id = max_id, since_id = since_id)
        item.timeline.isGap = false
        repository.update(item.timeline)
        loadingBetween.postValue((loadingBetween.value ?: listOf()) - max_id)
    }

    suspend fun loadMore() {
        loadingMore.postValue(true)
        source.value?.lastOrNull()?.status?.status?.statusId?.let {
            repository.loadMore(it)
        }
        loadingMore.postValue(false)
    }
}

