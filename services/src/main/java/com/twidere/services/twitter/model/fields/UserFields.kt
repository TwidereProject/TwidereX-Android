package com.twidere.services.twitter.model.fields

enum class UserFields(val value: String) {
    created_at("created_at"),
    description("description"),
    entities("entities"),
    id("id"),
    location("location"),
    tw_name("name"),
    pinned_tweet_id("pinned_tweet_id"),
    profile_image_url("profile_image_url"),
    tw_protected("protected"),
    public_metrics("public_metrics"),
    url("url"),
    username("username"),
    verified("verified"),
    withheld("withheld"),
}