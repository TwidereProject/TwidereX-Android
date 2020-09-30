package com.twidere.services.twitter

import com.twidere.services.http.authorization.OAuth1Authorization
import com.twidere.services.http.retrofit
import com.twidere.services.microblog.*
import com.twidere.services.microblog.model.IRelationship
import com.twidere.services.microblog.model.IUser
import com.twidere.services.microblog.model.MicroBlogError
import com.twidere.services.microblog.model.Relationship
import com.twidere.services.twitter.api.TwitterResources
import com.twidere.services.twitter.model.TweetFields
import com.twidere.services.twitter.model.UserFields
import com.twidere.services.twitter.model.UserV2

internal const val TWITTER_BASE_URL = "https://api.twitter.com/"

class TwitterService(
    private val consumer_key: String,
    private val consumer_secret: String,
    private val access_token: String,
    private val access_token_secret: String,
) : MicroBlogService, HomeTimelineService, MentionsTimelineService, LookupService, RelationshipService {
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

    override suspend fun showRelationship(id: String): IRelationship {
        val response = resources.showFriendships(id)
        return Relationship(
            followedBy = response.relationship?.target?.followedBy ?: false,
            following = response.relationship?.target?.following ?: false,
        )
    }
}
