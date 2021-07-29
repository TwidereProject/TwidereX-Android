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
package com.twidere.twiderex.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.twidere.services.mastodon.model.MastodonList
import com.twidere.services.microblog.model.IListModel
import com.twidere.services.twitter.model.TwitterList
import com.twidere.services.twitter.model.User
import com.twidere.twiderex.db.dao.ListsDao
import com.twidere.twiderex.db.mapper.toDbList
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.Executors

@RunWith(AndroidJUnit4::class)
class DbListTest {
    private lateinit var listsDao: ListsDao
    private lateinit var cacheDatabase: CacheDatabase
    private val twitterAccountKey = MicroBlogKey.twitter("123")
    private val mastodonAccountKey = MicroBlogKey("456", "mastodon.com")
    private val originData = mutableListOf<IListModel>()
    private val twitterCount = 10
    private val mastodonCount = 10
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        cacheDatabase = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), CacheDatabase::class.java)
            .setTransactionExecutor(Executors.newSingleThreadExecutor()).build()
        listsDao = cacheDatabase.listsDao()
        for (i in 0 until twitterCount) {
            val ownerId = if (i % 2 == 0) twitterAccountKey.id else "789"
            originData.add(
                TwitterList(
                    id = i.toLong(),
                    name = "twitter name $i",
                    description = "description $i",
                    mode = "private",
                    idStr = i.toString(),
                    user = User(id = ownerId.toLong(), idStr = ownerId)
                )
            )
        }
        for (i in 0 until mastodonCount) {
            originData.add(
                MastodonList(
                    id = i.toString(),
                    title = "mastodon name $i",
                )
            )
        }
        runBlocking {
            val dbLists = originData.map {
                it.toDbList(if (it is TwitterList) twitterAccountKey else mastodonAccountKey)
            }
            Assert.assertEquals(originData.size, dbLists.size)
            listsDao.insertAll(dbLists)
        }
    }

    @After
    fun tearDown() {
        cacheDatabase.close()
    }

    @Test
    fun checkInsertDbLists() {
        runBlocking {
            val result = listsDao.findAll()
            Assert.assertEquals(originData.size, result?.size)
        }
    }

    @Test
    fun findDbListWithListKey() {
        runBlocking {
            val twitterList = listsDao.findWithListKey(MicroBlogKey.twitter("0"), twitterAccountKey)
            Assert.assertEquals("0", twitterList?.listId)
            twitterList?.let { assert(it.title.startsWith("twitter")) }

            val mastodonList = listsDao.findWithListKey(MicroBlogKey("0", mastodonAccountKey.host), mastodonAccountKey)
            Assert.assertEquals("0", mastodonList?.listId)
            mastodonList?.let { assert(it.title.startsWith("mastodon")) }
        }
    }

    @Test
    fun findDbListWithListKeyWithFlow_AutoUpdateAfterDbUpdate() {
        runBlocking {
            val source = listsDao.findWithListKeyWithFlow(MicroBlogKey.twitter("0"), twitterAccountKey)
            var data = source.firstOrNull()
            Assert.assertEquals("description 0", data?.description)
            data?.let {
                listsDao.update(listOf(it.copy(description = "Update 0")))
            }
            data = source.firstOrNull()
            Assert.assertEquals("Update 0", data?.description)
        }
    }

    @Test
    fun findDbListsWithAccountKey() {
        runBlocking {
            val twitterLists = listsDao.findWithAccountKey(twitterAccountKey)
            Assert.assertEquals(twitterCount, twitterLists?.size)
            twitterLists?.forEach {
                assert(it.title.startsWith("twitter"))
            }
            val mastodonList = listsDao.findWithAccountKey(mastodonAccountKey)
            Assert.assertEquals(mastodonCount, mastodonList?.size)
            mastodonList?.forEach {
                assert(it.title.startsWith("mastodon"))
            }
        }
    }

    @Test
    fun updateDbList() {
        runBlocking {
            var updateList = listsDao.findWithListKey(MicroBlogKey.twitter("0"), twitterAccountKey)
            Assert.assertNotNull(updateList)
            listsDao.update(listOf(updateList!!.copy(title = "updated title")))
            updateList = listsDao.findWithListKey(MicroBlogKey.twitter("0"), twitterAccountKey)
            Assert.assertNotNull(updateList)
            Assert.assertEquals("updated title", updateList?.title)
        }
    }

    @Test
    fun deleteDbList() {
        runBlocking {
            var deleteList = listsDao.findWithListKey(MicroBlogKey.twitter("0"), twitterAccountKey)
            Assert.assertNotNull(deleteList)
            listsDao.delete(listOf(deleteList!!))
            deleteList = listsDao.findWithListKey(MicroBlogKey.twitter("0"), twitterAccountKey)
            Assert.assertNull(deleteList)
        }
    }

    @Test
    fun testPagingSource() {
        runBlocking {
            val pagingSource = listsDao.getPagingSource(twitterAccountKey)
            val resultFirst = pagingSource.load(PagingSource.LoadParams.Refresh(null, loadSize = 2, false))
            Assert.assertEquals(2, (resultFirst as PagingSource.LoadResult.Page).data.size)
            Assert.assertEquals("0", resultFirst.data[0].listId)

            val resultLoadMore = pagingSource.load(PagingSource.LoadParams.Append(resultFirst.nextKey ?: 2, loadSize = 2, false))
            Assert.assertEquals(2, (resultLoadMore as PagingSource.LoadResult.Page).data.size)
            Assert.assertEquals("2", resultLoadMore.data[0].listId)
        }
    }

    @Test
    fun clearDbList() {
        runBlocking {
            listsDao.clearAll(twitterAccountKey)
            val restLists = listsDao.findAll()
            Assert.assertEquals(mastodonCount, restLists?.size)
        }
    }
}
