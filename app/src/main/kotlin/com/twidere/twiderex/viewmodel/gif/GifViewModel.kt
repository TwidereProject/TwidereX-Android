/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
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
package com.twidere.twiderex.viewmodel.gif

import android.content.Context
import android.net.Uri
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.twidere.twiderex.http.TwidereServiceFactory
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.ui.UiGif
import com.twidere.twiderex.repository.GifRepository
import com.twidere.twiderex.utils.FileProviderHelper
import dagger.assisted.AssistedInject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.internal.notify

class GifViewModel @AssistedInject constructor(
    private val gifRepository: GifRepository,
) : ViewModel() {

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(): GifViewModel
    }

    val input = MutableStateFlow("")

    val selectedItem = MutableStateFlow<UiGif?>(null)

    val enable = selectedItem.map { it != null }

    private val service = TwidereServiceFactory.createGifService()

    @OptIn(FlowPreview::class)
    val searchFlow = input.debounce(666L).map {
        it.takeIf { it.isNotEmpty() }?.let { query ->
            gifRepository.gifSearch(
                service = service,
                query = query,
                lang = Locale.current.language
            )
        }
    }

    val trendSource = gifRepository.gifTrending(service = service).cachedIn(viewModelScope)

    private val _commitLoading = MutableStateFlow(false)

    val commitLoading get() = _commitLoading

    fun commit(context: Context, onSuccess: (uri: Uri) -> Unit, platform: PlatformType) {
        selectedItem.value?.let {
            // mastodon support image/video only
            val url = when (platform) {
                PlatformType.Mastodon -> it.mp4
                else -> it.url
            }
            val suffix = when (platform) {
                PlatformType.Mastodon -> "mp4"
                else -> it.type
            }
            viewModelScope.launch {
                _commitLoading.value = true
                try {
                    val target = FileProviderHelper.getUriFromMedias("${it.id}.$suffix", context)
                    gifRepository.download(target = target.toString(), source = url, service = service)
                    onSuccess(target)
                } catch (e: Throwable) {
                    e.notify()
                } finally {
                    _commitLoading.value = false
                }
            }
        }
    }
}
