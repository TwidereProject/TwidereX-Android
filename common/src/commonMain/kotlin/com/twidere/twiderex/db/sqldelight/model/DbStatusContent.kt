package com.twidere.twiderex.db.sqldelight.model

import androidx.compose.runtime.Immutable
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.MastodonNotificationType
import com.twidere.twiderex.model.enums.MediaType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface DbStatusContent {

  @Serializable
  @SerialName("twitter")
  data class Twitter(
    val id: String,
    val text: String,
    val source: String,
    val user: User,
    val createdAt: Long,
    val sensitive: Boolean,
    val quote: Twitter? = null,
    val retweet: Twitter? = null,
    val replyTo: Twitter? = null,
    val card: Card? = null,
    val poll: Poll? = null,
    val geo: Geo? = null,
    val media: List<Media> = emptyList(),
    val urls: List<UrlEntity> = emptyList(),
    val publicMetrics: PublicMetrics = PublicMetrics(),
    val referenceTweetId: ReferenceTweetId = ReferenceTweetId(),
  ) : DbStatusContent {

    @Serializable
    data class Geo(
      val name: String,
      val lat: Long? = null,
      val long: Long? = null,
    )

    @Serializable
    data class Poll(
      val id: String,
      val options: List<PollOption>,
      val expiresAt: Long?, // some instance of mastodon won't expire
      val expired: Boolean,
      val multiple: Boolean,
      val voted: Boolean,
      val votesCount: Long? = null,
      val votersCount: Long? = null,
      val ownVotes: List<Int> = emptyList(),
    )

    @Serializable
    data class PollOption(
      val text: String,
      val count: Long,
    )

    @Serializable
    data class ReferenceTweetId(
      val replyToAccountId: String? = null,
      val replyTo: String? = null,
      val retweet: String? = null,
      val quote: String? = null,
      val conversation: String? = null,
    )

    @Serializable
    data class User(
      val userKey: MicroBlogKey,
      val nickName: String,
      val userName: String,
      val avatar: String,
    )

    @Serializable
    data class Media(
      val previewUrl: String,
      val mediaUrl: String,
      val ratio: Float,
      val type: MediaType
    )

    @Serializable
    data class UrlEntity(
      val url: String,
      val expandedUrl: String,
      val displayUrl: String,
      val title: String? = null,
      val description: String? = null,
      val images: List<String>? = null,
    )

    @Serializable
    data class PublicMetrics(
      val retweetCount: Long = 0,
      val replyCount: Long = 0,
      val likeCount: Long = 0,
      val quoteCount: Long = 0,
    )


    @Serializable
    data class Card(
      val link: String,
      val displayLink: String?,
      val title: String?,
      val description: String?,
      val image: String?,
    )
  }

  @Serializable
  @SerialName("mastodon-notification")
  data class MastodonNotification(
    val id: String,
    val user: Mastodon.User,
    val updated: Boolean,
    val notificationType: MastodonNotificationType,
    val status: Mastodon? = null,
  ): DbStatusContent

  @Serializable
  @SerialName("mastodon")
  data class Mastodon(
    val id: String,
    val spoilerText: String,
    val text: String,
    val source: String,
    val user: User,
    val createdAt: Long,
    val visibility: Visibility,
    val sensitive: Boolean,
    val emoji: List<Emoji> = emptyList(),
    val mentions: List<Mention>? = null,
    val card: Card? = null,
    val retweet: Mastodon? = null,
    val replyTo: Mastodon? = null,
    val poll: Poll? = null,
    val media: List<Media> = emptyList(),
    val urls: List<UrlEntity> = emptyList(),
    val publicMetrics: PublicMetrics = PublicMetrics(),
    val referenceTweetId: ReferenceTweetId = ReferenceTweetId(),
  ) : DbStatusContent {

    @Serializable
    data class Poll(
      val id: String,
      val options: List<PollOption>,
      val expiresAt: Long?, // some instance of mastodon won't expire
      val expired: Boolean,
      val multiple: Boolean,
      val voted: Boolean,
      val votesCount: Long? = null,
      val votersCount: Long? = null,
      val ownVotes: List<Int> = emptyList(),
      val emoji: List<Emoji> = emptyList(),
    )

    @Serializable
    data class PollOption(
      val text: String,
      val count: Long,
    )

    @Serializable
    data class Emoji(
      val shortcode: String? = null,
      val url: String? = null,
      val staticUrl: String? = null,
      val visibleInPicker: Boolean? = null,
    )

    @Serializable
    enum class Visibility {
      Public,
      Unlisted,
      Private,
      Direct;
    }

    @Immutable
    @Serializable
    data class Mention(
      val id: String? = null,
      val username: String? = null,
      val url: String? = null,
      val acct: String? = null
    )

    @Serializable
    data class ReferenceTweetId(
      val replyToAccountId: String? = null,
      val replyTo: String? = null,
      val retweet: String? = null,
    )

    @Serializable
    data class User(
      val userKey: MicroBlogKey,
      val nickName: String,
      val userName: String,
      val avatar: String,
      val acct: String,
    )

    @Serializable
    data class Media(
      val previewUrl: String,
      val mediaUrl: String,
      val ratio: Float,
      val type: MediaType
    )

    @Serializable
    data class UrlEntity(
      val url: String,
      val expandedUrl: String,
      val displayUrl: String,
      val title: String? = null,
      val description: String? = null,
      val images: List<String>? = null,
    )

    @Serializable
    data class PublicMetrics(
      val retweetCount: Long = 0,
      val replyCount: Long = 0,
      val likeCount: Long = 0,
    )

    @Serializable
    data class Card(
      val link: String,
      val displayLink: String?,
      val title: String?,
      val description: String?,
      val image: String?,
    )
  }

  @Serializable
  @SerialName("gap")
  data class Gap(
    val maxId: String,
    val sinceId: String,
    val loading: Boolean,
  ) : DbStatusContent

}
