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
package com.twidere.services.twitter.model

import com.twidere.services.microblog.model.IUser
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long? = null,

    @SerialName("id_str")
    val idStr: String? = null,

    val name: String? = null,

    @SerialName("screen_name")
    val screenName: String? = null,

    val location: String? = null,
    val description: String? = null,
    val url: String? = null,
    val entities: UserEntities? = null,
    val protected: Boolean? = null,

    @SerialName("followers_count")
    val followersCount: Long? = null,

    @SerialName("friends_count")
    val friendsCount: Long? = null,

    @SerialName("listed_count")
    val listedCount: Long? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("favourites_count")
    val favouritesCount: Long? = null,

    @SerialName("utc_offset")
    val utcOffset: Int? = null,

    @SerialName("time_zone")
    val timeZone: String? = null,

    @SerialName("geo_enabled")
    val geoEnabled: Boolean? = null,

    val verified: Boolean? = null,

    @SerialName("statuses_count")
    val statusesCount: Long? = null,

    val lang: String? = null,
    val status: Status? = null,

    @SerialName("contributors_enabled")
    val contributorsEnabled: Boolean? = null,

    @SerialName("is_translator")
    val isTranslator: Boolean? = null,

    @SerialName("is_translation_enabled")
    val isTranslationEnabled: Boolean? = null,

    @SerialName("profile_background_color")
    val profileBackgroundColor: String? = null,

    @SerialName("profile_background_image_url")
    val profileBackgroundImageURL: String? = null,

    @SerialName("profile_background_image_url_https")
    val profileBackgroundImageURLHTTPS: String? = null,

    @SerialName("profile_background_tile")
    val profileBackgroundTile: Boolean? = null,

    @SerialName("profile_image_url")
    val profileImageURL: String? = null,

    @SerialName("profile_image_url_https")
    val profileImageURLHTTPS: String? = null,

    @SerialName("profile_banner_url")
    val profileBannerURL: String? = null,

    @SerialName("profile_link_color")
    val profileLinkColor: String? = null,

    @SerialName("profile_sidebar_border_color")
    val profileSidebarBorderColor: String? = null,

    @SerialName("profile_sidebar_fill_color")
    val profileSidebarFillColor: String? = null,

    @SerialName("profile_text_color")
    val profileTextColor: String? = null,

    @SerialName("profile_use_background_image")
    val profileUseBackgroundImage: Boolean? = null,

    @SerialName("has_extended_profile")
    val hasExtendedProfile: Boolean? = null,

    @SerialName("default_profile")
    val defaultProfile: Boolean? = null,

    @SerialName("default_profile_image")
    val defaultProfileImage: Boolean? = null,

    val following: Boolean? = null,

    @SerialName("follow_request_sent")
    val followRequestSent: Boolean? = null,

    val notifications: Boolean? = null,
    val blocking: Boolean? = null,

    @SerialName("blocked_by")
    val blockedBy: Boolean? = null,

    @SerialName("translator_type")
    val translatorType: String? = null,

    val suspended: Boolean? = null,

    @SerialName("needs_phone_verification")
    val needsPhoneVerification: Boolean? = null,

//    @SerialName("profile_image_extensions_alt_text")
//    val profileImageExtensionsAltText: Any? = null,
//
//    @SerialName("profile_banner_extensions_alt_text")
//    val profileBannerExtensionsAltText: Any? = null
) : IUser
