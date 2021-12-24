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
package com.twidere.twiderex.room.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.ReferenceType
import com.twidere.twiderex.room.db.model.DbStatusReference
import com.twidere.twiderex.room.db.model.DbStatusReferenceWithStatus

@Dao
internal interface RoomStatusReferenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<DbStatusReference>)

    @Transaction
    @Query("SELECT * FROM status_reference WHERE referenceStatusKey == :key AND referenceType == :referenceType")
    suspend fun find(key: MicroBlogKey, referenceType: ReferenceType): List<DbStatusReferenceWithStatus>

    @Query("DELETE FROM status_reference WHERE statusKey in (:key)")
    suspend fun remove(key: List<MicroBlogKey>)

    @Query("DELETE FROM status_reference WHERE statusKey == :key")
    suspend fun delete(key: MicroBlogKey)
}
