package com.twidere.twiderex.model.ui

import android.os.Parcelable
import com.twidere.twiderex.db.model.DbUser
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UiUser(
    val id: String,
    val name: String,
    val screenName: String,
    val profileImage: String,
    val profileBackgroundImage: String?,
    val followersCount: Long,
    val friendsCount: Long,
    val listedCount: Long,
    val desc: String,
    val website: String?,
    val location: String?,
    val verified: Boolean,
    val protected: Boolean,
) : Parcelable {
    companion object {
        fun DbUser.toUi() = UiUser(
            id = id,
            name = name,
            screenName = screenName,
            profileImage = profileImage,
            profileBackgroundImage = profileBackgroundImage,
            followersCount = followersCount,
            friendsCount = friendsCount,
            listedCount = listedCount,
            desc = desc,
            website = website,
            location = location,
            verified = verified,
            protected = isProtected,
        )
    }
}