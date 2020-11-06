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

import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.twidere.services.microblog.StatusService
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.model.DbStatusV2
import com.twidere.twiderex.model.UserKey
import javax.inject.Singleton

@Singleton
class StatusRepository @AssistedInject constructor(
    private val database: AppDatabase,
    @Assisted private val key: UserKey,
    @Assisted private val service: StatusService,
) {

    @AssistedInject.Factory
    interface AssistedFactory {
        fun create(key: UserKey, service: StatusService): StatusRepository
    }

    suspend fun like(id: String) {
        updateStatus(id) {
//            it.liked = true
        }
        runCatching {
            service.like(id)
        }.onFailure {
            it.printStackTrace()
            updateStatus(id) {
//                it.liked = false
            }
        }
    }

    suspend fun unlike(id: String) {
        updateStatus(id) {
//            it.liked = false
        }
        runCatching {
            service.unlike(id)
        }.onFailure {
            it.printStackTrace()
            updateStatus(id) {
//                it.liked = true
            }
        }
    }

    suspend fun retweet(id: String) {
        updateStatus(id) {
//            it.retweeted = true
        }
        runCatching {
            service.retweet(id)
        }.onFailure {
            it.printStackTrace()
            updateStatus(id) {
//                it.retweeted = false
            }
        }
    }

    suspend fun unRetweet(id: String) {
        updateStatus(id) {
//            it.retweeted = false
        }
        runCatching {
            service.unRetweet(id)
        }.onFailure {
            it.printStackTrace()
            updateStatus(id) {
//                ReferencedTweetType.retweeted = true
            }
        }
    }

    private suspend fun updateStatus(id: String, action: (DbStatusV2) -> Unit) {
        database.statusDao().findWithStatusId(id)?.let {
            action.invoke(it)
            database.statusDao().insertAll(listOf(it))
        }
    }
}
