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
package com.twidere.twiderex.mock

import com.twidere.services.microblog.MicroBlogService
import com.twidere.twiderex.mock.db.MockRoomCacheDatabase
import com.twidere.twiderex.mock.service.MockListsService
import com.twidere.twiderex.mock.service.MockTrendService
import com.twidere.twiderex.room.db.RoomCacheDatabase

object MockCenter {
    fun mockCacheDatabase(): RoomCacheDatabase {
        return MockRoomCacheDatabase()
    }

    fun mockListsService(): MicroBlogService {
        return MockListsService()
    }

    fun mockTrendService(): MicroBlogService {
        return MockTrendService()
    }
}
