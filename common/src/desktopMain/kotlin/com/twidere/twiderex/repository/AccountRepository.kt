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
package com.twidere.twiderex.repository

import android.accounts.Account
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.AccountPreferences
import com.twidere.twiderex.model.AmUser
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.cred.CredentialsType
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.ui.UiUser
import kotlinx.coroutines.flow.SharedFlow

actual class AccountRepository {
    actual val activeAccount: SharedFlow<AccountDetails?>
        get() = TODO("Not yet implemented")
    actual val accounts: SharedFlow<List<AccountDetails>>
        get() = TODO("Not yet implemented")

    actual fun updateAccount(user: UiUser) {
    }

    actual fun updateAccount(detail: AccountDetails) {
    }

    actual fun getAccounts(): List<Account> {
        TODO("Not yet implemented")
    }

    actual fun hasAccount(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun findByAccountKey(accountKey: MicroBlogKey): Account? {
        TODO("Not yet implemented")
    }

    actual fun setCurrentAccount(detail: AccountDetails) {
    }

    actual fun addAccount(
        account: Account,
        type: PlatformType,
        accountKey: MicroBlogKey,
        credentials_type: CredentialsType,
        credentials_json: String,
        extras_json: String,
        user: AmUser,
        lastActive: Long
    ) {
    }

    actual fun getAccountDetails(account: Account): AccountDetails {
        TODO("Not yet implemented")
    }

    actual fun getAccountPreferences(accountKey: MicroBlogKey): AccountPreferences {
        TODO("Not yet implemented")
    }

    actual fun containsAccount(key: MicroBlogKey): Boolean {
        TODO("Not yet implemented")
    }

    actual fun delete(detail: AccountDetails) {
    }

    actual fun getFirstByType(type: PlatformType): AccountDetails? {
        TODO("Not yet implemented")
    }
}
