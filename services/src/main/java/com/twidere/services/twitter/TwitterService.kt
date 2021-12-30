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
package com.twidere.services.twitter

import com.twidere.services.http.AuthorizationInterceptor
import com.twidere.services.http.Errors
import com.twidere.services.http.HttpClientFactory
import com.twidere.services.http.MicroBlogNotFoundException
import com.twidere.services.http.authorization.OAuth1Authorization
import com.twidere.services.microblog.DirectMessageService
import com.twidere.services.microblog.DownloadMediaService
import com.twidere.services.microblog.ListsService
import com.twidere.services.microblog.LookupService
import com.twidere.services.microblog.MicroBlogService
import com.twidere.services.microblog.RelationshipService
import com.twidere.services.microblog.SearchService
import com.twidere.services.microblog.StatusService
import com.twidere.services.microblog.TimelineService
import com.twidere.services.microblog.TrendService
import com.twidere.services.microblog.model.IRelationship
import com.twidere.services.microblog.model.ISearchResponse
import com.twidere.services.microblog.model.IStatus
import com.twidere.services.microblog.model.IUser
import com.twidere.services.microblog.model.Relationship
import com.twidere.services.twitter.api.TwitterResources
import com.twidere.services.twitter.api.UploadResources
import com.twidere.services.twitter.model.Attachment
import com.twidere.services.twitter.model.BlockV2Request
import com.twidere.services.twitter.model.DirectMessageEvent
import com.twidere.services.twitter.model.DirectMessageEventObject
import com.twidere.services.twitter.model.MessageCreate
import com.twidere.services.twitter.model.MessageData
import com.twidere.services.twitter.model.MessageTarget
import com.twidere.services.twitter.model.PurpleMedia
import com.twidere.services.twitter.model.StatusV2
import com.twidere.services.twitter.model.TwitterPaging
import com.twidere.services.twitter.model.TwitterSearchResponseV1
import com.twidere.services.twitter.model.TwitterSearchResponseV2
import com.twidere.services.twitter.model.User
import com.twidere.services.twitter.model.UserV2
import com.twidere.services.twitter.model.exceptions.TwitterApiException
import com.twidere.services.twitter.model.exceptions.TwitterApiExceptionV2
import com.twidere.services.twitter.model.fields.Expansions
import com.twidere.services.twitter.model.fields.MediaFields
import com.twidere.services.twitter.model.fields.PlaceFields
import com.twidere.services.twitter.model.fields.PollFields
import com.twidere.services.twitter.model.fields.TweetFields
import com.twidere.services.twitter.model.fields.UserFields
import com.twidere.services.twitter.model.request.TwitterReactionRequestBody
import com.twidere.services.utils.Base64
import com.twidere.services.utils.await
import com.twidere.services.utils.copyToInLength
import okhttp3.Request
import java.io.ByteArrayOutputStream
import java.io.InputStream

internal const val TWITTER_BASE_URL = "https://api.twitter.com/"
internal const val UPLOAD_TWITTER_BASE_URL = "https://upload.twitter.com/"

