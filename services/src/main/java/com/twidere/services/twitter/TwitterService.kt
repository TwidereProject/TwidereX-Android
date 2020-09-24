package com.twidere.services.twitter

import com.twidere.services.http.authorization.OAuthAuthorization
import com.twidere.services.http.retrofit
import com.twidere.services.microblog.HomeTimelineService
import com.twidere.services.microblog.MentionsTimelineService
import com.twidere.services.microblog.MicroBlogService
import com.twidere.services.twitter.api.TwitterResources

internal const val TWITTER_BASE_URL = "https://api.twitter.com/"

class TwitterService(
    private val consumer_key: String,
    private val consumer_secret: String,
    private val access_token: String,
    private val access_token_secret: String,
) : MicroBlogService, HomeTimelineService, MentionsTimelineService {
    private val resources by lazy {
        retrofit<TwitterResources>(
            TWITTER_BASE_URL,
            OAuthAuthorization(
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
}
