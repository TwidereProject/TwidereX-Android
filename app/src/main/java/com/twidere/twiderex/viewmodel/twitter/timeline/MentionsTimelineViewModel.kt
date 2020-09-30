package com.twidere.twiderex.viewmodel.twitter.timeline

import androidx.hilt.lifecycle.ViewModelInject
import com.twidere.services.microblog.MentionsTimelineService
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.timeline.MentionsTimelineRepository
import com.twidere.twiderex.repository.timeline.TimelineRepository

class MentionsTimelineViewModel @ViewModelInject constructor(
    accountRepository: AccountRepository,
    database: AppDatabase,
) : TimelineViewModel() {
    override val repository: TimelineRepository =
        accountRepository.getCurrentAccount().let { account ->
            accountRepository.getCurrentAccount().service.let {
                it as MentionsTimelineService
            }.let { service ->
                MentionsTimelineRepository(account.key, service, database)
            }
        }
}

