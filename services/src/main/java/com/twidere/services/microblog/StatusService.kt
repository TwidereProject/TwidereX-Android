package com.twidere.services.microblog

interface StatusService {
    suspend fun like(id: String)
    suspend fun unlike(id: String)
    suspend fun retweet(id: String)
    suspend fun unRetweet(id: String)
}