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
package com.twidere.services.twitter.api

import com.twidere.services.twitter.model.guest.ActivateResponse
import com.twidere.services.twitter.model.guest.TwitterGuestResponse
import com.twidere.services.twitter.model.guest.User
import io.github.seiko.ktorfit.annotation.generator.GenerateApi
import io.github.seiko.ktorfit.annotation.http.GET
import io.github.seiko.ktorfit.annotation.http.POST
import io.github.seiko.ktorfit.annotation.http.Path
import io.github.seiko.ktorfit.annotation.http.Query
import io.ktor.client.HttpClient

@Suppress("NO_ACTUAL_FOR_EXPECT")
@GenerateApi
internal expect class GuestApi(client: HttpClient) {
  @POST("1.1/guest/activate.json")
  suspend fun activate(): ActivateResponse

  @GET("2/timeline/profile/{userId}.json")
  suspend fun userTimeline(
    @Path("userId") userId: String,
    @Query("cursor") cursor: String? = null,
    @Query("include_profile_interstitial_type") includeProfileInterstitialType: Int = 1,
    @Query("include_blocking") includeBlocking: Int = 1,
    @Query("include_blocked_by") includeBlockedBy: Int = 1,
    @Query("include_followed_by") includeFollowedBy: Int = 1,
    @Query("include_want_retweets") includeWantRetweets: Int = 1,
    @Query("include_mute_edge") includeMuteEdge: Int = 1,
    @Query("include_can_dm") includeCanDm: Int = 1,
    @Query("include_can_media_tag") includeCanMediaTag: Int = 1,
    @Query("skip_status") skipStatus: Int = 1,
    @Query("cards_platform") cardsPlatform: String = "Web-12",
    @Query("include_cards") includeCards: Int = 1,
    @Query("include_ext_alt_text") includeExtAltText: Int = 1,
    @Query("include_quote_count") includeQuoteCount: Int = 1,
    @Query("include_reply_count") includeReplyCount: Int = 1,
    @Query("tweet_mode") tweetMode: String = "extended",
    @Query("include_entities") includeEntities: Boolean = true,
    @Query("include_user_entities") includeUserEntities: Boolean = true,
    @Query("include_ext_media_color") includeExtMediaColor: Boolean = true,
    @Query("include_ext_media_availability") includeExtMediaAvailability: Boolean = true,
    @Query("send_error_codes") sendErrorCodes: Boolean = true,
    @Query("simple_quoted_tweet") simpleQuotedTweet: Boolean = true,
    @Query("include_tweet_replies") includeTweetReplies: Boolean = true,
    @Query("count") count: Int = 20,
    @Query("ext") ext: String = "mediaStats,highlightedLabel,cameraMoment",
  ): TwitterGuestResponse

  @GET("2/timeline/conversation/{tweetId}.json")
  suspend fun conversation(
    @Path("tweetId") tweetId: String,
    @Query("cursor") cursor: String? = null,
    @Query("include_profile_interstitial_type") includeProfileInterstitialType: Int = 1,
    @Query("include_blocking") includeBlocking: Int = 1,
    @Query("include_blocked_by") includeBlockedBy: Int = 1,
    @Query("include_followed_by") includeFollowedBy: Int = 1,
    @Query("include_want_retweets") includeWantRetweets: Int = 1,
    @Query("include_mute_edge") includeMuteEdge: Int = 1,
    @Query("include_can_dm") includeCanDm: Int = 1,
    @Query("include_can_media_tag") includeCanMediaTag: Int = 1,
    @Query("skip_status") skipStatus: Int = 1,
    @Query("cards_platform") cardsPlatform: String = "Web-12",
    @Query("include_cards") includeCards: Int = 1,
    @Query("include_ext_alt_text") includeExtAltText: Int = 1,
    @Query("include_quote_count") includeQuoteCount: Int = 1,
    @Query("include_reply_count") includeReplyCount: Int = 1,
    @Query("tweet_mode") tweetMode: String = "extended",
    @Query("include_entities") includeEntities: Boolean = true,
    @Query("include_user_entities") includeUserEntities: Boolean = true,
    @Query("include_ext_media_color") includeExtMediaColor: Boolean = true,
    @Query("include_ext_media_availability") includeExtMediaAvailability: Boolean = true,
    @Query("send_error_codes") sendErrorCodes: Boolean = true,
    @Query("simple_quoted_tweet") simpleQuotedTweet: Boolean = true,
    @Query("count") count: Int = 20,
    @Query("ext") ext: String = "mediaStats,highlightedLabel,cameraMoment",
  ): TwitterGuestResponse

  @GET("1.1/users/show.json")
  suspend fun user(
    @Query("screen_name") screenName: String? = null,
    @Query("user_id") userId: String? = null,
    @Query("include_profile_interstitial_type") includeProfileInterstitialType: Int = 1,
    @Query("include_blocking") includeBlocking: Int = 1,
    @Query("include_blocked_by") includeBlockedBy: Int = 1,
    @Query("include_followed_by") includeFollowedBy: Int = 1,
    @Query("include_want_retweets") includeWantRetweets: Int = 1,
    @Query("include_mute_edge") includeMuteEdge: Int = 1,
    @Query("include_can_dm") includeCanDm: Int = 1,
    @Query("include_can_media_tag") includeCanMediaTag: Int = 1,
    @Query("skip_status") skipStatus: Int = 1,
  ): User
}
