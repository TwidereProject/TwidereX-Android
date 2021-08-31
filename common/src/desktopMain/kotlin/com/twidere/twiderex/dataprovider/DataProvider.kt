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
package com.twidere.twiderex.dataprovider

import com.twidere.twiderex.cache.FileCacheHandler
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.CacheDatabase

actual class DataProvider {
    // data provide functions....
    actual companion object Factory {
        actual fun create(): DataProvider {
            TODO("Not yet implemented")
        }
    }

    actual val appDatabase: AppDatabase
        get() = TODO("Not yet implemented")

    actual val cacheDatabase: CacheDatabase
        get() = TODO("Not yet implemented")

    actual val fileCacheHandler: FileCacheHandler
        get() = TODO("Not yet implemented")
}