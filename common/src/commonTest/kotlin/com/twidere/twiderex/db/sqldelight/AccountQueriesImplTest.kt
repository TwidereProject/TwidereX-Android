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
package com.twidere.twiderex.db.sqldelight

import com.twidere.twiderex.base.BaseAppDatabaseTest
import com.twidere.twiderex.dataprovider.mapper.toAmUser
import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.mock.model.mockIUser
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.TwidereAccount
import com.twidere.twiderex.model.cred.CredentialsType
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.sqldelight.table.DbAccount
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class AccountQueriesImplTest : BaseAppDatabaseTest() {
    private val accountKey = MicroBlogKey.twitter("123")

    @Test
    fun insertAccount_InsertOrReplaceWhenContentAndAccountKeyEquals() = runBlocking {
        val query = database.accountQueries
        val insert = DbAccount(
            accountKey = accountKey,
            account = TwidereAccount("name", "type"),
            type = PlatformType.Twitter,
            credentials_json = "insert",
            credentials_type = CredentialsType.Basic,
            extras_json = "",
            user = mockIUser().toUi(accountKey).toAmUser(),
            lastActive = System.currentTimeMillis()
        )
        query.insert(insert)
        var result = query.findWithAccountKey(accountKey).executeAsOneOrNull()
        assertEquals("insert", result?.credentials_json)
        query.insert(insert.copy(credentials_json = "update"))
        result = query.findWithAccountKey(accountKey).executeAsOneOrNull()
        assertEquals("update", result?.credentials_json)
    }

    @Test
    fun deleteAccount_DeleteAccountWithGivenKeys(): Unit = runBlocking {
        val query = database.accountQueries
        val insert = DbAccount(
            accountKey = accountKey,
            account = TwidereAccount("name", "type"),
            type = PlatformType.Twitter,
            credentials_json = "insert",
            credentials_type = CredentialsType.Basic,
            extras_json = "",
            user = mockIUser().toUi(accountKey).toAmUser(),
            lastActive = System.currentTimeMillis()
        )
        query.insert(insert)
        var result = query.findWithAccountKey(accountKey).executeAsOneOrNull()
        assertNotNull(result)
        query.delete(insert.accountKey)
        result = query.findWithAccountKey(accountKey).executeAsOneOrNull()
        assertNull(result)
    }
}
