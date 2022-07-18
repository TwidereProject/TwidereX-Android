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
package com.twidere.twiderex.utils

import com.twidere.twiderex.model.ui.UiCard
import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object PreviewResolver {

    private val loadingList: MutableList<String> by lazy {
        mutableListOf()
    }

    private val cache: Cache<String, UiCard> by lazy {
        Cache.Builder().apply {
            maximumCacheSize(300)
        }.build()
    }

    private fun isInLoading(url: String): Boolean {
        return loadingList.contains(url)
    }

    private val scope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.IO)
    }

    fun getCached(url: String?): UiCard? {
        if (url == null) {
            return null
        }
        return cache.get(url)
    }

    private val previewClient: OkHttpClient by lazy {
        OkHttpClient.Builder().apply {
            dispatcher(
                dispatcher = Dispatcher().apply {
                    maxRequestsPerHost = 64
                }
            )
        }.build()
    }

    private fun Document.getMeta(name: String): String? {
        return this.head().getElementsByAttributeValue("property", name)
            .firstOrNull { it.tagName() == "meta" }?.attributes()?.get("content")
    }

    fun parsePreview(card: UiCard?, onSuccess: (UiCard) -> Unit) {

        if (card?.link == null) {
            return
        }

        if (isInLoading(card.link)) {
            return
        }

        getCached(card.link)?.let {
            onSuccess.invoke(it)
            return
        }

        scope.launch {
            loadingList.add(card.link)
            val response = runCatching {
                previewClient.newCall(
                    Request
                        .Builder()
                        .url(
                            card.link.replace(
                                "http:",
                                "https:"
                            )
                        )
                        .build()
                ).execute()
            }.getOrNull()

            response?.body?.string()?.let {
                Jsoup.parse(it)
            }?.let { doc ->
                val title = doc.getMeta("og:title") ?: doc.title()
                val desc = doc.getMeta("og:description")
                val img = doc.getMeta("og:image")
                card.copy(
                    title = title,
                    description = desc,
                    image = img
                )
            }?.also {
                cache.put(card.link, it)
                onSuccess.invoke(it)
            }
            loadingList.remove(card.link)
        }
    }
}
