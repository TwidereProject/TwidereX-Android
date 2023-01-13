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

import com.twidere.services.twitter.model.GeoPoint
import com.twidere.services.twitter.model.Place
import com.twidere.services.twitter.model.PurpleMedia
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class TwitterGuestResponse(
  val globalObjects: GlobalObjects? = null,
  val timeline: Timeline? = null
)

@Serializable
data class GlobalObjects(
  val tweets: Map<String, TweetValue>? = null,
  val users: Map<String, User>? = null,
  // val moments: Broadcasts? = null,
  // val cards: Broadcasts? = null,
  // val places: Broadcasts? = null,
  // val media: Broadcasts? = null,
  // val broadcasts: Broadcasts? = null,
  // val topics: Broadcasts? = null,
  // val lists: Broadcasts? = null,
)

// @Serializable
// class Broadcasts()

@OptIn(ExperimentalSerializationApi::class)
// @Serializer(forClass = Instant::class)
internal object GuestDateSerializer : KSerializer<Instant> {
  override val descriptor: SerialDescriptor
    get() = PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder): Instant {
    val str = decoder.decodeString()
    val values = str.split(' ')
    return try {
      LocalDateTime(
        year = values[5].toInt(),
        monthNumber = values[1].let {
          when (it) {
            "Jan" -> 1
            "Feb" -> 2
            "Mar" -> 3
            "Apr" -> 4
            "May" -> 5
            "Jun" -> 6
            "Jul" -> 7
            "Aug" -> 8
            "Sep" -> 9
            "Oct" -> 10
            "Nov" -> 11
            "Dec" -> 12
            else -> 0
          }
        },
        dayOfMonth = values[2].toInt(),
        hour = values[3].split(':')[0].toInt(),
        minute = values[3].split(':')[1].toInt(),
        second = values[3].split(':')[2].toInt(),
        nanosecond = 0
      ).toInstant(TimeZone.UTC)
    } catch (e: Throwable) {
      Instant.fromEpochMilliseconds(0)
    }
  }

  override fun serialize(encoder: Encoder, value: Instant) {
    encoder.encodeString(value.toString())
  }
}

