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
package com.twidere.services.twitter.api

import com.twidere.services.twitter.model.TwitterUploadResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UploadResources {
    @POST("/1.1/media/upload.json?command=INIT")
    @FormUrlEncoded
    suspend fun initUpload(
        @Field("media_type") media_type: String,
        @Field("total_bytes") total_bytes: Long,
    ): TwitterUploadResponse

    @POST("/1.1/media/upload.json?command=APPEND")
    @FormUrlEncoded
    suspend fun appendUpload(
        @Field("media_id") media_id: String,
        @Field("segment_index") segment_index: Long,
        @Field("media_data") media_data: String,
    ): Response<Unit>

    @POST("/1.1/media/upload.json?command=FINALIZE")
    @FormUrlEncoded
    suspend fun finalizeUpload(
        @Field("media_id") media_id: String,
    ): TwitterUploadResponse
}
