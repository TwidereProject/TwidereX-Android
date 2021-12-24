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

import com.twidere.services.microblog.SearchService
import com.twidere.services.microblog.model.ISearchResponse
import com.twidere.services.microblog.model.IStatus
import com.twidere.services.microblog.model.IUser
import com.twidere.twiderex.mock.model.mockIStatus
import com.twidere.twiderex.mock.model.mockIUser
import com.twidere.twiderex.mock.model.toIPaging
import org.jetbrains.annotations.TestOnly
import java.util.UUID

internal class MockSearchService @TestOnly constructor(var searchUser: List<IUser>? = null) : SearchService, ErrorService() {
    override suspend fun searchTweets(
        query: String,
        count: Int,
        nextPage: String?
    ): ISearchResponse {
        checkError()
        val list = mutableListOf<IStatus>()
        val nextKey = UUID.randomUUID().toString()
        for (i in 0 until count) {
            list.add(mockIStatus())
        }
        return MockSearchResponse(
            nextPage = nextKey,
            status = list
        )
    }

    override suspend fun searchMedia(
        query: String,
        count: Int,
        nextPage: String?
    ): ISearchResponse {
        return searchTweets(query, count, nextPage)
    }

    override suspend fun searchUsers(
        query: String,
        page: Int?,
        count: Int,
        following: Boolean
    ): List<IUser> {
        checkError()
        return (
            searchUser ?: let {
                val list = mutableListOf<IUser>()
                for (i in 0 until count) {
                    list.add(mockIUser())
                }
                list
            }
            ).toIPaging()
    }
}

internal class MockSearchResponse @TestOnly constructor(
    override val nextPage: String?,
    override val status: List<IStatus>
) : ISearchResponse