@Serializable
data class TweetValue(
  @SerialName("created_at")
  @Serializable(with = GuestDateSerializer::class)
  val createdAt: Instant? = null,

  val id: Double? = null,

  @SerialName("id_str")
  val idStr: String? = null,

  @SerialName("full_text")
  val fullText: String? = null,
  val text: String? = null,

  val truncated: Boolean? = null,

  @SerialName("display_text_range")
  val displayTextRange: List<Long>? = null,

  val entities: TweetEntities? = null,
  val source: String? = null,

  @SerialName("in_reply_to_status_id")
  val inReplyToStatusID: Double? = null,

  @SerialName("in_reply_to_status_id_str")
  val inReplyToStatusIDStr: String? = null,

  @SerialName("in_reply_to_user_id")
  val inReplyToUserID: Long? = null,

  @SerialName("in_reply_to_user_id_str")
  val inReplyToUserIDStr: String? = null,

  @SerialName("in_reply_to_screen_name")
  val inReplyToScreenName: String? = null,

  @SerialName("user_id")
  val userID: Double? = null,

  @SerialName("user_id_str")
  val userIDStr: String? = null,

  val geo: GeoPoint? = null,
  // val coordinates: JsonObject? = null,
  val place: Place? = null,
  // val contributors: JsonObject? = null,

  @SerialName("is_quote_status")
  val isQuoteStatus: Boolean? = null,

  @SerialName("retweet_count")
  val retweetCount: Long? = null,

  @SerialName("favorite_count")
  val favoriteCount: Long? = null,

  @SerialName("reply_count")
  val replyCount: Long? = null,

  @SerialName("conversation_id")
  val conversationID: Double? = null,

  @SerialName("conversation_id_str")
  val conversationIDStr: String? = null,

  val favorited: Boolean? = null,
  val retweeted: Boolean? = null,
  val lang: String? = null,

  // @SerialName("supplemental_language")
  // val supplementalLanguage: JsonObject? = null,

  @SerialName("self_thread")
  val selfThread: SelfThread? = null,

  @SerialName("quoted_status_id")
  val quotedStatusID: Double? = null,

  @SerialName("quoted_status_id_str")
  val quotedStatusIDStr: String? = null,

  @SerialName("quoted_status_permalink")
  val quotedStatusPermalink: QuotedStatusPermalink? = null,

  @SerialName("possibly_sensitive")
  val possiblySensitive: Boolean? = null,

  @SerialName("possibly_sensitive_editable")
  val possiblySensitiveEditable: Boolean? = null,

  @SerialName("retweeted_status_id")
  val retweetedStatusID: Double? = null,

  @SerialName("retweeted_status_id_str")
  val retweetedStatusIDStr: String? = null,

  @SerialName("extended_entities")
  val extendedEntities: ExtendedEntities? = null,

  val card: Card? = null,
) {

  @Serializable
  data class Card(
    val name: String? = null,
    val url: String? = null,
    @SerialName("card_type_url")
    val cardTypeUrl: String? = null,
    @SerialName("binding_values")
    val bindingValues: BindingValues? = null,
    @SerialName("card_platform")
    val cardPlatform: CardPlatform? = null,
  ) {

    @Serializable
    data class BindingValues(
      @SerialName("vanity_url")
      val vanityUrl: VanityUrl? = null,
      val domain: VanityUrl? = null,
      val title: VanityUrl? = null,
      val description: VanityUrl? = null,
      @SerialName("thumbnail_image_small")
      val thumbnailImageSmall: ThumbnailImage? = null,
      @SerialName("thumbnail_image")
      val thumbnailImage: ThumbnailImage? = null,
      @SerialName("thumbnail_image_large")
      val thumbnailImageLarge: ThumbnailImage? = null,
      @SerialName("thumbnail_image_x_large")
      val thumbnailImageXLarge: ThumbnailImage? = null,
      @SerialName("thumbnail_image_color")
      val thumbnailImageColor: ThumbnailImageColor? = null,
      @SerialName("thumbnail_image_original")
      val thumbnailImageOriginal: ThumbnailImage? = null,
      @SerialName("card_url")
      val cardUrl: VanityUrl? = null,
    ) {
      @Serializable
      data class VanityUrl(
        val type: String? = null,
        @SerialName("string_value")
        val stringValue: String? = null,
        @SerialName("scribe_key")
        val scribeKey: String? = null,
      )

      @Serializable
      data class ThumbnailImage(
        val type: String? = null,
        @SerialName("image_value")
        val imageValue: ImageValue? = null,
      )

      @Serializable
      data class ThumbnailImageColor(
        val type: String? = null,
        @SerialName("image_color_value")
        val imageColorValue: ImageColorValue? = null,
      ) {

        @Serializable
        data class ImageColorValue(
          val palette: List<Palette>? = null,
        ) {

          @Serializable
          data class Palette(
            val percentage: Float? = null,
            val rgb: Rgb? = null,
          )

          @Serializable
          data class Rgb(
            val red: Float? = null,
            val green: Float? = null,
            val blue: Float? = null,
          )
        }
      }

      @Serializable
      data class ImageValue(
        val url: String? = null,
        val width: Int? = null,
        val height: Int? = null,
      )
    }

    @Serializable
    data class CardPlatform(
      val platform: Platform? = null,
    ) {
      @Serializable
      data class Platform(
        val device: Device? = null,
        val audience: Audience? = null,
      ) {
        @Serializable
        data class Device(
          val name: String? = null,
          val version: String? = null,
        )

        @Serializable
        data class Audience(
          val name: String? = null,
        )
      }
    }
  }
}

@Serializable
data class TweetEntities(
  // val hashtags: JsonArray? = null,
  // val symbols: JsonArray? = null,

  @SerialName("user_mentions")
  val userMentions: List<UserMention>? = null,

  val urls: List<URL>? = null,
  val media: List<PurpleMedia>? = null
)

@Serializable
data class OriginalInfo(
  val width: Long? = null,
  val height: Long? = null,

  @SerialName("focus_rects")
  val focusRects: List<FocusRect>? = null
)

