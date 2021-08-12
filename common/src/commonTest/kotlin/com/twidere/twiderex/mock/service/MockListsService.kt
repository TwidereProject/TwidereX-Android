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
package com.twidere.twiderex.mock.service

import com.twidere.services.microblog.ListsService
import com.twidere.services.microblog.MicroBlogService
import com.twidere.services.microblog.model.IListModel
import com.twidere.services.microblog.model.IUser
import com.twidere.services.twitter.model.TwitterList
import com.twidere.services.twitter.model.exceptions.TwitterApiException

class MockListsService : ListsService, MicroBlogService {
    override suspend fun lists(
        userId: String?,
        screenName: String?,
        reverse: Boolean
    ): List<IListModel> {
        val list = mutableListOf<IListModel>()
        for (i in 0 until 20) {
            val id = System.currentTimeMillis()
            list.add(
                TwitterList(
                    id = id,
                    idStr = id.toString(),
                    name = "list $i timestamp:${System.currentTimeMillis()}",
                )
            )
        }
        return list
    }

    override suspend fun createList(
        name: String,
        mode: String?,
        description: String?,
        repliesPolicy: String?
    ): IListModel {
        if (name == "error") throw TwitterApiException(error = "throw exception intentional")
        val id = System.currentTimeMillis()
        return TwitterList(
            id = id,
            idStr = id.toString(),
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
        if (listId == "error") throw TwitterApiException(error = "throw exception intentional")
        // do nothing
    }

    override suspend fun listMembers(listId: String, count: Int, cursor: String?): List<IUser> {
        if (listId == "error") throw TwitterApiException(error = "throw exception intentional")
        TODO("Not yet implemented")
    }

    override suspend fun addMember(listId: String, userId: String, screenName: String) {
        if (listId == "error") throw TwitterApiException(error = "throw exception intentional")
        // do nothing
    }

    override suspend fun removeMember(listId: String, userId: String, screenName: String) {
        if (listId == "error") throw TwitterApiException(error = "throw exception intentional")
        // do nothing
    }

    override suspend fun listSubscribers(listId: String, count: Int, cursor: String?): List<IUser> {
        if (listId == "error") throw TwitterApiException(error = "throw exception intentional")
        TODO("Not yet implemented")
    }

    override suspend fun unsubscribeList(listId: String): IListModel {
        if (listId == "error") throw TwitterApiException(error = "throw exception intentional")
        return TwitterList(
            id = listId.toLong(),
            idStr = listId,
            name = "",
            following = false
        )
    }

    override suspend fun subscribeList(listId: String): IListModel {
        if (listId == "error") throw TwitterApiException(error = "throw exception intentional")
        return TwitterList(
            id = listId.toLong(),
            idStr = listId,
            name = "",
            following = true
        )
    }
}
