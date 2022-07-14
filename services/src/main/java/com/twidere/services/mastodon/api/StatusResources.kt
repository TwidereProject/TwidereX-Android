/*
 *  Twidere X
 *
 *  Copyright (C) TwidereProject and Contributors
 * 
 *  This file is part of Twidere X.
 * 
 *  Twidere X is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Twidere X is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Twidere X. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.services.mastodon.api

import com.twidere.services.mastodon.model.Poll
import com.twidere.services.mastodon.model.PostStatus
import com.twidere.services.mastodon.model.PostVote
import com.twidere.services.mastodon.model.Status
import com.twidere.services.mastodon.model.UploadResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface StatusResources {
    @POST("/api/v1/statuses/{id}/favourite")
    suspend fun favourite(@Path("id") id: String): Status

    @POST("/api/v1/statuses/{id}/unfavourite")
    suspend fun unfavourite(@Path("id") id: String): Status

    @POST("/api/v1/statuses/{id}/reblog")
    suspend fun reblog(@Path("id") id: String): Status

    @POST("/api/v1/statuses/{id}/unreblog")
    suspend fun unreblog(@Path("id") id: String): Status

    @POST("/api/v1/statuses/{id}/bookmark")
    suspend fun bookmark(@Path("id") id: String): Status

    @POST("/api/v1/statuses/{id}/unbookmark")
    suspend fun unbookmark(@Path("id") id: String): Status

    @POST("/api/v1/statuses/{id}/mute")
    suspend fun mute(@Path("id") id: String): Status

    @POST("/api/v1/statuses/{id}/unmute")
    suspend fun unmute(@Path("id") id: String): Status

    @POST("/api/v1/statuses/{id}/pin")
    suspend fun pin(@Path("id") id: String): Status

    @POST("/api/v1/statuses/{id}/unpin")
    suspend fun unpin(@Path("id") id: String): Status

    @DELETE("/api/v1/statuses/{id}")
    suspend fun delete(@Path("id") id: String): Status

    @POST("/api/v1/statuses")
    suspend fun post(@Body data: PostStatus): Status

    @Multipart
    @POST("/api/v1/media")
    suspend fun upload(@Part file: MultipartBody.Part): UploadResponse

    @POST("/api/v1/polls/{id}/votes")
    suspend fun vote(@Path("id") id: String, @Body data: PostVote): Poll
}
