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
 
package com.twidere.twiderex.model

import android.accounts.Account
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.twidere.services.microblog.MicroBlogService
import com.twidere.services.twitter.TwitterService
import com.twidere.twiderex.db.model.DbUser
import com.twidere.twiderex.model.cred.BasicCredentials
import com.twidere.twiderex.model.cred.Credentials
import com.twidere.twiderex.model.cred.CredentialsType
import com.twidere.twiderex.model.cred.EmptyCredentials
import com.twidere.twiderex.model.cred.OAuth2Credentials
import com.twidere.twiderex.model.cred.OAuthCredentials
import com.twidere.twiderex.utils.fromJson
import kotlinx.android.parcel.IgnoredOnParcel

@JsonClass(generateAdapter = true)
data class AccountDetails(
    val account: Account,
    val type: PlatformType,
    val key: UserKey,
    val credentials_type: CredentialsType,
    @Json(name = "credentials")
    var credentials_json: String,
    @Json(name = "extras")
    val extras_json: String,
    var user: DbUser,
) {

    @IgnoredOnParcel
    val credentials: Credentials?
        get() = when (credentials_type) {
            CredentialsType.OAuth,
            CredentialsType.XAuth -> credentials_json.fromJson<OAuthCredentials>()
            CredentialsType.Basic -> credentials_json.fromJson<BasicCredentials>()
            CredentialsType.Empty -> credentials_json.fromJson<EmptyCredentials>()
            CredentialsType.OAuth2 -> credentials_json.fromJson<OAuth2Credentials>()
        }

    @IgnoredOnParcel
    val service by lazy<MicroBlogService> {
        when (type) {
            PlatformType.Twitter -> {
                credentials?.let {
                    it as? OAuthCredentials
                }?.let {
                    TwitterService(
                        consumer_key = it.consumer_key,
                        consumer_secret = it.consumer_secret,
                        access_token = it.access_token,
                        access_token_secret = it.access_token_secret,
                    )
                } as MicroBlogService
            }
            PlatformType.StatusNet -> TODO()
            PlatformType.Fanfou -> TODO()
            PlatformType.Mastodon -> TODO()
        }
    }
}