class TwitterService(
    private val consumer_key: String,
    private val consumer_secret: String,
    private val access_token: String,
    private val access_token_secret: String,
    private val httpClientFactory: HttpClientFactory,
    private val accountId: String = ""
) : MicroBlogService,
    TimelineService,
    LookupService,
    RelationshipService,
    SearchService,
    StatusService,
    DownloadMediaService,
    ListsService,
    TrendService,
    DirectMessageService {
    private val resources: TwitterResources
        get() = httpClientFactory.createResources(
            clazz = TwitterResources::class.java,
            baseUrl = TWITTER_BASE_URL,
            authorization = createOAuth1Authorization(),
            useCache = true,
        )

    private val uploadResources: UploadResources
        get() = httpClientFactory.createResources(
            clazz = UploadResources::class.java,
            baseUrl = UPLOAD_TWITTER_BASE_URL,
            authorization = createOAuth1Authorization(),
            useCache = true
        )

    private fun createOAuth1Authorization() = OAuth1Authorization(
        consumer_key,
        consumer_secret,
        access_token,
        access_token_secret,
    )

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
        max_id: String?,
        exclude_replies: Boolean,
    ) = resources.userTimeline(
        user_id = user_id,
        count = count,
        since_id = since_id,
        max_id = max_id,
        trim_user = false,
        exclude_replies = exclude_replies,
        include_entities = true,
    )

    override suspend fun favorites(
        user_id: String,
        count: Int,
        since_id: String?,
        max_id: String?
    ) =
        resources.favoritesList(
            user_id = user_id,
            count = count,
            since_id = since_id,
            max_id = max_id,
            include_entities = true,
        )

    override suspend fun listTimeline(
        list_id: String,
        count: Int,
        max_id: String?,
        since_id: String?
    ) = resources.listTimeline(
        list_id = list_id,
        count = count,
        max_id = max_id,
        since_id = since_id,
        include_entities = true
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
                throw TwitterApiException(
                    errors = user.errors.map {
                        Errors(
                            code = null,
                            message = null,
                            detail = it.detail,
                            title = it.title,
                            resource_type = it.resourceType,
                            parameter = it.parameter,
                            value = it.value,
                            type = it.type,
                        )
                    }
                )
            } else {
                // Shouldn't happen?
                throw Exception()
            }
        }
        user.data.profileBanner = runCatching {
            resources.profileBanners(name)
        }.getOrNull()
        return user.data
    }

    override suspend fun lookupUsersByName(name: List<String>): List<IUser> {
        return resources.lookupUsersByName(
            names = name.joinToString(","),
            tweetFields = TweetFields.values().joinToString(",") {
                it.value
            },
            userFields = UserFields.values().joinToString(",") {
                it.value
            }
        ).data ?: emptyList()
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
                throw TwitterApiException(
                    errors = user.errors.map {
                        if ("Not Found Error".equals(it.title, true)) throw MicroBlogNotFoundException(it.detail)
                        Errors(
                            code = null,
                            message = null,
                            detail = it.detail,
                            title = it.title,
                            resource_type = it.resourceType,
                            parameter = it.parameter,
                            value = it.value,
                            type = it.type,
                        )
                    }
                )
            } else {
                // Shouldn't happen?
                throw Exception()
            }
        }
        user.data.profileBanner = user.data.username?.let { userName ->
            runCatching {
                resources.profileBanners(userName)
            }.getOrNull()
        }
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
        val data = response.data ?: throw TwitterApiException("Status not found")
        response.includes?.let {
            data.setExtra(it)
        }
        return data
    }

    suspend fun lookupStatuses(id: List<String>): List<StatusV2> {
        val response = resources.lookupTweets(
            id.joinToString(","),
            userFields = UserFields.values().joinToString(",") { it.value },
            pollFields = PollFields.values().joinToString(",") { it.name },
            placeFields = PlaceFields.values().joinToString(",") { it.value },
            mediaFields = MediaFields.values()
                .joinToString(",") { it.name },
            expansions = Expansions.values().joinToString(",") { it.value },
            tweetFields = TweetFields.values().joinToString(",") { it.value },
        )
        response.data?.forEach { status ->
            response.includes?.let {
                status.setExtra(it)
            }
        }
        return response.data ?: emptyList()
    }

    override suspend fun userPinnedStatus(userId: String): List<IStatus> {
        val user = lookupUser(userId)
        return listOfNotNull(user.pinnedTweetID?.let { lookupStatus(it) })
    }

    override suspend fun searchTweets(
        query: String,
        count: Int,
        nextPage: String?,
    ): ISearchResponse {
        return try {
            searchV2("$query -is:retweet", count = count, nextPage = nextPage)
        } catch (e: TwitterApiExceptionV2) {
            searchV1("$query -filter:retweets", count = count, max_id = nextPage)
        }
    }

    override suspend fun searchMedia(query: String, count: Int, nextPage: String?): ISearchResponse {
        return try {
            searchV2("$query has:media -is:retweet", count = count, nextPage = nextPage)
        } catch (e: TwitterApiExceptionV2) {
            searchV1("$query filter:media -filter:retweets", count = count, max_id = nextPage)
        }
    }

    suspend fun searchV2(
        query: String,
        count: Int,
        nextPage: String?,
    ): TwitterSearchResponseV2 {
        val result = resources.search(
            query,
            next_token = nextPage,
            max_results = count,
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

    suspend fun searchV1(
        query: String,
        count: Int,
        since_id: String? = null,
        max_id: String? = null,
    ): TwitterSearchResponseV1 {
        return resources.searchV1(query, count = count, max_id = max_id, since_id = since_id)
    }

    override suspend fun searchUsers(query: String, page: Int?, count: Int, following: Boolean) =
        resources.searchUser(query, page, count)

    override suspend fun showRelationship(target_id: String): IRelationship {
        val response = resources.showFriendships(target_id)
        return Relationship(
            followedBy = response.relationship?.target?.followedBy ?: false,
            following = response.relationship?.target?.following ?: false,
            blocking = response.relationship?.source?.blocking ?: false,
            blockedBy = response.relationship?.source?.blockedBy ?: false
        )
    }

    override suspend fun follow(user_id: String) {
        resources.follow(user_id)
    }

    override suspend fun unfollow(user_id: String) {
        resources.unfollow(user_id)
    }

    override suspend fun like(id: String): IStatus {
        return try {
            resources.likeV2(userId = accountId, body = TwitterReactionRequestBody(tweet_id = id))
                .run {
                    lookupStatus(id)
                }
        } catch (e: TwitterApiExceptionV2) {
            resources.like(id)
        }
    }

    override suspend fun unlike(id: String): IStatus {
        return try {
            resources.unlikeV2(userId = accountId, tweetId = id)
                .run {
                    lookupStatus(id)
                }
        } catch (e: TwitterApiExceptionV2) {
            resources.unlike(id)
        }
    }

    override suspend fun retweet(id: String): IStatus {
        return try {
            resources.retweetV2(userId = accountId, body = TwitterReactionRequestBody(tweet_id = id))
                .run {
                    lookupStatus(id)
                }
        } catch (e: TwitterApiExceptionV2) {
            resources.retweet(id)
        }
    }

    override suspend fun unRetweet(id: String): IStatus {
        return try {
            resources.unRetweetV2(userId = accountId, tweetId = id)
                .run {
                    lookupStatus(id)
                }
        } catch (e: TwitterApiExceptionV2) {
            resources.unretweet(id)
        }
    }

    override suspend fun delete(id: String) = resources.destroy(id)

    private val BULK_SIZE: Long = 512 * 1024L // 512 Kib

    suspend fun update(
        status: String,
        in_reply_to_status_id: String? = null,
        repost_status_id: String? = null,
        display_coordinates: Boolean? = null,
        lat: Double? = null,
        long: Double? = null,
        media_ids: List<String>? = null,
        attachment_url: String? = null,
        possibly_sensitive: Boolean? = null,
        exclude_reply_user_ids: List<String>? = null
    ) = resources.update(
        status = status,
        in_reply_to_status_id = in_reply_to_status_id,
        auto_populate_reply_metadata = in_reply_to_status_id?.let {
            true
        },
        exclude_reply_user_ids = exclude_reply_user_ids?.joinToString(","),
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

    override suspend fun followers(user_id: String, nextPage: String?) = resources.followers(
        user_id,
        pagination_token = nextPage,
        userFields = UserFields.values().joinToString(",") { it.value },
        expansions = UserFields.pinned_tweet_id.name,
        tweetFields = TweetFields.values().joinToString(",") { it.value },
    ).let {
        TwitterPaging(it.data ?: emptyList(), it.meta?.nextToken)
    }

    override suspend fun following(user_id: String, nextPage: String?) = resources.following(
        user_id,
        pagination_token = nextPage,
        userFields = UserFields.values().joinToString(",") { it.value },
        expansions = UserFields.pinned_tweet_id.name,
        tweetFields = TweetFields.values().joinToString(",") { it.value },
    ).let {
        TwitterPaging(it.data ?: emptyList(), it.meta?.nextToken)
    }

    override suspend fun block(
        id: String
    ) = resources.block(
        sourceId = accountId,
        target = BlockV2Request(targetUserId = id)
    ).run {
        showRelationship(target_id = id)
    }

    override suspend fun unblock(
        id: String
    ) = resources.unblock(sourceId = accountId, targetId = id)
        .run {
            showRelationship(target_id = id)
        }

    suspend fun verifyCredentials(): User? {
        return resources.verifyCredentials()
    }

    override suspend fun download(target: String): InputStream {
        return httpClientFactory.createHttpClientBuilder()
            .addInterceptor(AuthorizationInterceptor(createOAuth1Authorization()))
            .build()
            .newCall(
                Request
                    .Builder()
                    .url(target)
                    .get()
                    .build()
            )
            .await()
            .body
            ?.byteStream() ?: throw IllegalArgumentException()
    }

    override suspend fun lists(
        userId: String?,
        screenName: String?,
        reverse: Boolean
    ) = resources.lists(
        user_id = userId,
        screen_name = screenName,
        reverse = reverse
    )

    override suspend fun createList(
        name: String,
        mode: String?,
        description: String?,
        repliesPolicy: String?
    ) = resources.createList(
        name = name,
        mode = mode,
        description = description
    )

    override suspend fun updateList(
        listId: String,
        name: String?,
        mode: String?,
        description: String?,
        repliesPolicy: String?
    ) = resources.updateList(
        list_id = listId,
        name = name,
        mode = mode,
        description = description
    )

    override suspend fun destroyList(
        listId: String
    ) {
        resources.destroyList(listId)
    }

    override suspend fun listMembers(
        listId: String,
        count: Int,
        cursor: String?
    ) = resources.listMembers(
        list_id = listId,
        count = count,
        cursor = cursor
    ).let {
        TwitterPaging(
            data = it.users ?: emptyList(),
            nextPage = if (0 < it.nextCursor ?: 0) it.nextCursorStr else null
        )
    }

    override suspend fun addMember(
        listId: String,
        userId: String,
        screenName: String
    ) {
        resources.addMember(listId, userId, screenName)
    }

    override suspend fun removeMember(
        listId: String,
        userId: String,
        screenName: String
    ) {
        resources.removeMember(listId, userId, screenName)
    }

    override suspend fun listSubscribers(
        listId: String,
        count: Int,
        cursor: String?
    ) = resources.listSubscribers(
        list_id = listId,
        count = count,
        cursor = cursor
    ).let {
        TwitterPaging(
            data = it.users ?: emptyList(),
            nextPage = if (0 < it.nextCursor ?: 0) it.nextCursorStr else null
        )
    }

    override suspend fun unsubscribeList(
        listId: String
    ) = resources.unsubscribeLists(listId)

    override suspend fun subscribeList(
        listId: String
    ) = resources.subscribeLists(listId)

    // worldwide id = 1
    override suspend fun trends(
        locationId: String,
        limit: Int?
    ) = resources.trends(locationId).let {
        it[0]
    }.trends?.let { list ->
        limit?.let {
            list.subList(0, it.coerceIn(0, list.size))
        } ?: list
    } ?: emptyList()

    override suspend fun getDirectMessages(cursor: String?, count: Int?) = resources.getMessages(
        cursor,
        count
    ).let {
        TwitterPaging(
            data = it.events ?: emptyList(),
            nextPage = it.nextCursor
        )
    }

    override suspend fun showDirectMessage(id: String) = resources.showMessage(id).event

    override suspend fun destroyDirectMessage(id: String) {
        resources.destroyMessage(id)
    }

    suspend fun sendDirectMessage(
        type: String = "message_create",
        recipientId: String,
        text: String?,
        attachmentType: String?, // if set, must set to media
        mediaId: String?
    ): DirectMessageEvent? {
        if (attachmentType != "media") throw NotImplementedError("Currently only media support")
        return resources.sendMessage(
            DirectMessageEventObject(
                event = DirectMessageEvent(
                    type = type,
                    messageCreate = MessageCreate(
                        messageData = MessageData(
                            text = text,
                            attachment = mediaId?.let {
                                Attachment(
                                    type = attachmentType,
                                    media = PurpleMedia(id = it.toLong())
                                )
                            }
                        ),
                        target = MessageTarget(recipientId = recipientId)
                    )
                )
            )
        ).event
    }
}
