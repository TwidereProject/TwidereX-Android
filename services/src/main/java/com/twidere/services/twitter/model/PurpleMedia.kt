package com.twidere.services.twitter.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PurpleMedia (
    val id: Double? = null,

    @SerialName("id_str")
    val idStr: String? = null,

    val indices: List<Long>? = null,

    @SerialName("media_url")
    val mediaURL: String? = null,

    @SerialName("media_url_https")
    val mediaURLHTTPS: String? = null,

    val url: String? = null,

    @SerialName("display_url")
    val displayURL: String? = null,

    @SerialName("expanded_url")
    val expandedURL: String? = null,

    val type: String? = null,
    val sizes: Sizes? = null,

    @SerialName("source_status_id")
    val sourceStatusID: Double? = null,

    @SerialName("source_status_id_str")
    val sourceStatusIDStr: String? = null,

    @SerialName("source_user_id")
    val sourceUserID: Long? = null,

    @SerialName("source_user_id_str")
    val sourceUserIDStr: String? = null
)