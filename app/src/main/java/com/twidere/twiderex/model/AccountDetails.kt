package com.twidere.twiderex.model

import android.accounts.Account
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.twidere.twiderex.model.cred.*
import com.twidere.twiderex.utils.fromJson
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class AccountDetails(
    val account: Account,
    val type: AccountType,
    val key: UserKey,
    val credentials_type: CredentialsType,
    @Json(name = "credentials")
    val credentials_json: String,
    @Json(name = "extras")
    val extras_json: String,
) : Parcelable {
    val credentials: Credentials?
        get() = when (credentials_type) {
            CredentialsType.OAuth,
            CredentialsType.XAuth -> credentials_json.fromJson<OAuthCredentials>()
            CredentialsType.Basic -> credentials_json.fromJson<BasicCredentials>()
            CredentialsType.Empty -> credentials_json.fromJson<EmptyCredentials>()
            CredentialsType.OAuth2 -> credentials_json.fromJson<OAuth2Credentials>()
        }
}