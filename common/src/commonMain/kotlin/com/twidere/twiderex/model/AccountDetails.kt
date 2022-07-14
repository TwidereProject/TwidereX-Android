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
package com.twidere.twiderex.model

import com.twidere.twiderex.http.TwidereServiceFactory
import com.twidere.twiderex.model.cred.BasicCredentials
import com.twidere.twiderex.model.cred.Credentials
import com.twidere.twiderex.model.cred.CredentialsType
import com.twidere.twiderex.model.cred.EmptyCredentials
import com.twidere.twiderex.model.cred.OAuth2Credentials
import com.twidere.twiderex.model.cred.OAuthCredentials
import com.twidere.twiderex.model.enums.ListType
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.model.ui.UserMetrics
import com.twidere.twiderex.utils.fromJson

data class AccountDetails(
    val account: TwidereAccount,
    val type: PlatformType,
    // Note that UserKey that being used in AccountDetails is idStr@domain, not screenName@domain
    val accountKey: MicroBlogKey,
    val credentials_type: CredentialsType,
    var credentials_json: String,
    val extras_json: String,
    var user: AmUser,
    var lastActive: Long,
    val preferences: AccountPreferences,
) {
    val credentials: Credentials
        get() = when (credentials_type) {
            CredentialsType.OAuth,
            CredentialsType.XAuth -> credentials_json.fromJson<OAuthCredentials>()
            CredentialsType.Basic -> credentials_json.fromJson<BasicCredentials>()
            CredentialsType.Empty -> credentials_json.fromJson<EmptyCredentials>()
            CredentialsType.OAuth2 -> credentials_json.fromJson<OAuth2Credentials>()
        }

    val service by lazy {
        TwidereServiceFactory.createApiService(
            type = type,
            credentials = credentials,
            accountKey = accountKey
        )
    }

    val listType: ListType
        get() = when (type) {
            PlatformType.Twitter -> ListType.All
            PlatformType.StatusNet -> TODO()
            PlatformType.Fanfou -> TODO()
            PlatformType.Mastodon -> ListType.Owned
        }

    val supportDirectMessage = type == PlatformType.Twitter

    fun toUi() = with(user) {
        UiUser(
            id = userId,
            name = name,
            screenName = screenName,
            profileImage = profileImage,
            profileBackgroundImage = profileBackgroundImage,
            metrics = UserMetrics(
                fans = followersCount,
                follow = friendsCount,
                listed = listedCount,
                status = 0
            ),
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
