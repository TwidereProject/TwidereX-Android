package com.twidere.twiderex.viewmodel.twitter

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.twidere.services.microblog.HomeTimelineService
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.model.DbTimelineWithStatus
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.HomeTimelineRepository

class HomeTimelineViewModel @ViewModelInject constructor(
    accountRepository: AccountRepository,
    database: AppDatabase
) : ViewModel() {

    private val repository by lazy {
        accountRepository.getCurrentAccount().let { account ->
            accountRepository.getCurrentAccount().service.let {
                it as HomeTimelineService
            }.let { service ->
                HomeTimelineRepository(account.key, service, database)
            }
        }
    }

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