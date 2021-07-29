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
import com.twidere.services.twitter.model.Attachment
import com.twidere.services.twitter.model.DirectMessageEvent
import com.twidere.services.twitter.model.Entities
import com.twidere.services.twitter.model.EntitiesURL
import com.twidere.services.twitter.model.MessageCreate
import com.twidere.services.twitter.model.MessageData
import com.twidere.services.twitter.model.MessageTarget
import com.twidere.services.twitter.model.PurpleMedia
import com.twidere.services.twitter.model.User
import com.twidere.twiderex.db.mapper.toDbDirectMessage
import com.twidere.twiderex.db.mapper.toDbUser
import com.twidere.twiderex.db.model.DbDMConversation
import com.twidere.twiderex.db.model.DbDMConversation.Companion.saveToDb
import com.twidere.twiderex.db.model.DbDMEventWithAttachments
import com.twidere.twiderex.db.model.DbDMEventWithAttachments.Companion.saveToDb
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID
import java.util.concurrent.Executors

@RunWith(AndroidJUnit4::class)
class DbDMEventTest {
    private lateinit var cacheDatabase: CacheDatabase
    private val user1AccountKey = MicroBlogKey.twitter("1")
    private val user2AccountKey = MicroBlogKey.twitter("2")
    private val conversationCount = 5

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        cacheDatabase = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), CacheDatabase::class.java)
            .setTransactionExecutor(Executors.newSingleThreadExecutor()).build()
        runBlocking {
            for (i in 0 until conversationCount) {
                generateDirectMessage(
                    accountKey = user1AccountKey,
                    System.currentTimeMillis().toString(),
                    user1AccountKey.id
                ).also {
                    it.saveToDb(cacheDatabase)
                }.toConversation()
                    .saveToDb(cacheDatabase)
                generateDirectMessage(
                    accountKey = user2AccountKey,
                    System.currentTimeMillis().toString(),
                    user2AccountKey.id
                ).also {
                    it.saveToDb(cacheDatabase)
                }.toConversation()
                    .saveToDb(cacheDatabase)
            }
        }
    }

    private fun List<DbDMEventWithAttachments>.toConversation() =
        groupBy {
            it.message.conversationKey
        }.map {
            it.value.maxByOrNull { msg -> msg.message.sortId }?.message!!
        }.map {
            DbDMConversation(
                _id = UUID.randomUUID().toString(),
                accountKey = it.accountKey,
                conversationId = it.conversationKey.id,
                conversationKey = it.conversationKey,
                conversationAvatar = "",
                conversationName = it.htmlText,
                conversationType = DbDMConversation.Type.ONE_TO_ONE,
                conversationSubName = "",
                recipientKey = it.conversationUserKey,
            )
        }

    private suspend fun generateDirectMessage(accountKey: MicroBlogKey, senderId: String, recipientId: String): List<DbDMEventWithAttachments> {
        val messageList = mutableListOf<DbDMEventWithAttachments>()
        val count = 5
        for (i in 0 until count) {
            messageList.add(
                DirectMessageEvent(
                    createdTimestamp = System.currentTimeMillis().toString(),
                    id = UUID.randomUUID().toString(),
                    type = "message_create",
                    messageCreate = MessageCreate(
                        messageData = MessageData(
                            text = "message:$count",
                            entities = Entities(
                                urls = listOf(
                                    EntitiesURL(
                                        display_url = "url$count",
                                        expanded_url = "expanded:$count",
                                        url = "url:$count",
                                        indices = listOf(0, 1)
                                    )
                                )
                            ),
                            attachment = Attachment(
                                type = "media",
                                media = PurpleMedia(
                                    id = count.toLong(),
                                    idStr = count.toString(),
                                )
                            )
                        ),
                        senderId = senderId,
                        target = MessageTarget(
                            recipientId
                        )
                    )
                ).toDbDirectMessage(
                    accountKey,
                    User(
                        id = senderId.toLong(),
                        idStr = senderId,

                    ).toDbUser()
                )
            )
            delay(1)
        }
        return messageList
    }

    @After
    fun tearDown() {
        cacheDatabase.close()
    }

    @Test
    fun insertMessages_GetAllMessagesByAccountKey() = runBlocking {
        val user1Messages = cacheDatabase.directMessageDao().getAll(accountKey = user1AccountKey)
        user1Messages.forEach {
            Assert.assertEquals(user1AccountKey, it.message.accountKey)
        }
        val user2Messages = cacheDatabase.directMessageDao().getAll(accountKey = user2AccountKey)
        user2Messages.forEach {
            Assert.assertEquals(user2AccountKey, it.message.accountKey)
        }
    }

    @Test
    fun getAllMessages_ContainsDbMediaAndUrl() = runBlocking {
        val user1Messages = cacheDatabase.directMessageDao().getAll(accountKey = user1AccountKey)
        user1Messages.forEach {
            assert(it.media.isNotEmpty())
            assert(it.urlEntity.isNotEmpty())
            it.media.forEach { media ->
                Assert.assertEquals(it.message.messageKey, media.belongToKey)
            }
            it.urlEntity.forEach { url ->
                Assert.assertEquals(it.message.messageKey, url.statusKey)
            }
        }
    }

    @Test
    fun insertAllConversations_GetConversationsByAccountKey() = runBlocking {
        val user1Conversation = cacheDatabase.directMessageConversationDao().find(accountKey = user1AccountKey)
        Assert.assertEquals(conversationCount, user1Conversation.size)
        user1Conversation.forEach {
            Assert.assertEquals(user1AccountKey, it.conversation.accountKey)
        }
        val user2Conversation = cacheDatabase.directMessageConversationDao().find(accountKey = user2AccountKey)
        user2Conversation.forEach {
            Assert.assertEquals(user2AccountKey, it.conversation.accountKey)
        }
    }

    @Test
    fun foundConversations_ContainsLatestMessages() = runBlocking {
        val conversations = cacheDatabase.directMessageConversationDao().find(accountKey = user1AccountKey)
        Assert.assertEquals(conversationCount, conversations.size)
        conversations.forEach {
            // check if latest message belong to this conversation
            Assert.assertEquals(it.conversation.conversationKey, it.latestMessage.message.conversationKey)
            // check if it is the latest message
            val latestMessage = cacheDatabase.directMessageDao().find(it.conversation.accountKey, it.conversation.conversationKey)
                .maxByOrNull { msg -> msg.message.sortId }
            Assert.assertEquals(it.latestMessage.message.messageId, latestMessage?.message?.messageId)
        }
    }

    @Test
    fun deleteConversation() = runBlocking {
        val result = cacheDatabase.directMessageConversationDao().find(accountKey = user1AccountKey)
        Assert.assertEquals(conversationCount, result.size)
        cacheDatabase.directMessageDao().clearConversation(user1AccountKey, result[0].conversation.conversationKey)
        cacheDatabase.directMessageConversationDao().delete(result[0].conversation)
        Assert.assertEquals(result.size - 1, cacheDatabase.directMessageConversationDao().find(user1AccountKey).size)
    }

    @Test
    fun deleteAndClearMessages() = runBlocking {
        val result = cacheDatabase.directMessageDao().getAll(user1AccountKey)
        cacheDatabase.directMessageDao().delete(result[0].message)
        Assert.assertEquals(result.size - 1, cacheDatabase.directMessageDao().getAll(user1AccountKey).size)

        cacheDatabase.directMessageDao().clearAll(user1AccountKey)
        assert(cacheDatabase.directMessageDao().find(user1AccountKey, result[1].message.conversationKey).isEmpty())
    }

    @Test
    fun clearConversation() = runBlocking {
        cacheDatabase.directMessageDao().clearAll(user1AccountKey)
        cacheDatabase.directMessageConversationDao().clearAll(user1AccountKey)
        assert(cacheDatabase.directMessageConversationDao().find(user1AccountKey).isEmpty())
    }

    @Test
    fun testMessagePagingSource() = runBlocking {
        val conversation = cacheDatabase.directMessageConversationDao().find(user1AccountKey)[0]
        val pagingSource = cacheDatabase.directMessageDao().getPagingSource(user1AccountKey, conversation.conversation.conversationKey)
        val resultFirst = pagingSource.load(PagingSource.LoadParams.Refresh(null, loadSize = 2, false))
        Assert.assertEquals(2, (resultFirst as PagingSource.LoadResult.Page).data.size)

        val resultLoadMore = pagingSource.load(PagingSource.LoadParams.Append(resultFirst.nextKey ?: 2, loadSize = 2, false))
        Assert.assertEquals(2, (resultLoadMore as PagingSource.LoadResult.Page).data.size)
    }

    @Test
    fun testConversationPagingSource() = runBlocking {
        val conversationPagingSource = cacheDatabase.directMessageConversationDao().getPagingSource(user1AccountKey)
        val resultFirst = conversationPagingSource.load(PagingSource.LoadParams.Refresh(null, loadSize = 2, false))
        Assert.assertEquals(2, (resultFirst as PagingSource.LoadResult.Page).data.size)

        val resultLoadMore = conversationPagingSource.load(PagingSource.LoadParams.Append(resultFirst.nextKey ?: 2, loadSize = 2, false))
        Assert.assertEquals(2, (resultLoadMore as PagingSource.LoadResult.Page).data.size)
    }

    @Test
    fun get_OrderBySortId() = runBlocking {
        val result = cacheDatabase.directMessageConversationDao().find(accountKey = user1AccountKey)
        result.forEachIndexed { index, con ->
            if (index < result.size - 2) assert(con.latestMessage.message.sortId > result[index + 1].latestMessage.message.sortId)
        }

        val message = cacheDatabase.directMessageDao().find(accountKey = user1AccountKey, conversationKey = result[0].conversation.conversationKey)
        message.forEachIndexed { index, msg ->
            if (index < result.size - 2) assert(msg.message.sortId > message[index + 1].message.sortId)
        }
    }
}
