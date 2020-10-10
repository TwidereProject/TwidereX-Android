package com.twidere.twiderex.repository

import com.twidere.services.microblog.StatusService
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.model.DbStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatusRepository @Inject constructor(
    private val repository: AccountRepository,
    private val database: AppDatabase,
) {

    private fun getStatusService() = repository.getCurrentAccount().service.let {
        it as? StatusService
    }

    suspend fun like(id: String) {
        updateStatus(id) {
            it.liked = true
        }
        runCatching {
            getStatusService()?.like(id)
        }.onFailure {
            it.printStackTrace()
            updateStatus(id) {
                it.liked = false
            }
        }
    }

    suspend fun unlike(id: String) {
        updateStatus(id) {
            it.liked = false
        }
        runCatching {
            getStatusService()?.unlike(id)
        }.onFailure {
            it.printStackTrace()
            updateStatus(id) {
                it.liked = true
            }
        }
    }

    suspend fun retweet(id: String) {
        updateStatus(id) {
            it.retweeted = true
        }
        runCatching {
            getStatusService()?.retweet(id)
        }.onFailure {
            it.printStackTrace()
            updateStatus(id) {
                it.retweeted = false
            }
        }
    }

    suspend fun unRetweet(id: String) {
        updateStatus(id) {
            it.retweeted = false
        }
        runCatching {
            getStatusService()?.unRetweet(id)
        }.onFailure {
            it.printStackTrace()
            updateStatus(id) {
                it.retweeted = true
            }
        }
    }

    private suspend fun updateStatus(id: String, action: (DbStatus) -> Unit) {
        database.statusDao().findWithStatusId(id)?.let {
            action.invoke(it)
            database.statusDao().update(it)
        }
    }
}