package com.twidere.services.twitter.model

enum class TweetFields(val value: String) {
    attachments("attachments"),
    author_id("author_id"),
    context_annotations("context_annotations"),
    conversation_id("conversation_id"),
    created_at("created_at"),
    entities("entities"),
    geo("geo"),
    id("id"),
    in_reply_to_user_id("in_reply_to_user_id"),
    lang("lang"),
    non_public_metrics("non_public_metrics"),
    public_metrics("public_metrics"),
    organic_metrics("organic_metrics"),
    promoted_metrics("promoted_metrics"),
    possibly_sensitive("possibly_sensitive"),
    referenced_tweets("referenced_tweets"),
    source("source"),
    text("text"),
    withheld("withheld"),
}