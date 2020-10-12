/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
 
package com.twidere.twiderex.model.adapter

import android.accounts.Account
import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.twidere.twiderex.model.JsonAccount

class AndroidAccountAdapter {
    @FromJson
    fun fromJson(account: String): Account? {
        return Moshi.Builder().build().adapter(JsonAccount::class.java).fromJson(account)?.let {
            Account(it.name, it.type)
        }
    }

    @ToJson
    fun toJson(account: Account): String {
        return Moshi.Builder().build().adapter<JsonAccount>(JsonAccount::class.java).toJson(
            JsonAccount(
                account.name,
                account.type
            )
        )
    }
}
