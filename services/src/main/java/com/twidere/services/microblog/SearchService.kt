package com.twidere.services.microblog

import com.twidere.services.microblog.model.ISearchResponse

interface SearchService {
    suspend fun searchTweets(
        query: String,
        nextPage: String? = null,
    ): ISearchResponse
}