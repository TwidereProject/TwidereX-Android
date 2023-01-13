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
package com.twidere.services.twitter.model.guest

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientEventInfo(
  val details: Details? = null
)

@Serializable
data class Details(
  val conversationDetails: ConversationDetails? = null
)

@Serializable
data class ConversationDetails(
  val conversationSection: String? = null
)

@Serializable
data class ConversationThread(
  val conversationComponents: List<ConversationComponent>? = null,
  val showMoreCursor: ShowMoreCursor? = null
)

@Serializable
data class ConversationComponent(
  val conversationTweetComponent: ConversationTweetComponent? = null,
  val tombstoneComponent: TombstoneComponent? = null
)

@Serializable
data class ConversationTweetComponent(
  val tweet: ConversationTweetComponentTweet? = null
)

@Serializable
data class ConversationTweetComponentTweet(
  val id: String? = null,
  val displayType: String? = null
)

@Serializable
data class TombstoneComponent(
  val displayType: String? = null,
  val tombstoneInfo: TombstoneInfo? = null
)

@Serializable
data class TombstoneInfo(
  val text: String? = null
)

@Serializable
data class ShowMoreCursor(
  val value: String? = null,
  val cursorType: String? = null,
  val displayTreatment: DisplayTreatment? = null
)

@Serializable
data class DisplayTreatment(
  val actionText: String? = null
)

@Serializable
data class TerminateTimeline(
  val direction: String? = null
)

@Serializable
data class User(
  val id: Double? = null,

  @SerialName("id_str")
  val idStr: String? = null,

  val name: String? = null,

  @SerialName("screen_name")
  val screenName: String? = null,

  val location: String? = null,

  val description: String? = null,
  val url: String? = null,
  val entities: Entities? = null,
  val protected: Boolean? = null,

  @SerialName("followers_count")
  val followersCount: Long? = null,

  @SerialName("friends_count")
  val friendsCount: Long? = null,

  @SerialName("listed_count")
  val listedCount: Long? = null,

  @SerialName("created_at")
  @Serializable(with = GuestDateSerializer::class)
  val createdAt: Instant? = null,

  @SerialName("favourites_count")
  val favouritesCount: Long? = null,

  @SerialName("geo_enabled")
  val geoEnabled: Boolean? = null,

  val verified: Boolean? = null,

  @SerialName("statuses_count")
  val statusesCount: Long? = null,

  @SerialName("media_count")
  val mediaCount: Long? = null,

  // val lang: JsonObject? = null,

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

  @SerialName("has_custom_timelines")
  val hasCustomTimelines: Boolean? = null,
  @SerialName("profile_interstitial_type")
  val profileInterstitialType: String? = null,

  @SerialName("business_profile_state")
  val businessProfileState: String? = null,

  @SerialName("translator_type")
  val translatorType: String? = null,

  @SerialName("require_some_consent")
  val requireSomeConsent: Boolean? = null,

  @SerialName("profile_image_extensions_media_color")
  val profileImageExtensionsMediaColor: MediaColor? = null,

  @SerialName("profile_image_extensions")
  val profileImageExtensions: ProfileExtensions? = null,

  @SerialName("profile_banner_extensions_media_color")
  val profileBannerExtensionsMediaColor: MediaColor? = null,

  @SerialName("profile_banner_extensions")
  val profileBannerExtensions: ProfileExtensions? = null,

  @SerialName("has_no_screen_name")
  val hasNoScreenName: Boolean? = null,

)

@Serializable
data class Entities(
  val description: Description? = null,
  val url: Urls? = null
)

@Serializable
data class Description(
  val urls: List<URL>? = null
)

@Serializable
data class Urls(
  val urls: List<URL>? = null,
)

@Serializable
data class URL(
  val url: String? = null,

  @SerialName("expanded_url")
  val expandedURL: String? = null,

  @SerialName("display_url")
  val displayURL: String? = null,

  val indices: List<Long>? = null
)
