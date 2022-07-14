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
package com.twidere.twiderex.db

import com.twidere.twiderex.dataprovider.db.CacheDatabaseImpl
import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.db.base.CacheDatabaseDaoTest
import com.twidere.twiderex.mock.model.mockIStatus
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiUrlEntity
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class StatusDaoImplTest : CacheDatabaseDaoTest() {
    private val accountKey = MicroBlogKey.twitter("123")

    @Test
    fun insertAll_SaveBothStatusAndAttachmentsToDatabase() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        val status = mockIStatus(hasMedia = true, hasReference = true).toUi(accountKey).copy(
            url = listOf(
                UiUrlEntity(
                    url = "test",
                    expandedUrl = "",
                    displayUrl = "",
                    title = "",
                    description = "",
                    image = ""
                ),
            ),
            retweeted = true,
            liked = true
        )
        cacheDatabase.statusDao().insertAll(listOf = listOf(status), accountKey = accountKey)
        cacheDatabase.statusDao().findWithStatusKey(status.statusKey, accountKey = accountKey)?.let {
            assertEquals(status.statusKey, it.statusKey)
            assertEquals(status.referenceStatus.values.first().statusKey, it.referenceStatus.values.first().statusKey)
            assertEquals(status.media.first().url, it.media.first().url)
            assertEquals(status.url.first().url, it.url.first().url)
            assertEquals(status.user.userKey, it.user.userKey)
            assertEquals(status.retweeted, it.retweeted)
            assertEquals(status.liked, it.retweeted)
        } ?: assert(false)

        roomDatabase.statusReferenceDao().find(key = status.referenceStatus.values.first().statusKey, referenceType = status.referenceStatus.keys.first()).let {
            assert(it.isNotEmpty())
        }

        roomDatabase.mediaDao().findMediaByBelongToKey(status.statusKey).let {
            assert(it.isNotEmpty())
        }

        roomDatabase.userDao().findWithUserKey(status.user.userKey).let {
            assertEquals(status.user.userKey, it?.userKey)
        }

        roomDatabase.urlEntityDao().findWithBelongToKey(status.statusKey).let {
            assert(it.isNotEmpty())
        }

        roomDatabase.reactionDao().findWithStatusKey(statusKey = status.statusKey, accountKey = accountKey).let {
            assertEquals(status.retweeted, it?.retweeted)
            assertEquals(status.liked, it?.retweeted)
        }
    }

    @Test
    fun delete_DeleteBothStatusAndReferencesAndReactions() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        val status = mockIStatus(hasReference = true).toUi(accountKey)
        val statusFlow = cacheDatabase.statusDao().findWithStatusKeyWithFlow(statusKey = status.statusKey, accountKey = accountKey)
        cacheDatabase.statusDao().insertAll(listOf = listOf(status), accountKey = accountKey)
        assertNotNull(statusFlow.firstOrNull())
        cacheDatabase.statusDao().delete(statusKey = status.statusKey)
        assertNull(statusFlow.firstOrNull())
        assert(roomDatabase.statusReferenceDao().find(key = status.referenceStatus.values.first().statusKey, referenceType = status.referenceStatus.keys.first()).isEmpty())
        assertNull(roomDatabase.reactionDao().findWithStatusKey(statusKey = status.statusKey, accountKey = accountKey))
    }

    @Test
    fun findWithStatusKeyWithFlow_ReturnsStatusFlowAndUpdateAfterDbChanged() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        val status = mockIStatus().toUi(accountKey)
        val statusFlow = cacheDatabase.statusDao().findWithStatusKeyWithFlow(statusKey = status.statusKey, accountKey = accountKey)
        assertNull(statusFlow.firstOrNull())
        cacheDatabase.statusDao().insertAll(listOf = listOf(status), accountKey = accountKey)
        assertEquals(status.statusKey, statusFlow.firstOrNull()?.statusKey)

        cacheDatabase.statusDao().updateAction(
            statusKey = status.statusKey,
            accountKey = accountKey,
            liked = true,
            retweet = null
        )
        assertEquals(true, statusFlow.firstOrNull()?.liked)
    }

    @Test
    fun updateReaction_UpdateOnlyEffectOnSpecifiedReaction() = runBlocking {
        val cacheDatabase = CacheDatabaseImpl(roomDatabase)
        val status = mockIStatus().toUi(accountKey)
        val statusFlow = cacheDatabase.statusDao().findWithStatusKeyWithFlow(statusKey = status.statusKey, accountKey = accountKey)
        cacheDatabase.statusDao().insertAll(listOf = listOf(status), accountKey = accountKey)
        assertEquals(false, statusFlow.firstOrNull()?.liked)
        assertEquals(false, statusFlow.firstOrNull()?.retweeted)
        cacheDatabase.statusDao().updateAction(
            statusKey = status.statusKey,
            accountKey = accountKey,
            liked = true,
            retweet = false
        )
        assertEquals(true, statusFlow.firstOrNull()?.liked)
        assertEquals(false, statusFlow.firstOrNull()?.retweeted)

        cacheDatabase.statusDao().updateAction(
            statusKey = status.statusKey,
            accountKey = accountKey,
            liked = false,
            retweet = true
        )
        assertEquals(false, statusFlow.firstOrNull()?.liked)
        assertEquals(true, statusFlow.firstOrNull()?.retweeted)
    }
}
