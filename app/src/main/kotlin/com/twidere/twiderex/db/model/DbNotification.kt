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

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.NotificationType

@Entity(
    tableName = "notification_timeline",
    indices = [
        Index(
            value = ["notificationKey"],
            unique = true
        )
    ],
)
data class DbNotification(
    /**
     * Id that being used in the database
     */
    @PrimaryKey
    val _id: String,
    /**
     * Actual notification id
     */
    val notificationId: String,
    val notificationKey: MicroBlogKey,
    val type: NotificationType,
    val timestamp: Long,
    val userKey: MicroBlogKey,
    val statusKey: MicroBlogKey?,
)

data class DbNotificationWithUserAndStatus(
    @Embedded
    val notification: DbNotification,
    @Relation(parentColumn = "userKey", entityColumn = "userKey", entity = DbUser::class)
    val user: DbUserWithEntity,
    @Relation(
        parentColumn = "statusKey",
        entityColumn = "statusKey",
        entity = DbStatusV2::class
    )
    val status: DbStatusWithReference?,
)
