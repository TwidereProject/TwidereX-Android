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
package com.twidere.twiderex.model.job

import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.ComposeType
import com.twidere.twiderex.model.enums.MastodonVisibility
import com.twidere.twiderex.viewmodel.compose.VoteExpired
import java.util.UUID

data class ComposeData(
    val content: String,
    val images: List<String>,
    val composeType: ComposeType,
    val statusKey: MicroBlogKey? = null,
    val lat: Double? = null,
    val long: Double? = null,
    val draftId: String = UUID.randomUUID().toString(),
    val excludedReplyUserIds: List<String>? = null,
    val voteOptions: List<String>? = null,
    val voteExpired: VoteExpired? = null,
    val voteMultiple: Boolean? = null,
    val visibility: MastodonVisibility? = null,
    val isSensitive: Boolean? = null,
    val contentWarningText: String? = null,
    val isThreadMode: Boolean = false
)
