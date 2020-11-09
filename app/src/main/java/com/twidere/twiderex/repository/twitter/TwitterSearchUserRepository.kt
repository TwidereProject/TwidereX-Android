/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.repository.twitter

import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.twidere.services.microblog.SearchService
import com.twidere.twiderex.db.mapper.toDbUser
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.model.ui.UiUser.Companion.toUi

class TwitterSearchUserRepository @AssistedInject constructor(
    @Assisted private val service: SearchService,
    @Assisted private val loadCount: Int,
) {
    @AssistedInject.Factory
    interface AssistedFactory {
        fun create(
            service: SearchService,
            loadCount: Int
        ): TwitterSearchUserRepository
    }

    suspend fun loadUsers(query: String, page: Int = 0): List<UiUser> {
        val result = service.searchUsers(query, page, loadCount)
        return result.map { it.toDbUser().toUi() }
    }
}
