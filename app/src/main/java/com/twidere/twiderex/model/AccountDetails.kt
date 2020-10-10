package com.twidere.twiderex.model

import android.accounts.Account
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.twidere.services.microblog.MicroBlogService
import com.twidere.services.twitter.TwitterService
import com.twidere.twiderex.db.model.DbUser
import com.twidere.twiderex.model.cred.*
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