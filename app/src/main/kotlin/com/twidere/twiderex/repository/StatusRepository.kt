/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
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

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.room.withTransaction
import com.twidere.services.mastodon.MastodonService
import com.twidere.services.microblog.LookupService
import com.twidere.services.nitter.NitterService
import com.twidere.services.twitter.TwitterService
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.mapper.toDbStatusWithReference
import com.twidere.twiderex.db.model.DbStatusV2
import com.twidere.twiderex.db.model.saveToDb
import com.twidere.twiderex.extensions.toUi
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiStatus.Companion.toUi
import com.twidere.twiderex.paging.mediator.paging.pager
import com.twidere.twiderex.paging.mediator.status.MastodonStatusContextMediator
import com.twidere.twiderex.paging.mediator.status.TwitterConversationMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StatusRepository(
    private val database: CacheDatabase,
    private val nitterService: NitterService?,
) {
    fun loadStatus(
        statusKey: MicroBlogKey,
        accountKey: MicroBlogKey
    ): Flow<UiStatus?> {
        return database.statusDao().findWithStatusKeyWithReferenceFlow(statusKey).map {
            it?.toUi(accountKey)
        }
    }

    suspend fun loadFromCache(statusKey: MicroBlogKey, accountKey: MicroBlogKey): UiStatus? {
        return database.statusDao().findWithStatusKeyWithReference(statusKey).let {
            it?.toUi(accountKey)
        }
    }

    suspend fun updateStatus(statusKey: MicroBlogKey, action: (DbStatusV2) -> Unit) {
        database.statusDao().findWithStatusKey(statusKey)?.let {
            action.invoke(it)
            database.statusDao().insertAll(listOf(it))
        }
    }

    suspend fun removeStatus(statusKey: MicroBlogKey) {
        database.withTransaction {
            database.statusDao().delete(statusKey)
            database.pagingTimelineDao().delete(statusKey)
            database.statusReferenceDao().delete(statusKey)
        }
    }

    suspend fun loadTweetFromNetwork(
        id: String,
        accountKey: MicroBlogKey,
        lookupService: LookupService
    ) {
        listOf(
            lookupService.lookupStatus(id).toDbStatusWithReference(accountKey = accountKey)
        ).saveToDb(database)
    }

    @OptIn(ExperimentalPagingApi::class)
    fun conversation(
        statusKey: MicroBlogKey,
        account: AccountDetails,
    ): Flow<PagingData<UiStatus>> {
        // TODO: remove usage of `when`
        val remoteMediator = when (account.type) {
            PlatformType.Twitter -> TwitterConversationMediator(
                service = account.service as TwitterService,
                nitterService = nitterService,
                statusKey = statusKey,
                accountKey = account.accountKey,
                database = database,
            )
            PlatformType.StatusNet -> TODO()
            PlatformType.Fanfou -> TODO()
            PlatformType.Mastodon -> MastodonStatusContextMediator(
                service = account.service as MastodonService,
                statusKey = statusKey,
                accountKey = account.accountKey,
                database = database,
            )
        }
        return remoteMediator.pager().toUi(accountKey = account.accountKey)
    }
}
