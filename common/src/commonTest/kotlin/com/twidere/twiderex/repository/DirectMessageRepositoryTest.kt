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

import com.twidere.services.microblog.model.IDirectMessage
import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.mock.db.MockCacheDatabase
import com.twidere.twiderex.mock.db.dao.MockUserDao
import com.twidere.twiderex.mock.model.mockIDirectMessage
import com.twidere.twiderex.mock.paging.collectDataForTest
import com.twidere.twiderex.mock.service.MockDirectMessageService
import com.twidere.twiderex.mock.service.MockLookUpService
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.PlatformType
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class DirectMessageRepositoryTest {
    private val mockDataBase = MockCacheDatabase()
    private val accountKey = MicroBlogKey.twitter("123")
    private var mockService = MockDirectMessageService(accountKey)
    private var mockLookUpService = MockLookUpService()

    @Test
    fun fetchList_FetchEventAndStoreToDB() = runBlocking {
        val repo = DirectMessageRepository(mockDataBase)
        mockService.errorMsg = null
        assert(mockDataBase.directMessageConversationDao().find(accountKey).isEmpty())
        assert((mockDataBase.userDao() as MockUserDao).datas.isEmpty())

        val result = repo.fetchEventAndSaveToDataBase(
            key = null,
            accountKey = accountKey,
            service = mockService,
            lookupService = mockLookUpService
        )
        assert(result.isNotEmpty())
        val conversationList = mockDataBase.directMessageConversationDao().find(accountKey)
        assert(conversationList.isNotEmpty())
        assert(mockDataBase.directMessageDao().getPagingSource(accountKey, conversationList.first().conversation.conversationKey).collectDataForTest().isNotEmpty())
        assert((mockDataBase.userDao() as MockUserDao).datas.isNotEmpty())
    }

    @Test
    fun createConversation_StoreNewConversationToDbIFNotExists() = runBlocking {
        val repo = DirectMessageRepository(mockDataBase)
        assert(mockDataBase.directMessageConversationDao().find(accountKey).isEmpty())
        val user = mockLookUpService.lookupUser("123").toUi(accountKey)
        val conversationKey = repo.createNewConversation(user, accountKey, PlatformType.Twitter)
        assert(mockDataBase.directMessageConversationDao().findWithConversationKey(accountKey, conversationKey) != null)

        val user2 = mockLookUpService.lookupUser("234").toUi(accountKey)
        val conversationKey2 = repo.createNewConversation(user2, accountKey, PlatformType.Twitter)
        assert(conversationKey2 != conversationKey)
    }

    @Test
    fun createConversation_ReturnsExistsConversationKeyIfConversationExists() = runBlocking {
        val repo = DirectMessageRepository(mockDataBase)
        val user = mockLookUpService.lookupUser("123").toUi(accountKey)
        val existsKey = repo.createNewConversation(user, accountKey, PlatformType.Twitter)

        val newKey = repo.createNewConversation(user, accountKey, PlatformType.Twitter)
        assert(newKey == existsKey)
    }

    @Test
    fun checkNewMessage_ReturnsConversationsThatContainsNewMessages() = runBlocking {
        val repo = DirectMessageRepository(mockDataBase)
        val originMessage = mutableListOf<IDirectMessage>()
        for (i in 0 until 10) {
            originMessage.add(mockIDirectMessage(accountId = accountKey.id, otherUserID = i.toString()))
            mockService.messages = originMessage
        }

        // new message
        val firstNewMessages = repo.checkNewMessages(accountKey, mockService, mockLookUpService)
        assert(firstNewMessages.size == 10)

        // same message with database, no new messages
        val secondNewMessage = repo.checkNewMessages(accountKey, mockService, mockLookUpService)
        assert(secondNewMessage.isEmpty())

        // new message
        mockService.messages = listOf(mockIDirectMessage(accountId = accountKey.id, otherUserID = 5.toString()))
        val thirdNewMessages = repo.checkNewMessages(accountKey, mockService, mockLookUpService)
        assertEquals(1, thirdNewMessages.size)
        assertEquals("5", thirdNewMessages.first().latestMessage.sender.id)
    }

    @Test
    fun deleteMessage_DeleteFromBothDbAndApi() = runBlocking {
        val repo = DirectMessageRepository(mockDataBase)
        mockService.messages = listOf(
            mockIDirectMessage(accountId = accountKey.id, otherUserID = "test"),
            mockIDirectMessage(accountId = accountKey.id, otherUserID = "test")
        )
        repo.fetchEventAndSaveToDataBase(null, accountKey, mockService, mockLookUpService)
        var conversation = mockDataBase.directMessageConversationDao().find(accountKey).first()
        repo.deleteMessage(
            accountKey = accountKey,
            conversationKey = conversation.conversation.conversationKey,
            messageId = conversation.latestMessage.messageId,
            messageKey = conversation.latestMessage.messageKey,
            mockService
        )
        // also delete from api
        assert(mockService.isDeleted(conversation.latestMessage.messageId))

        assertEquals(1, mockDataBase.directMessageDao().getMessageCount(accountKey, conversation.conversation.conversationKey))
        // as long as conversation still contains messages , it won't be delete
        assertNotNull(mockDataBase.directMessageConversationDao().findWithConversationKey(accountKey, conversation.conversation.conversationKey))

        conversation = mockDataBase.directMessageConversationDao().find(accountKey).first()
        repo.deleteMessage(
            accountKey = accountKey,
            conversationKey = conversation.conversation.conversationKey,
            messageId = conversation.latestMessage.messageId,
            messageKey = conversation.latestMessage.messageKey,
            mockService
        )
        assert(mockService.isDeleted(conversation.latestMessage.messageId))

        assertEquals(0, mockDataBase.directMessageDao().getMessageCount(accountKey, conversation.conversation.conversationKey))
        // when conversation contains zero message, it will be delete too
        assertNull(mockDataBase.directMessageConversationDao().findWithConversationKey(accountKey, conversation.conversation.conversationKey))
    }
}
