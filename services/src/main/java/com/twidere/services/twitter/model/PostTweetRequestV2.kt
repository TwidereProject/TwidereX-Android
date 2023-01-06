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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostTweetRequestV2(
  val direct_message_deep_link: String? = null,
  val for_super_followers_only: Boolean? = null,
  val geo: Geo? = null,
  val media: Media? = null,
  val poll: Poll? = null,
  val quote_tweet_id: String? = null,
  val reply: Reply? = null,
  val reply_settings: ReplySettings? = null,
  val text: String? = null,
) {
  init {
    if (text == null) {
      require(media?.media_ids != null) { "text or media is required" }
    }
  }

  @Serializable
  data class Geo(
    val place_id: String? = null,
  )

  @Serializable
  data class Media(
    val media_ids: List<String>? = null,
    val tagged_user_ids: List<String>? = null,
  )

  @Serializable
  data class Poll(
    val options: List<String>? = null,
    val duration_minutes: Int? = null,
  )

  @Serializable
  data class Reply(
    val exclude_reply_user_ids: List<String>? = null,
    val in_reply_to_tweet_id: String? = null,
  )

  @Serializable
  enum class ReplySettings {
    @SerialName("mentionedUsers")
    MentionedUsers,

    @SerialName("following")
    Following,
  }
}

@Serializable
data class PostTweetResponse(
  val id: String? = null,
  val text: String? = null,
)

@Serializable
data class DeleteTweetResponse(
  val deleted: Boolean? = null
)
