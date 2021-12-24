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

import com.twidere.services.microblog.DirectMessageService
import com.twidere.services.microblog.model.IDirectMessage
import com.twidere.twiderex.mock.model.mockIDirectMessage
import com.twidere.twiderex.mock.model.toIPaging
import com.twidere.twiderex.model.MicroBlogKey
import org.jetbrains.annotations.TestOnly

internal class MockDirectMessageService @TestOnly constructor(private val accountKey: MicroBlogKey, var messages: List<IDirectMessage>? = null) : DirectMessageService,
    ErrorService() {
    private val deletedMessageId = mutableListOf<String>()

    fun isDeleted(id: String) = deletedMessageId.contains(id)

    override suspend fun destroyDirectMessage(id: String) {
        checkError()
        deletedMessageId.add(id)
    }

    override suspend fun getDirectMessages(cursor: String?, count: Int?): List<IDirectMessage> {
        checkError()
        return (
            messages ?: let {
                val list = mutableListOf<IDirectMessage>()
                for (i in 0 until (count ?: 1)) {
                    list.add(mockIDirectMessage(accountId = accountKey.id, inCome = i % 2 == 0))
                }
                list
            }
            ).toIPaging()
    }

    override suspend fun showDirectMessage(id: String): IDirectMessage? {
        checkError()
        return mockIDirectMessage(id = id, accountId = accountKey.id)
    }
}
