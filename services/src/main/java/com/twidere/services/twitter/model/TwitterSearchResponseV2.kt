package com.twidere.services.twitter.model

import com.twidere.services.microblog.model.ISearchResponse
import com.twidere.services.microblog.model.IStatus
import kotlinx.serialization.Serializable

@Serializable
data class TwitterSearchResponseV2 (
    val data: List<StatusV2>? = null,
    val includes: IncludesV2? = null,
    val meta: Meta? = null
): ISearchResponse {

    override val nextPage: String?
        get() = meta?.nextToken
    override val status: List<IStatus>
        get() = data ?: emptyList()
}