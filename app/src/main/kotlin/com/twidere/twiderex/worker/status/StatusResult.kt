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
package com.twidere.twiderex.worker.status

import androidx.work.workDataOf
import com.twidere.twiderex.model.MicroBlogKey

data class StatusResult(
    val statusKey: MicroBlogKey,
    val accountKey: MicroBlogKey,
    val retweeted: Boolean? = null,
    val liked: Boolean? = null,
    val retweetCount: Long? = null,
    val likeCount: Long? = null,
) {
    fun toWorkData() = workDataOf(
        "statusKey" to statusKey.toString(),
        "accountKey" to accountKey.toString(),
        "liked" to liked,
        "retweeted" to retweeted,
        "retweetCount" to retweetCount,
        "likeCount" to likeCount,
    )
}
