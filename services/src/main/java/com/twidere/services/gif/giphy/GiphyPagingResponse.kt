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
package com.twidere.services.gif.giphy

import com.twidere.services.gif.model.IGif
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GiphyPagingResponse(
    @SerialName("data")
    val `data`: List<GifObject>? = null,
    @SerialName("meta")
    val meta: Meta? = null,
    @SerialName("pagination")
    val pagination: Pagination? = null
) {

    @Serializable
    data class Meta(
        @SerialName("msg")
        val msg: String? = null,
        @SerialName("response_id")
        val responseId: String? = null,
        @SerialName("status")
        val status: Int? = null
    )

    @Serializable
    data class Pagination(
        @SerialName("count")
        val count: Int? = null,
        @SerialName("offset")
        val offset: Int? = null,
        @SerialName("total_count")
        val totalCount: Int? = null
    )
}

@Serializable
data class GifObject(
    @SerialName("analytics")
    val analytics: Analytics? = null,
    @SerialName("analytics_response_payload")
    val analyticsResponsePayload: String? = null,
    @SerialName("bitly_gif_url")
    val bitlyGifUrl: String? = null,
    @SerialName("bitly_url")
    val bitlyUrl: String? = null,
    @SerialName("content_url")
    val contentUrl: String? = null,
    @SerialName("embed_url")
    val embedUrl: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("images")
    val images: Images? = null,
    @SerialName("import_datetime")
    val importDatetime: String? = null,
    @SerialName("is_sticker")
    val isSticker: Int? = null,
    @SerialName("rating")
    val rating: String? = null,
    @SerialName("slug")
    val slug: String? = null,
    @SerialName("source")
    val source: String? = null,
    @SerialName("source_post_url")
    val sourcePostUrl: String? = null,
    @SerialName("source_tld")
    val sourceTld: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("trending_datetime")
    val trendingDatetime: String? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("url")
    val url: String? = null,
    @SerialName("user")
    val user: User? = null,
    @SerialName("username")
    val username: String? = null
) : IGif {
    @Serializable
    data class Analytics(
        @SerialName("onclick")
        val onclick: Onclick? = null,
        @SerialName("onload")
        val onload: Onload? = null,
        @SerialName("onsent")
        val onsent: Onsent? = null
    ) {
        @Serializable
        data class Onclick(
            @SerialName("url")
            val url: String? = null
        )

        @Serializable
        data class Onload(
            @SerialName("url")
            val url: String? = null
        )

        @Serializable
        data class Onsent(
            @SerialName("url")
            val url: String? = null
        )
    }

    @Serializable
    data class Images(
        @SerialName("downsized")
        val downsized: Downsized? = null,
        @SerialName("downsized_large")
        val downsizedLarge: DownsizedLarge? = null,
        @SerialName("downsized_medium")
        val downsizedMedium: DownsizedMedium? = null,
        @SerialName("downsized_small")
        val downsizedSmall: DownsizedSmall? = null,
        @SerialName("downsized_still")
        val downsizedStill: DownsizedStill? = null,
        @SerialName("fixed_height")
        val fixedHeight: FixedHeight? = null,
        @SerialName("fixed_height_downsampled")
        val fixedHeightDownsampled: FixedHeightDownsampled? = null,
        @SerialName("fixed_height_small")
        val fixedHeightSmall: FixedHeightSmall? = null,
        @SerialName("fixed_height_small_still")
        val fixedHeightSmallStill: FixedHeightSmallStill? = null,
        @SerialName("fixed_height_still")
        val fixedHeightStill: FixedHeightStill? = null,
        @SerialName("fixed_width")
        val fixedWidth: FixedWidth? = null,
        @SerialName("fixed_width_downsampled")
        val fixedWidthDownsampled: FixedWidthDownsampled? = null,
        @SerialName("fixed_width_small")
        val fixedWidthSmall: FixedWidthSmall? = null,
        @SerialName("fixed_width_small_still")
        val fixedWidthSmallStill: FixedWidthSmallStill? = null,
        @SerialName("fixed_width_still")
        val fixedWidthStill: FixedWidthStill? = null,
        @SerialName("hd")
        val hd: Hd? = null,
        @SerialName("looping")
        val looping: Looping? = null,
        @SerialName("original")
        val original: Original? = null,
        @SerialName("original_mp4")
        val originalMp4: OriginalMp4? = null,
        @SerialName("original_still")
        val originalStill: OriginalStill? = null,
        @SerialName("preview")
        val preview: Preview? = null,
        @SerialName("preview_gif")
        val previewGif: PreviewGif? = null,
        @SerialName("preview_webp")
        val previewWebp: PreviewWebp? = null,
        @SerialName("480w_still")
        val wStill: WStill? = null
    ) {
        @Serializable
        data class Downsized(
            @SerialName("height")
            val height: String? = null,
            @SerialName("size")
            val size: String? = null,
            @SerialName("url")
            val url: String? = null,
            @SerialName("width")
            val width: String? = null
        )

        @Serializable
        data class DownsizedLarge(
            @SerialName("height")
            val height: String? = null,
            @SerialName("size")
            val size: String? = null,
            @SerialName("url")
            val url: String? = null,
            @SerialName("width")
            val width: String? = null
        )

        @Serializable
        data class DownsizedMedium(
            @SerialName("height")
            val height: String? = null,
            @SerialName("size")
            val size: String? = null,
            @SerialName("url")
            val url: String? = null,
            @SerialName("width")
            val width: String? = null
        )

        @Serializable
        data class DownsizedSmall(
            @SerialName("height")
            val height: String? = null,
            @SerialName("mp4")
            val mp4: String? = null,
            @SerialName("mp4_size")
            val mp4Size: String? = null,
            @SerialName("width")
            val width: String? = null
        )

        @Serializable
        data class DownsizedStill(
            @SerialName("height")
            val height: String? = null,
            @SerialName("size")
            val size: String? = null,
            @SerialName("url")
            val url: String? = null,
            @SerialName("width")
            val width: String? = null
        )

        @Serializable
        data class FixedHeight(
            @SerialName("height")
            val height: String? = null,
            @SerialName("mp4")
            val mp4: String? = null,
            @SerialName("mp4_size")
            val mp4Size: String? = null,
            @SerialName("size")
            val size: String? = null,
            @SerialName("url")
            val url: String? = null,
            @SerialName("webp")
            val webp: String? = null,
            @SerialName("webp_size")
            val webpSize: String? = null,
            @SerialName("width")
            val width: String? = null
        )

        @Serializable
        data class FixedHeightDownsampled(
            @SerialName("height")
            val height: String? = null,
            @SerialName("size")
            val size: String? = null,
            @SerialName("url")
            val url: String? = null,
            @SerialName("webp")
            val webp: String? = null,
            @SerialName("webp_size")
            val webpSize: String? = null,
            @SerialName("width")
            val width: String? = null
        )

        @Serializable
        data class FixedHeightSmall(
            @SerialName("height")
            val height: String? = null,
            @SerialName("mp4")
            val mp4: String? = null,
            @SerialName("mp4_size")
            val mp4Size: String? = null,
            @SerialName("size")
            val size: String? = null,
            @SerialName("url")
            val url: String? = null,
            @SerialName("webp")
            val webp: String? = null,
            @SerialName("webp_size")
            val webpSize: String? = null,
            @SerialName("width")
            val width: String? = null
        )

        @Serializable
        data class FixedHeightSmallStill(
            @SerialName("height")
            val height: String? = null,
            @SerialName("size")
            val size: String? = null,
            @SerialName("url")
            val url: String? = null,
            @SerialName("width")
            val width: String? = null
        )

        @Serializable
        data class FixedHeightStill(
            @SerialName("height")
            val height: String? = null,
            @SerialName("size")
            val size: String? = null,
            @SerialName("url")
            val url: String? = null,
            @SerialName("width")
            val width: String? = null
        )

        @Serializable
        data class FixedWidth(
            @SerialName("height")
            val height: String? = null,
            @SerialName("mp4")
            val mp4: String? = null,
            @SerialName("mp4_size")
            val mp4Size: String? = null,
            @SerialName("size")
            val size: String? = null,
            @SerialName("url")
            val url: String? = null,
            @SerialName("webp")
            val webp: String? = null,
            @SerialName("webp_size")
            val webpSize: String? = null,
            @SerialName("width")
            val width: String? = null
        )

        @Serializable
        data class FixedWidthDownsampled(
            @SerialName("height")
            val height: String? = null,
            @SerialName("size")
            val size: String? = null,
            @SerialName("url")
            val url: String? = null,
            @SerialName("webp")
            val webp: String? = null,
            @SerialName("webp_size")
            val webpSize: String? = null,
            @SerialName("width")
            val width: String? = null
        )

        @Serializable
        data class FixedWidthSmall(
            @SerialName("height")
            val height: String? = null,
            @SerialName("mp4")
            val mp4: String? = null,
            @SerialName("mp4_size")
            val mp4Size: String? = null,
            @SerialName("size")
            val size: String? = null,
            @SerialName("url")
            val url: String? = null,
            @SerialName("webp")
            val webp: String? = null,
            @SerialName("webp_size")
            val webpSize: String? = null,
            @SerialName("width")
            val width: String? = null
        )

        @Serializable
        data class FixedWidthSmallStill(
            @SerialName("height")
            val height: String? = null,
            @SerialName("size")
            val size: String? = null,
            @SerialName("url")
            val url: String? = null,
            @SerialName("width")
            val width: String? = null
        )

        @Serializable
        data class FixedWidthStill(
            @SerialName("height")
            val height: String? = null,
            @SerialName("size")
            val size: String? = null,
            @SerialName("url")
            val url: String? = null,
            @SerialName("width")
            val width: String? = null
        )

        @Serializable
        data class Hd(
            @SerialName("height")
            val height: String? = null,
            @SerialName("mp4")
            val mp4: String? = null,
            @SerialName("mp4_size")
            val mp4Size: String? = null,
            @SerialName("width")
            val width: String? = null
        )

        @Serializable
        data class Looping(
            @SerialName("mp4")
            val mp4: String? = null,
            @SerialName("mp4_size")
            val mp4Size: String? = null
        )

        @Serializable
        data class Original(
            @SerialName("frames")
            val frames: String? = null,
            @SerialName("hash")
            val hash: String? = null,
            @SerialName("height")
            val height: String? = null,
            @SerialName("mp4")
            val mp4: String? = null,
            @SerialName("mp4_size")
            val mp4Size: String? = null,
            @SerialName("size")
            val size: String? = null,
            @SerialName("url")
            val url: String? = null,
            @SerialName("webp")
            val webp: String? = null,
            @SerialName("webp_size")
            val webpSize: String? = null,
            @SerialName("width")
            val width: String? = null
        )

        @Serializable
        data class OriginalMp4(
            @SerialName("height")
            val height: String? = null,
            @SerialName("mp4")
            val mp4: String? = null,
            @SerialName("mp4_size")
            val mp4Size: String? = null,
            @SerialName("width")
            val width: String? = null
        )

        @Serializable
        data class OriginalStill(
            @SerialName("height")
            val height: String? = null,
            @SerialName("size")
            val size: String? = null,
            @SerialName("url")
            val url: String? = null,
            @SerialName("width")
            val width: String? = null
        )

        @Serializable
        data class Preview(
            @SerialName("height")
            val height: String? = null,
            @SerialName("mp4")
            val mp4: String? = null,
            @SerialName("mp4_size")
            val mp4Size: String? = null,
            @SerialName("width")
            val width: String? = null
        )

        @Serializable
        data class PreviewGif(
            @SerialName("height")
            val height: String? = null,
            @SerialName("size")
            val size: String? = null,
            @SerialName("url")
            val url: String? = null,
            @SerialName("width")
            val width: String? = null
        )

        @Serializable
        data class PreviewWebp(
            @SerialName("height")
            val height: String? = null,
            @SerialName("size")
            val size: String? = null,
            @SerialName("url")
            val url: String? = null,
            @SerialName("width")
            val width: String? = null
        )

        @Serializable
        data class WStill(
            @SerialName("height")
            val height: String? = null,
            @SerialName("size")
            val size: String? = null,
            @SerialName("url")
            val url: String? = null,
            @SerialName("width")
            val width: String? = null
        )
    }

    @Serializable
    data class User(
        @SerialName("avatar_url")
        val avatarUrl: String? = null,
        @SerialName("banner_image")
        val bannerImage: String? = null,
        @SerialName("banner_url")
        val bannerUrl: String? = null,
        @SerialName("description")
        val description: String? = null,
        @SerialName("display_name")
        val displayName: String? = null,
        @SerialName("instagram_url")
        val instagramUrl: String? = null,
        @SerialName("is_verified")
        val isVerified: Boolean? = null,
        @SerialName("profile_url")
        val profileUrl: String? = null,
        @SerialName("username")
        val username: String? = null,
        @SerialName("website_url")
        val websiteUrl: String? = null
    )
}
