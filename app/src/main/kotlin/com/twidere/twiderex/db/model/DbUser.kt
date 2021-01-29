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
package com.twidere.twiderex.db.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.PlatformType

@Entity(
    tableName = "user",
    indices = [Index(value = ["userKey"], unique = true)],
)
data class DbUser(
    /**
     * Id that being used in the database
     */
    @PrimaryKey
    var _id: String,
    val userId: String,
    val name: String,
    val userKey: MicroBlogKey,
    val screenName: String,
    val profileImage: String,
    val profileBackgroundImage: String?,
    val followersCount: Long,
    val friendsCount: Long,
    val listedCount: Long,
    val desc: String,
    val website: String?,
    val location: String?,
    val verified: Boolean,
    val isProtected: Boolean,
    val platformType: PlatformType,
)
