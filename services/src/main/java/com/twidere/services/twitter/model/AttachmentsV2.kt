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
package com.twidere.services.twitter.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class AttachmentsV2(
    @SerialName("media_keys")
    val mediaKeys: List<String>? = null,
    @SerialName("poll_ids")
    val pollIds: List<String>? = null,
) {
    internal fun setExtra(includesV2: IncludesV2) {
        mediaKeys?.let {
            media = includesV2.media?.filter { mediaKeys.contains(it.mediaKey) }
        }
        pollIds?.let {
            poll = includesV2.polls?.filter { pollIds.contains(it.id) }
        }
    }

    @Transient
    var media: List<MediaV2>? = null
    @Transient
    var poll: List<PollV2>? = null
}
