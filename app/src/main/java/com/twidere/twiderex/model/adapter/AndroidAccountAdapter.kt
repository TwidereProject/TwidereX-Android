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