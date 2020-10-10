package com.twidere.twiderex.viewmodel.twitter.timeline

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.twidere.twiderex.repository.timeline.TimelineRepository

abstract class TimelineViewModel(
    private val preferences: SharedPreferences,
) : ViewModel() {
    abstract val repository: TimelineRepository
    abstract val savedStateKey: String
    private var hasMore = true

    val source by lazy {
        repository.liveData
    }

    val loadingBetween = MutableLiveData(listOf<String>())
    val loadingMore = MutableLiveData(false)
    val refreshing = MutableLiveData(false)

    suspend fun refresh() {
        refreshing.postValue(true)
        repository.refresh(source.value?.firstOrNull()?.statusId)
        refreshing.postValue(false)
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
        if (!hasMore) {
            return
        }
        loadingMore.postValue(true)
        source.value?.lastOrNull()?.statusId?.let {
            hasMore = repository.loadMore(it).any()
        }
        loadingMore.postValue(false)
    }

    fun restoreScrollState(): Int {
        return preferences.getInt("${savedStateKey}_offset", 0)
    }

    fun saveScrollState(offset: Int) {
        preferences.edit {
            putInt("${savedStateKey}_offset", offset)
        }
    }
}

