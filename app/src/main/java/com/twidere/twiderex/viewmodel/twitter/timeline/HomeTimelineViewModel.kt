package com.twidere.twiderex.viewmodel.twitter.timeline

import android.content.SharedPreferences
import androidx.hilt.lifecycle.ViewModelInject
import com.twidere.services.microblog.TimelineService
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.timeline.HomeTimelineRepository
import com.twidere.twiderex.repository.timeline.TimelineRepository

class HomeTimelineViewModel @ViewModelInject constructor(
    accountRepository: AccountRepository,
    database: AppDatabase,
    preferences: SharedPreferences,
) : TimelineViewModel(preferences) {
    override val repository: TimelineRepository =
        accountRepository.getCurrentAccount().let { account ->
            account.service.let {
                it as TimelineService
            }.let { service ->
                HomeTimelineRepository(account.key, service, database)
            }
        }
    override val savedStateKey: String = "${accountRepository.getCurrentAccount().key}_home"
}