@Serializable
data class FocusRect(
  val x: Long? = null,
  val y: Long? = null,
  val h: Long? = null,
  val w: Long? = null
)

@Serializable
data class Sizes(
  val thumb: Large? = null,
  val large: Large? = null,
  val medium: Large? = null,
  val small: Large? = null
)

@Serializable
data class Large(
  val w: Long? = null,
  val h: Long? = null,
  val resize: String? = null
)

@Serializable
data class UserMention(
  @SerialName("screen_name")
  val screenName: String? = null,

  val name: String? = null,
  val id: Double? = null,

  @SerialName("id_str")
  val idStr: String? = null,

  val indices: List<Long>? = null
)

@Serializable
data class ExtendedEntities(
  val media: List<PurpleMedia>? = null
)

@Serializable
data class AdditionalMediaInfo(
  val monetizable: Boolean? = null
)

@Serializable
data class EXT(
  val mediaStats: EXTMediaStats? = null
)

@Serializable
data class EXTMediaStats(
  // val r: RUnion? = null,
  val ttl: Long? = null
)

// @Serializable
// sealed class RUnion {
//     class RrValue(val value: RR) : RUnion()
//     class StringValue(val value: String) : RUnion()
// }

// @Serializable
// data class RR(
//     val ok: Ok? = null
// )
//
// @Serializable
// data class Ok(
//     val viewCount: String? = null
// )

@Serializable
data class EXTMediaAvailability(
  val status: String? = null
)

@Serializable
data class MediaColor(
  val palette: List<Palette>? = null
)

@Serializable
data class Palette(
  val rgb: RGB? = null,
  val percentage: Double? = null
)

@Serializable
data class RGB(
  val red: Long? = null,
  val green: Long? = null,
  val blue: Long? = null
)

@Serializable
data class VideoInfo(
  @SerialName("aspect_ratio")
  val aspectRatio: List<Long>? = null,

  @SerialName("duration_millis")
  val durationMillis: Long? = null,

  val variants: List<Variant>? = null
)

@Serializable
data class Variant(
  val bitrate: Long? = null,

  @SerialName("content_type")
  val contentType: String? = null,

  val url: String? = null
)

@Serializable
data class QuotedStatusPermalink(
  val url: String? = null,
  val expanded: String? = null,
  val display: String? = null
)

@Serializable
data class SelfThread(
  val id: Double? = null,

  @SerialName("id_str")
  val idStr: String? = null
)

@Serializable
data class ProfileExtensions(
  val mediaStats: ProfileBannerExtensionsMediaStats? = null
)

@Serializable
data class ProfileBannerExtensionsMediaStats(
  val ttl: Long? = null
)

@Serializable
data class Timeline(
  val id: String? = null,
  val instructions: List<Instruction>? = null,
  // val responseObjects: ResponseObjects? = null
)

@Serializable
data class Instruction(
  // val clearCache: Broadcasts? = null,
  val addEntries: AddEntries? = null,
  val terminateTimeline: TerminateTimeline? = null
)

@Serializable
data class AddEntries(
  val entries: List<Entry>? = null
)

@Serializable
data class Entry(
  @SerialName("entryId")
  val entryID: String? = null,

  val sortIndex: String? = null,
  val content: EntryContent? = null
)

@Serializable
data class EntryContent(
  val item: Item? = null,
  val operation: Operation? = null
)

@Serializable
data class Item(
  val content: ItemContent? = null,
  val clientEventInfo: ClientEventInfo? = null
)

@Serializable
data class ItemContent(
  val tweet: ContentTweet? = null,
  val conversationThread: ConversationThread? = null
)

@Serializable
data class ContentTweet(
  val id: String? = null,
  val displayType: String? = null,
  val hasModeratedReplies: Boolean? = null
)

@Serializable
data class Operation(
  val value: String? = null,
  val cursorType: String? = null,
  val cursor: Cursor? = null
)

@Serializable
data class Cursor(
  val value: String? = null,
  val cursorType: String? = null,
  val stopOnEmptyResponse: Boolean? = null
)

// @Serializable
// data class ResponseObjects(
// val feedbackActions: Broadcasts? = null,
// val immediateReactions: Broadcasts? = null
// )
