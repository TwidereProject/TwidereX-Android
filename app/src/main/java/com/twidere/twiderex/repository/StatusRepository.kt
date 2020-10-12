/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
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
