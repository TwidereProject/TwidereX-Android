package com.twidere.twiderex.providers

import androidx.compose.runtime.ambientOf
import com.twidere.services.microblog.StatusService
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.repository.StatusRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

val AmbientStatusActions = ambientOf<StatusActions>()

class StatusActions @Inject constructor(
    private val factory: StatusRepository.AssistedFactory,
) {
    private val repositoryFactory = { account: AccountDetails ->
        account.service.let {
            it as StatusService
        }.let {
            factory.create(account.key, it)
        }
    }

    fun like(status: UiStatus, account: AccountDetails) = GlobalScope.launch {
        val repository = repositoryFactory.invoke(account)
        if (status.liked) {
            repository.unlike(status.statusId)
        } else {
            repository.like(status.statusId)
        }
    }

    fun retweet(status: UiStatus, account: AccountDetails) = GlobalScope.launch {
        val repository = repositoryFactory.invoke(account)
        if (status.retweeted) {
            repository.unRetweet(status.statusId)
        } else {
            repository.retweet(status.statusId)
        }
    }
}