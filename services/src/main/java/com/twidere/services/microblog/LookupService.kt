package com.twidere.services.microblog

import com.twidere.services.microblog.model.ISearchResponse
import com.twidere.services.microblog.model.IUser

interface LookupService {
    suspend fun lookupUserByName(
        name: String
    ): IUser

    suspend fun lookupUser(
        id: String
    ): IUser

    suspend fun lookupTweets(
        query: String,
        nextPage: String? = null,
    ): ISearchResponse
}