/*
 *  Twidere X
 *
 *  Copyright (C) TwidereProject and Contributors
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

import androidx.datastore.core.DataStore
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.twidere.services.mastodon.MastodonService
import com.twidere.services.microblog.LookupService
import com.twidere.services.microblog.MicroBlogService
import com.twidere.services.nitter.NitterService
import com.twidere.services.twitter.TwitterService
import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.http.TwidereServiceFactory
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.paging.mediator.paging.pager
import com.twidere.twiderex.paging.mediator.paging.toUi
import com.twidere.twiderex.paging.mediator.status.MastodonStatusContextMediator
import com.twidere.twiderex.paging.mediator.status.TwitterConversationMediator
import com.twidere.twiderex.preferences.model.MiscPreferences
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow

class StatusRepository(
    private val database: CacheDatabase,
    private val preferences: DataStore<MiscPreferences>?,
) {
    fun loadStatus(
        statusKey: MicroBlogKey,
        accountKey: MicroBlogKey
    ): Flow<UiStatus?> {
        return database.statusDao().findWithStatusKeyWithFlow(statusKey, accountKey)
    }

    suspend fun loadFromCache(statusKey: MicroBlogKey, accountKey: MicroBlogKey): UiStatus? {
        return database.statusDao().findWithStatusKey(statusKey, accountKey)
    }

    suspend fun updateStatus(
        statusKey: MicroBlogKey,
        accountKey: MicroBlogKey,
        action: (UiStatus) -> UiStatus
    ) {
        database.statusDao().findWithStatusKey(statusKey, accountKey = accountKey)?.let {
            database.statusDao().insertAll(listOf(action.invoke(it)), accountKey)
        }
    }

    suspend fun removeStatus(statusKey: MicroBlogKey) {
        database.withTransaction {
            database.statusDao().delete(statusKey)
            database.pagingTimelineDao().delete(statusKey)
        }
    }

    suspend fun loadTweetFromNetwork(
        id: String,
        accountKey: MicroBlogKey,
        lookupService: LookupService
    ) {
        database.statusDao().insertAll(
            listOf(
                lookupService.lookupStatus(id).toUi(accountKey = accountKey)
            ),
            accountKey = accountKey
        )
    }

    @OptIn(ExperimentalPagingApi::class, FlowPreview::class)
    fun conversation(
        statusKey: MicroBlogKey,
        platformType: PlatformType,
        service: MicroBlogService,
        accountKey: MicroBlogKey
    ): Flow<PagingData<UiStatus>> = flow {
        // TODO: remove usage of `when`
        val remoteMediator = when (platformType) {
            PlatformType.Twitter -> TwitterConversationMediator(
                service = service as TwitterService,
                nitterService = preferences?.data?.first()?.nitterInstance?.takeIf { it.isNotEmpty() }
                    ?.let {
                        NitterService(
                            it.trimEnd('/'),
                            TwidereServiceFactory.createHttpClientFactory()
                        )
                    },
                statusKey = statusKey,
                accountKey = accountKey,
                database = database,
            )
            PlatformType.StatusNet -> TODO()
            PlatformType.Fanfou -> TODO()
            PlatformType.Mastodon -> MastodonStatusContextMediator(
                service = service as MastodonService,
                statusKey = statusKey,
                accountKey = accountKey,
                database = database,
            )
        }
        emit(remoteMediator.pager().toUi())
    }.flattenMerge()
}
