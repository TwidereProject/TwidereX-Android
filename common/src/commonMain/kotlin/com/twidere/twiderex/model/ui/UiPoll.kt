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
package com.twidere.twiderex.model.ui

data class UiPoll(
    val id: String,
    val options: List<Option>,
    val expiresAt: Long?, // some instance of mastodon won't expire
    val expired: Boolean,
    val multiple: Boolean,
    val voted: Boolean,
    val votesCount: Long? = null,
    val votersCount: Long? = null,
    val ownVotes: List<Int>? = null,
)

data class Option(
    val text: String,
    val count: Long,
)
