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

import com.twidere.twiderex.dataprovider.mapper.toPagingTimeline
import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.mock.db.MockCacheDatabase
import com.twidere.twiderex.mock.model.mockIStatus
import com.twidere.twiderex.mock.service.MockLookUpService
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.paging.saveToDb
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class StatusRepositoryTest {

    @Test
    fun statusFlowUpdatesWhenUpdateStatusSuccess() = runBlocking {
        val database = MockCacheDatabase()
        val accountKey = MicroBlogKey.twitter("test")
        val status = mockIStatus().toUi(accountKey)
        StatusRepository(
            database = database,
            preferences = null,
        ).let {
            database.statusDao().insertAll(listOf(status), accountKey)
            it.updateStatus(
                statusKey = status.statusKey,
                accountKey = accountKey,
                action = {
                    it.copy(rawText = "updated")
                }
            )
            assertEquals("updated", it.loadStatus(statusKey = status.statusKey, accountKey = accountKey).first()?.rawText)
        }
    }

    @Test
    fun removeBothStatusAndPagingTimelineAfterDeleteStatusSuccess() = runBlocking {
        val database = MockCacheDatabase()
        val accountKey = MicroBlogKey.twitter("test")
        val status = mockIStatus().also {
            listOf(it.toPagingTimeline(accountKey, pagingKey = "test"))
                .saveToDb(database)
        }.toUi(accountKey)
        StatusRepository(
            database = database,
            preferences = null,
        ).let {
            val statusFlow = it.loadStatus(statusKey = status.statusKey, accountKey = accountKey)
            assertEquals(status.statusKey, statusFlow.first()?.statusKey)
            assertEquals(status.statusKey, database.pagingTimelineDao().getLatest("test", accountKey)?.status?.statusKey)
            it.removeStatus(
                statusKey = status.statusKey,
            )
            assertNull(statusFlow.first())
            assertNull(database.pagingTimelineDao().getLatest("test", accountKey))
        }
    }

    @Test
    fun saveToDbAfterLoadSuccessfullyFromNetwork(): Unit = runBlocking {
        val database = MockCacheDatabase()
        val accountKey = MicroBlogKey.twitter("test")
        val repo = StatusRepository(
            database = database,
            preferences = null,
        )
        val mockStatus = mockIStatus().toUi(accountKey)

        repo.loadTweetFromNetwork(
            id = mockStatus.statusId,
            accountKey = accountKey,
            lookupService = MockLookUpService()
        )

        assertNotNull(repo.loadStatus(mockStatus.statusKey, accountKey = accountKey).first())
    }
}
