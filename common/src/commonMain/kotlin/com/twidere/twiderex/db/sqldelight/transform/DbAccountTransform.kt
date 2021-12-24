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
package com.twidere.twiderex.db.sqldelight.transform

import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.AccountPreferences
import com.twidere.twiderex.sqldelight.table.DbAccount

internal fun AccountDetails.toDbAccount() = DbAccount(
    accountKey = accountKey,
    account = account,
    type = type,
    credentials_type = credentials_type,
    credentials_json = credentials_json,
    extras_json = extras_json,
    user = user,
    lastActive = lastActive
)

internal fun DbAccount.toUi(preferences: AccountPreferences) = AccountDetails(
    accountKey = accountKey,
    account = account,
    type = type,
    credentials_type = credentials_type,
    credentials_json = credentials_json,
    extras_json = extras_json,
    user = user,
    lastActive = lastActive,
    preferences = preferences
)
