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
package com.twidere.twiderex.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.twidere.services.mastodon.MastodonService
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.ui.UiEmoji
import com.twidere.twiderex.model.ui.UiEmoji.Companion.toUi

object MastodonEmojiCache {
    private val items = hashMapOf<String, LiveData<List<UiEmoji>>>()
    fun get(account: AccountDetails): LiveData<List<UiEmoji>> {
        return items.getOrPut(account.accountKey.host) {
            liveData {
                account.service.let {
                    it as MastodonService
                }.let {
                    try {
                        it.emojis().toUi()
                    } catch (e: Throwable) {
                        emptyList()
                    }
                }.let {
                    emit(it)
                }
            }
        }
    }
}
