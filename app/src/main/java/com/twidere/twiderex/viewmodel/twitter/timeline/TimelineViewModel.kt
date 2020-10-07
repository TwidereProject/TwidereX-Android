package com.twidere.twiderex.viewmodel.twitter.timeline

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.twidere.twiderex.repository.timeline.TimelineRepository

abstract class TimelineViewModel : ViewModel() {

    abstract val repository: TimelineRepository

    val source by lazy {
        repository.liveData
    }

    val loadingBetween = MutableLiveData(listOf<String>())
    val loadingMore = MutableLiveData(false)

    suspend fun refresh() {
        repository.refresh(source.value?.firstOrNull()?.statusId)
    }

    suspend fun loadBetween(
        max_id: String,
        since_id: String,
    ) {
        loadingBetween.postValue((loadingBetween.value ?: listOf()) + max_id)
        repository.loadBetween(max_id = max_id, since_id = since_id)
        loadingBetween.postValue((loadingBetween.value ?: listOf()) - max_id)
    }

    suspend fun loadMore() {
        loadingMore.postValue(true)
        source.value?.lastOrNull()?.statusId?.let {
            repository.loadMore(it)
        }
        loadingMore.postValue(false)
    }
}

