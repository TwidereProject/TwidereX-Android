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
package com.twidere.services.mastodon.model

import com.twidere.services.microblog.model.IPaging
import retrofit2.Response

class MastodonPaging<T>(
    data: List<T>,
    val next: String? = null,
    val prev: String? = null,
) : ArrayList<T>(data), IPaging {
    companion object {
        fun <T> from(response: Response<List<T>>): MastodonPaging<T> {
            val link = response.headers().get("link")
            val next = link?.let { "max_id=(\\d+)".toRegex().find(it) }?.groupValues?.getOrNull(1)
            val prev = link?.let { "min_id=(\\d+)".toRegex().find(it) }?.groupValues?.getOrNull(1)
            return MastodonPaging(
                data = response.body() ?: emptyList(),
                next = next,
                prev = prev,
            )
        }
    }

    override val nextPage: String?
        get() = next
}
