package com.twidere.services.twitter

import com.twidere.services.http.authorization.OAuth1Authorization
import com.twidere.services.http.retrofit
import com.twidere.services.microblog.*
import com.twidere.services.microblog.model.IRelationship
import com.twidere.services.microblog.model.IUser
import com.twidere.services.microblog.model.MicroBlogError
import com.twidere.services.microblog.model.Relationship
import com.twidere.services.twitter.api.TwitterResources
import com.twidere.services.twitter.model.UserV2
import com.twidere.services.twitter.model.fields.*

internal const val TWITTER_BASE_URL = "https://api.twitter.com/"

class TwitterService(
    private val consumer_key: String,
    private val consumer_secret: String,
    private val access_token: String,
    private val access_token_secret: String,
) : MicroBlogService, TimelineService, LookupService, RelationshipService, SearchService {
    private val resources by lazy {
        retrofit<TwitterResources>(
            TWITTER_BASE_URL,
            OAuth1Authorization(
                consumer_key,
                consumer_secret,
                access_token,
                access_token_secret,
            ),
        )
    }

    override suspend fun homeTimeline(
        count: Int,
        since_id: String?,
        max_id: String?,
    ) = resources.homeTimeline(
        count,
        since_id,
        max_id,
        trim_user = false,
        exclude_replies = false,
        include_entities = true,
    )

    override suspend fun mentionsTimeline(
        count: Int,
        since_id: String?,
        max_id: String?
    ) = resources.mentionsTimeline(
        count,
        since_id,
        max_id,
        trim_user = false,
        exclude_replies = false,
        include_entities = true,
    )

    override suspend fun userTimeline(
        user_id: String,
        count: Int,
        since_id: String?,
        max_id: String?
    ) = resources.userTimeline(
        user_id,
        count,
        since_id,
        max_id,
        trim_user = false,
        exclude_replies = false,
        include_entities = true,
    )

    override suspend fun lookupUserByName(
        name: String
    ): UserV2 {
        val user = resources.userByName(
            name,
            tweetFields = TweetFields.values().joinToString(",") {
                it.value
            },
            userFields = UserFields.values().joinToString(",") {
                it.value
            }
        )
        if (user.data == null) {
            if (user.errors != null && user.errors.any()) {
                throw MicroBlogError(user.errors.first().detail)
            } else {
                // Shouldn't happen?
                throw MicroBlogError()
            }
        }
        user.data.profileBanner = runCatching {
            resources.profileBanners(name)
        }.getOrNull()
        return user.data
    }

    override suspend fun lookupUser(id: String): IUser {
        val user = resources.user(
            id,
            tweetFields = TweetFields.values().joinToString(",") {
                it.value
            },
            userFields = UserFields.values().joinToString(",") {
                it.value
            }
        )
        if (user.data == null) {
            if (user.errors != null && user.errors.any()) {
                throw MicroBlogError(user.errors.first().detail)
            } else {
                // Shouldn't happen?
                throw MicroBlogError()
            }
        }
        user.data.profileBanner = runCatching {
            resources.profileBanners(user.data.username!!)
        }.getOrNull()
        return user.data
    }

    override suspend fun searchTweets(query: String, nextPage: String?) =
        resources.tweets(
            query,
            next_token = nextPage,
            userFields = UserFields.values().joinToString(",") { it.value },
            pollFields = PollFields.values().joinToString(",") { it.name },
            placeFields = PlaceFields.values().joinToString(",") { it.value },
            mediaFields = MediaFields.values()
                .filter { it != MediaFields.organic_metrics && it != MediaFields.non_public_metrics && it != MediaFields.promoted_metrics }
                .joinToString(",") { it.name },
            expansions = Expansions.values().joinToString(",") { it.value },
            tweetFields = listOf(
                TweetFields.attachments,
                TweetFields.author_id,
                TweetFields.conversation_id,
                TweetFields.created_at,
                TweetFields.entities,
                TweetFields.geo,
                TweetFields.id,
                TweetFields.in_reply_to_user_id,
                TweetFields.lang,
                TweetFields.possibly_sensitive,
                TweetFields.public_metrics,
                TweetFields.referenced_tweets,
                TweetFields.source,
                TweetFields.text,
                TweetFields.withheld
            ).joinToString(",") { it.value },
        )

    override suspend fun showRelationship(id: String): IRelationship {
        val response = resources.showFriendships(id)
        return Relationship(
            followedBy = response.relationship?.target?.followedBy ?: false,
            following = response.relationship?.target?.following ?: false,
        )
    }
}
