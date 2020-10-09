package com.twidere.services.microblog.model

interface ISearchResponse {
    val nextPage: String?
    val status: List<IStatus>
}