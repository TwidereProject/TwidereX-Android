/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.services.twitter

import com.twidere.services.http.authorization.OAuth1Authorization
import com.twidere.services.http.retrofit
import com.twidere.services.microblog.LookupService
import com.twidere.services.microblog.MicroBlogService
import com.twidere.services.microblog.RelationshipService
import com.twidere.services.microblog.SearchService
import com.twidere.services.microblog.StatusService
import com.twidere.services.microblog.TimelineService
import com.twidere.services.microblog.model.IRelationship
import com.twidere.services.microblog.model.IStatus
import com.twidere.services.microblog.model.MicroBlogError
import com.twidere.services.microblog.model.Relationship
import com.twidere.services.twitter.api.TwitterResources
import com.twidere.services.twitter.api.UploadResources
import com.twidere.services.twitter.model.StatusV2
import com.twidere.services.twitter.model.TwitterSearchResponseV2
import com.twidere.services.twitter.model.UserV2
import com.twidere.services.twitter.model.fields.Expansions
import com.twidere.services.twitter.model.fields.MediaFields
import com.twidere.services.twitter.model.fields.PlaceFields
import com.twidere.services.twitter.model.fields.PollFields
import com.twidere.services.twitter.model.fields.TweetFields
import com.twidere.services.twitter.model.fields.UserFields
import com.twidere.services.utils.Base64
import com.twidere.services.utils.copyToInLength
import java.io.ByteArrayOutputStream
import java.io.InputStream

internal const val TWITTER_BASE_URL = "https://api.twitter.com/"
internal const val UPLOAD_TWITTER_BASE_URL = "https://upload.twitter.com/"

class TwitterService(
    private val consumer_key: String,
    private val consumer_secret: String,
    private val access_token: String,
    private val access_token_secret: String,
) : MicroBlogService,
    TimelineService,
    LookupService,
    RelationshipService,
    SearchService,
    StatusService {
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
    private val uploadResources by lazy {
        retrofit<UploadResources>(
            UPLOAD_TWITTER_BASE_URL,
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

    override suspend fun favorites(
        user_id: String,
        count: Int,
        since_id: String?,
        max_id: String?
    ) =
        resources.favoritesList(
            user_id,
            count,
            since_id,
            max_id,
            include_entities = true,
        )

    override suspend fun lookupUserByName(
        name: String
    ): UserV2 {
        val user = resources.lookupUserByName(
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

    override suspend fun lookupUser(id: String): UserV2 {
        val user = resources.lookupUser(
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

    override suspend fun lookupStatus(id: String): StatusV2 {
        val response = resources.lookupTweet(
            id,
            userFields = UserFields.values().joinToString(",") { it.value },
            pollFields = PollFields.values().joinToString(",") { it.name },
            placeFields = PlaceFields.values().joinToString(",") { it.value },
            mediaFields = MediaFields.values()
                .joinToString(",") { it.name },
            expansions = Expansions.values().joinToString(",") { it.value },
            tweetFields = TweetFields.values().joinToString(",") { it.value },
        )
        val data = response.data ?: throw MicroBlogError("Status not found")
        response.includes?.let {
            data.setExtra(it)
        }
        return data
    }

    override suspend fun userPinnedStatus(userId: String): IStatus? {
        val user = lookupUser(userId)
        return user.pinnedTweetID?.let { lookupStatus(it) }
    }

    override suspend fun searchTweets(
        query: String,
        count: Int,
        since_id: String?,
        nextPage: String?,
    ): TwitterSearchResponseV2 {
        val result = resources.search(
            query,
            next_token = nextPage,
            max_results = count,
            since_id = since_id,
            userFields = UserFields.values().joinToString(",") { it.value },
            pollFields = PollFields.values().joinToString(",") { it.name },
            placeFields = PlaceFields.values().joinToString(",") { it.value },
            mediaFields = MediaFields.values()
                .joinToString(",") { it.name },
            expansions = Expansions.values().joinToString(",") { it.value },
            tweetFields = TweetFields.values().joinToString(",") { it.value },
        )
        result.data?.forEach { status ->
            result.includes?.let {
                status.setExtra(it)
            }
        }
        return result
    }

    override suspend fun searchUsers(query: String, page: Int?, count: Int) =
        resources.searchUser(query, page, count)

    override suspend fun showRelationship(id: String): IRelationship {
        val response = resources.showFriendships(id)
        return Relationship(
            followedBy = response.relationship?.target?.followedBy ?: false,
            following = response.relationship?.target?.following ?: false,
        )
    }

    override suspend fun like(id: String) = resources.like(id)

    override suspend fun unlike(id: String) = resources.unlike(id)

    override suspend fun retweet(id: String) = resources.retweet(id)

    override suspend fun unRetweet(id: String) = resources.unretweet(id)

    override suspend fun compose(content: String) = update(content)

    private val BULK_SIZE: Long = 512 * 1024 // 512 Kib

    suspend fun update(
        status: String,
        in_reply_to_status_id: String? = null,
        repost_status_id: String? = null,
        display_coordinates: Boolean? = null,
        lat: Long? = null,
        long: Long? = null,
        media_ids: List<String>? = null,
        attachment_url: String? = null,
        possibly_sensitive: Boolean? = null,
    ) = resources.update(
        status = status,
        in_reply_to_status_id = in_reply_to_status_id,
        repost_status_id = repost_status_id,
        display_coordinates = display_coordinates,
        lat = lat,
        long = long,
        media_ids = media_ids?.joinToString(","),
        attachment_url = attachment_url,
        possibly_sensitive = possibly_sensitive,
    )

    suspend fun uploadFile(stream: InputStream, type: String, length: Long): String {
        val response =
            uploadResources.initUpload(type, length)
        val mediaId = response.mediaIDString ?: throw Error()
        var streamReadLength = 0
        var segmentIndex = 0L
        while (streamReadLength < length) {
            val currentBulkSize = BULK_SIZE.coerceAtMost(length - streamReadLength).toInt()
            ByteArrayOutputStream().use { output ->
                stream.copyToInLength(output, currentBulkSize)
                val data = Base64.encodeToString(output.toByteArray(), Base64.DEFAULT)
                uploadResources.appendUpload(mediaId, segmentIndex, data)
            }
            segmentIndex++
            streamReadLength += currentBulkSize
        }

        return uploadResources.finalizeUpload(mediaId).mediaIDString ?: throw Error()
    }
}
