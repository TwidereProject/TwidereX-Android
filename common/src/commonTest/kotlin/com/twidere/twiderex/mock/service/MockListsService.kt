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
package com.twidere.twiderex.mock.service

import com.twidere.services.microblog.ListsService
import com.twidere.services.microblog.MicroBlogService
import com.twidere.services.microblog.model.IListModel
import com.twidere.services.microblog.model.IUser
import com.twidere.services.twitter.model.TwitterList
import com.twidere.services.twitter.model.exceptions.TwitterApiException
import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.mock.model.mockIListModel
import com.twidere.twiderex.mock.model.mockIUser
import com.twidere.twiderex.mock.model.toIPaging
import com.twidere.twiderex.model.MicroBlogKey

internal class MockListsService : ListsService, MicroBlogService, ErrorService() {

    override suspend fun lists(
        userId: String?,
        screenName: String?,
        reverse: Boolean
    ): List<IListModel> {
        checkError()
        val list = mutableListOf<IListModel>()
        for (i in 0 until 20) {
            list.add(
                mockIListModel()
            )
        }
        return list.toIPaging(null)
    }

    override suspend fun createList(
        name: String,
        mode: String?,
        description: String?,
        repliesPolicy: String?
    ): IListModel {
        checkError()
        if (name == "error") throw TwitterApiException(error = "throw exception intentional")
        return mockIListModel(
            name = name,
            description = description,
            mode = mode,
        )
    }

    override suspend fun updateList(
        listId: String,
        name: String?,
        mode: String?,
        description: String?,
        repliesPolicy: String?
    ): IListModel {
        checkError()
        if (listId == "error") throw TwitterApiException(error = "throw exception intentional")
        return TwitterList(
            id = listId.toLong(),
            idStr = listId,
            name = name,
            mode = mode,
            description = description,
        )
    }

    override suspend fun destroyList(listId: String) {
        checkError()
        if (listId == "error") throw TwitterApiException(error = "throw exception intentional")
        // do nothing
    }

    override suspend fun listMembers(listId: String, count: Int, cursor: String?): List<IUser> {
        checkError()
        if (listId == "error") throw TwitterApiException(error = "throw exception intentional")
        val list = mutableListOf<IUser>()
        for (i in 0 until count) {
            list.add(
                mockIUser()
            )
        }
        return list.toIPaging()
    }

    override suspend fun addMember(listId: String, userId: String, screenName: String) {
        checkError()
        if (listId == "error") throw TwitterApiException(error = "throw exception intentional")
        // do nothing
    }

    override suspend fun removeMember(listId: String, userId: String, screenName: String) {
        checkError()
        if (listId == "error") throw TwitterApiException(error = "throw exception intentional")
        // do nothing
    }

    private val subscribers = mutableListOf<String>()
    override suspend fun listSubscribers(listId: String, count: Int, cursor: String?): List<IUser> {
        checkError()
        if (listId == "error") throw TwitterApiException(error = "throw exception intentional")
        val list = mutableListOf<IUser>()
        for (i in 0 until count) {
            list.add(
                mockIUser().also {
                    subscribers.add(it.toUi(MicroBlogKey.twitter("123")).id)
                }
            )
        }
        return list.toIPaging()
    }

    fun isSubscribers(userId: String): Boolean {
        return subscribers.contains(userId)
    }

    override suspend fun unsubscribeList(listId: String): IListModel {
        checkError()
        if (listId == "error") throw TwitterApiException(error = "throw exception intentional")
        return TwitterList(
            id = listId.toLong(),
            idStr = listId,
            name = "",
            following = false
        )
    }

    override suspend fun subscribeList(listId: String): IListModel {
        checkError()
        if (listId == "error") throw TwitterApiException(error = "throw exception intentional")
        return TwitterList(
            id = listId.toLong(),
            idStr = listId,
            name = "",
            following = true
        )
    }
}
