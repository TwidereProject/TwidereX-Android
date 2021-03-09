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
package com.twidere.twiderex.model

import android.accounts.Account
import com.twidere.services.mastodon.MastodonService
import com.twidere.services.microblog.MicroBlogService
import com.twidere.services.twitter.TwitterService
import com.twidere.twiderex.model.adapter.AndroidAccountSerializer
import com.twidere.twiderex.model.cred.BasicCredentials
import com.twidere.twiderex.model.cred.Credentials
import com.twidere.twiderex.model.cred.CredentialsType
import com.twidere.twiderex.model.cred.EmptyCredentials
import com.twidere.twiderex.model.cred.OAuth2Credentials
import com.twidere.twiderex.model.cred.OAuthCredentials
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.utils.fromJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountDetails(
    @Serializable(with = AndroidAccountSerializer::class)
    val account: Account,
    val type: PlatformType,
    // Note that UserKey that being used in AccountDetails is idStr@domain, not screenName@domain
    val accountKey: MicroBlogKey,
    val credentials_type: CredentialsType,
    @SerialName("credentials")
    var credentials_json: String,
    @SerialName("extras")
    val extras_json: String,
    var user: AmUser,
    var lastActive: Long,
) {
    val credentials: Credentials
        get() = when (credentials_type) {
            CredentialsType.OAuth,
            CredentialsType.XAuth -> credentials_json.fromJson<OAuthCredentials>()
            CredentialsType.Basic -> credentials_json.fromJson<BasicCredentials>()
            CredentialsType.Empty -> credentials_json.fromJson<EmptyCredentials>()
            CredentialsType.OAuth2 -> credentials_json.fromJson<OAuth2Credentials>()
        }

    val service by lazy<MicroBlogService> {
        when (type) {
            PlatformType.Twitter -> {
                credentials.let {
                    it as OAuthCredentials
                }.let {
                    TwitterService(
                        consumer_key = it.consumer_key,
                        consumer_secret = it.consumer_secret,
                        access_token = it.access_token,
                        access_token_secret = it.access_token_secret,
                    )
                }
            }
            PlatformType.StatusNet -> TODO()
            PlatformType.Fanfou -> TODO()
            PlatformType.Mastodon ->
                credentials.let {
                    it as OAuth2Credentials
                }.let {
                    MastodonService(accountKey.host, it.access_token)
                }
        }
    }

    fun toUi() = with(user) {
        UiUser(
            id = userId,
            name = name,
            screenName = screenName,
            profileImage = profileImage,
            profileBackgroundImage = profileBackgroundImage,
            followersCount = followersCount,
            friendsCount = friendsCount,
            listedCount = listedCount,
            rawDesc = desc,
            htmlDesc = desc,
            website = website,
            location = location,
            verified = verified,
            protected = isProtected,
            userKey = userKey,
            platformType = type,
            acct = userKey.copy(id = screenName),
        )
    }
}
