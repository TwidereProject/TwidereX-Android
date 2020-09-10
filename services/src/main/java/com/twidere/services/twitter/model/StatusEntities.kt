package com.twidere.services.twitter.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray

@Serializable
data class StatusEntities (
    val hashtags: JsonArray? = null,
    val symbols: JsonArray? = null,

    @SerialName("user_mentions")
    val userMentions: List<UserMention>? = null,

    val urls: JsonArray? = null,
    val media: List<PurpleMedia>? = null
)