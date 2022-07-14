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
package com.twidere.twiderex.repository

import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.AccountPreferences
import com.twidere.twiderex.model.AmUser
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.cred.CredentialsType
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.ui.UiUser
import kotlinx.coroutines.flow.Flow

expect class AccountRepository {
    val activeAccount: Flow<AccountDetails?>
    val accounts: Flow<List<AccountDetails>>
    fun updateAccount(user: UiUser)
    fun getAccounts(): List<AccountDetails>
    fun hasAccount(): Boolean
    fun findByAccountKey(accountKey: MicroBlogKey): AccountDetails?
    fun setCurrentAccount(detail: AccountDetails)
    fun addAccount(
        displayKey: MicroBlogKey,
        type: PlatformType,
        accountKey: MicroBlogKey,
        credentials_type: CredentialsType,
        credentials_json: String,
        extras_json: String,
        user: AmUser,
        lastActive: Long,
    )
    fun getAccountPreferences(accountKey: MicroBlogKey): AccountPreferences
    fun containsAccount(key: MicroBlogKey): Boolean
    fun updateAccount(detail: AccountDetails)
    fun delete(detail: AccountDetails)
    fun getFirstByType(type: PlatformType): AccountDetails?
}
