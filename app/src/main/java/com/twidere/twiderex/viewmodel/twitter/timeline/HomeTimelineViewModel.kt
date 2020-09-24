package com.twidere.twiderex.viewmodel.twitter.timeline

import androidx.hilt.lifecycle.ViewModelInject
import com.twidere.services.microblog.HomeTimelineService
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.timeline.HomeTimelineRepository
import com.twidere.twiderex.repository.timeline.TimelineRepository

class HomeTimelineViewModel @ViewModelInject constructor(
    accountRepository: AccountRepository,
    database: AppDatabase,
) : TimelineViewModel(database) {
    override val repository: TimelineRepository =
        accountRepository.getCurrentAccount().let { account ->
            accountRepository.getCurrentAccount().service.let {
                it as HomeTimelineService
            }.let { service ->
                HomeTimelineRepository(account.key, service, database)
            }
        }
}

