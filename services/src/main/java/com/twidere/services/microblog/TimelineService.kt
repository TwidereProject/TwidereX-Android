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
package com.twidere.services.microblog

import com.twidere.services.microblog.model.IStatus

interface TimelineService {
    suspend fun homeTimeline(
        count: Int = 20,
        since_id: String? = null,
        max_id: String? = null,
    ): List<IStatus>

    suspend fun mentionsTimeline(
        count: Int = 20,
        since_id: String? = null,
        max_id: String? = null,
    ): List<IStatus>

    suspend fun userTimeline(
        user_id: String,
        count: Int = 20,
        since_id: String? = null,
        max_id: String? = null,
        exclude_replies: Boolean = false,
    ): List<IStatus>

    suspend fun favorites(
        user_id: String,
        count: Int = 20,
        since_id: String? = null,
        max_id: String? = null,
    ): List<IStatus>

    suspend fun listTimeline(
        list_id: String,
        count: Int = 20,
        max_id: String? = null,
        since_id: String? = null,
    ): List<IStatus>
}
