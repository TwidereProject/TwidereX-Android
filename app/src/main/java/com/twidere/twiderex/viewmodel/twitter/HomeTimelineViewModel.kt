package com.twidere.twiderex.viewmodel.twitter

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.twidere.services.microblog.HomeTimelineService
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.model.DbTimelineWithStatus
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.HomeTimelineRepository

class HomeTimelineViewModel @ViewModelInject constructor(
    accountRepository: AccountRepository,
    database: AppDatabase
) : ViewModel() {

    val repository by lazy {
        accountRepository.getCurrentAccount().let { account ->
            accountRepository.getCurrentAccount().service.let {
                it as? HomeTimelineService
            }?.let { service ->
                HomeTimelineRepository(account.key, service, database)
            }
        }
    }

    val source by lazy {
        repository?.liveData ?: liveData {
            emit(listOf<DbTimelineWithStatus>())
        }
    }

    suspend fun refresh() {
        repository?.refresh(source.value?.firstOrNull()?.status?.statusId)
    }

    suspend fun loadBetween(
        max_id: String,
        since_id: String,
    ) {
        repository?.loadBetween(max_id = max_id, since_id = since_id)
    }

    suspend fun loadMore() {
        source.value?.lastOrNull()?.status?.statusId?.let {
            repository?.loadMore(it)
        }
    }
}