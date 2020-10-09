package com.twidere.services.twitter.model.fields

enum class Expansions(val value: String) {
    attachments_poll_ids("attachments.poll_ids"),
    attachments_media_keys("attachments.media_keys"),
    author_id("author_id"),
    geo_place_id("geo.place_id"),
    in_reply_to_user_id("in_reply_to_user_id"),
    referenced_tweets_id("referenced_tweets.id"),
    entities_mentions_username("entities.mentions.username"),
    referenced_tweets_id_author_id("referenced_tweets.id.author_id"),
}