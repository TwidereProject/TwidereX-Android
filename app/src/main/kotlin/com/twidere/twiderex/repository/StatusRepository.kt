/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of Twidere X.
 * 
 *  Twidere X is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Twidere X is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Twidere X. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.repository

import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.twidere.services.microblog.StatusService
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.model.DbStatusReaction
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiStatus
import java.util.UUID

class StatusRepository @AssistedInject constructor(
    private val database: AppDatabase,
    @Assisted private val key: MicroBlogKey,
    @Assisted private val service: StatusService,
) {

    @AssistedInject.Factory
    interface AssistedFactory {
        fun create(key: MicroBlogKey, service: StatusService): StatusRepository
    }

    suspend fun like(status: UiStatus) {
        updateStatus(status.statusKey) {
            it.liked = true
        }
        runCatching {
            service.like(status.statusId)
        }.onFailure {
            it.printStackTrace()
            updateStatus(status.statusKey) {
                it.liked = false
            }
        }
    }

    suspend fun unlike(status: UiStatus) {
        updateStatus(status.statusKey) {
            it.liked = false
        }
        runCatching {
            service.unlike(status.statusId)
        }.onFailure {
            it.printStackTrace()
            updateStatus(status.statusKey) {
                it.liked = true
            }
        }
    }

    suspend fun retweet(status: UiStatus) {
        updateStatus(status.statusKey) {
            it.retweeted = true
        }
        runCatching {
            service.retweet(status.statusId)
        }.onFailure {
            it.printStackTrace()
            updateStatus(status.statusKey) {
                it.retweeted = false
            }
        }
    }

    suspend fun unRetweet(status: UiStatus) {
        updateStatus(status.statusKey) {
            it.retweeted = false
        }
        runCatching {
            service.unRetweet(status.statusId)
        }.onFailure {
            it.printStackTrace()
            updateStatus(status.statusKey) {
                it.retweeted = true
            }
        }
    }

    private suspend fun updateStatus(statusKey: MicroBlogKey, action: (DbStatusReaction) -> Unit) {
        database.reactionDao().findWithStatusKey(statusKey, key).let {
            it ?: DbStatusReaction(
                _id = UUID.randomUUID().toString(),
                statusKey = statusKey,
                accountKey = key,
                liked = false,
                retweeted = false,
            )
        }.let {
            action.invoke(it)
            database.reactionDao().insertAll(listOf(it))
        }
    }
}
