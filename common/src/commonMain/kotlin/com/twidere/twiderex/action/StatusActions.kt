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
package com.twidere.twiderex.action

import androidx.compose.runtime.compositionLocalOf
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.ui.UiStatus

val LocalStatusActions = compositionLocalOf<IStatusActions> { error("No LocalStatusActions") }

interface IStatusActions {
    fun like(status: UiStatus, account: AccountDetails) {}
    fun retweet(status: UiStatus, account: AccountDetails) {}
    fun delete(status: UiStatus, account: AccountDetails) {}
    fun vote(status: UiStatus, account: AccountDetails, votes: List<Int>) {}
}

expect class StatusActions : IStatusActions {
    override fun delete(status: UiStatus, account: AccountDetails)
    override fun like(status: UiStatus, account: AccountDetails)
    override fun retweet(status: UiStatus, account: AccountDetails)
    override fun vote(status: UiStatus, account: AccountDetails, votes: List<Int>)
}

object FakeStatusActions : IStatusActions
