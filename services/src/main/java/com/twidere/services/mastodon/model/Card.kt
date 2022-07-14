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
package com.twidere.services.mastodon.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Card(
    val url: String? = null,
    val title: String? = null,
    val description: String? = null,
    val type: String? = null,

    @SerialName("author_name")
    val authorName: String? = null,

    @SerialName("author_url")
    val authorURL: String? = null,

    @SerialName("provider_name")
    val providerName: String? = null,

    @SerialName("provider_url")
    val providerURL: String? = null,

    val html: String? = null,
    val width: Long? = null,
    val height: Long? = null,
    val image: String? = null,

    @SerialName("embed_url")
    val embedURL: String? = null
)
