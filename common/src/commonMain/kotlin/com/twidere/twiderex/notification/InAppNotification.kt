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
package com.twidere.twiderex.notification

import androidx.compose.runtime.Composable
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.utils.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

interface NotificationEvent {
    @Composable
    fun getMessage(): String
}

class StringNotificationEvent(
    private val message: String,
) : NotificationEvent {
    @Composable
    override fun getMessage(): String {
        return message
    }
}

class InAppNotification {
    private val _source = MutableStateFlow<Event<NotificationEvent?>?>(null)
    val source
        get() = _source.asSharedFlow()

    fun show(event: NotificationEvent) {
        _source.value = ((Event(event)))
    }

    @Composable
    fun observeAsState(initial: Event<NotificationEvent?>? = null) =
        source.observeAsState(initial = initial)
}
