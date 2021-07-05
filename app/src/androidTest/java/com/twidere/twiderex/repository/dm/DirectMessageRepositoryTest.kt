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
package com.twidere.twiderex.repository.dm

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.mapper.toDbUser
import com.twidere.twiderex.mock.MockDirectMessageService
import com.twidere.twiderex.mock.MockLookUpService
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.model.ui.UiUser.Companion.toUi
import com.twidere.twiderex.repository.DirectMessageRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.Executors

/**
 * instead of testing pagination, we should focus on our code logic
 */
@RunWith(AndroidJUnit4::class)
class DirectMessageRepositoryTest {
    private lateinit var mockDataBase: CacheDatabase

    private var mockService = MockDirectMessageService()
    private var mockLookUpService = MockLookUpService()
    private val accountKey = MicroBlogKey.twitter("123")

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        mockDataBase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            CacheDatabase::class.java
        )
            .setTransactionExecutor(Executors.newSingleThreadExecutor()).build()
    }

    @After
    fun tearDown() {
        mockDataBase.clearAllTables()
    }

    @Test
    fun fetchList_FetchEventAndStoreToDB() = runBlocking {
        val repo = DirectMessageRepository(mockDataBase)
        mockService.errorMsg = null
        assert(mockDataBase.directMessageConversationDao().find(accountKey).isEmpty())

        mockService.add(
            mockService.generateDirectMessage(
                20,
                senderId = "345",
                recipientId = accountKey.id
            )
        )
        val result = repo.fetchEventAndSaveToDataBase(
            key = null,
            accountKey = accountKey,
            service = mockService,
            lookupService = mockLookUpService
        )
        assert(result.isNotEmpty())
        val conversationList = mockDataBase.directMessageConversationDao().find(accountKey)
        assert(conversationList.isNotEmpty())
        assert(mockDataBase.directMessageDao().find(accountKey, conversationList.first().conversation.conversationKey).isNotEmpty())
        // check lookup for user
        assert(
            conversationList.first().latestMessage.sender.userId == "345"
        )
    }

    @Test
    fun createConversation_StoreNewConversationToDbIFNotExists() = runBlocking {
        val repo = DirectMessageRepository(mockDataBase)
        assert(mockDataBase.directMessageConversationDao().find(accountKey).isEmpty())
        val user = mockLookUpService.lookupUser("123").toDbUser(accountKey).toUi()
        val conversationKey = repo.createNewConversation(user, accountKey, PlatformType.Twitter)
        assert(mockDataBase.directMessageConversationDao().findWithConversationKey(accountKey, conversationKey) != null)

        val user2 = mockLookUpService.lookupUser("234").toDbUser(accountKey).toUi()
        val conversationKey2 = repo.createNewConversation(user2, accountKey, PlatformType.Twitter)
        assert(conversationKey2 != conversationKey)
    }

    @Test
    fun createConversation_ReturnsExistsConversationKeyIfConversationExists() = runBlocking {
        val repo = DirectMessageRepository(mockDataBase)
        val user = mockLookUpService.lookupUser("123").toDbUser(accountKey).toUi()
        val existsKey = repo.createNewConversation(user, accountKey, PlatformType.Twitter)

        val newKey = repo.createNewConversation(user, accountKey, PlatformType.Twitter)
        assert(newKey == existsKey)
    }

    @Test
    fun checkNewMessage_ReturnsConversationsThatContainsNewMessages() = runBlocking {
        val repo = DirectMessageRepository(mockDataBase)
        for (i in 0 until 10) {
            mockService.add(
                mockService.generateDirectMessage(
                    1,
                    senderId = i.toString(),
                    recipientId = accountKey.id
                )
            )
        }

        val firstNewMessages = repo.checkNewMessages(accountKey, mockService, mockLookUpService)
        Log.d("DMTest", "firstSize:${firstNewMessages.size}")
        assert(firstNewMessages.size == 10)

        mockService.add(mockService.generateDirectMessage(1, senderId = 5.toString(), recipientId = accountKey.id))
        val secondNewMessages = repo.checkNewMessages(accountKey, mockService, mockLookUpService)
        Log.d("DMTest", "second:${secondNewMessages.size}")
        assert(secondNewMessages.size == 1)
        Assert.assertEquals("5", secondNewMessages.first().latestMessage.sender.id)

        mockService.add(mockService.generateDirectMessage(1, senderId = accountKey.id, recipientId = 2.toString()))
        val thirdNewMessage = repo.checkNewMessages(accountKey, mockService, mockLookUpService)
        Log.d("DMTest", "third:${thirdNewMessage.size}")
        assert(thirdNewMessage.isEmpty())
    }

    @Test
    fun deleteMessage_DeleteFromBothDbAndApi() = runBlocking {
        val repo = DirectMessageRepository(mockDataBase)
        for (i in 0 until 10) {
            mockService.add(
                mockService.generateDirectMessage(
                    2,
                    senderId = i.toString(),
                    recipientId = accountKey.id
                )
            )
        }
        repo.fetchEventAndSaveToDataBase(null, accountKey, mockService, mockLookUpService)
        var conversation = mockDataBase.directMessageConversationDao().find(accountKey).first()
        assert(mockDataBase.directMessageDao().find(accountKey, conversation.conversation.conversationKey).size == 2)
        repo.deleteMessage(
            accountKey = accountKey,
            conversationKey = conversation.conversation.conversationKey,
            messageId = conversation.latestMessage.message.messageId,
            messageKey = conversation.latestMessage.message.messageKey,
            mockService
        )
        // after fetch from api, still can't find the deleted message
        repo.fetchEventAndSaveToDataBase(null, accountKey, mockService, mockLookUpService)
        assert(mockDataBase.directMessageDao().find(accountKey, conversation.conversation.conversationKey).size == 1)
        // as long as conversation still contains messages , it won't be delete
        assert(mockDataBase.directMessageConversationDao().findWithConversationKey(accountKey, conversation.conversation.conversationKey) != null)

        conversation = mockDataBase.directMessageConversationDao().find(accountKey).first()
        repo.deleteMessage(
            accountKey = accountKey,
            conversationKey = conversation.conversation.conversationKey,
            messageId = conversation.latestMessage.message.messageId,
            messageKey = conversation.latestMessage.message.messageKey,
            mockService
        )

        repo.fetchEventAndSaveToDataBase(null, accountKey, mockService, mockLookUpService)
        assert(
            mockDataBase.directMessageDao()
                .find(accountKey, conversation.conversation.conversationKey).isEmpty()
        )
        // when conversation contains zero message, it will be delete too
        assert(mockDataBase.directMessageConversationDao().findWithConversationKey(accountKey, conversation.conversation.conversationKey) == null)
    }
}
