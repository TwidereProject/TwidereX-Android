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

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.twidere.twiderex.component.navigation.INavigator
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.utils.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

interface NotificationEvent {
    @Composable
    fun getMessage(): String
}

interface NotificationWithActionEvent : NotificationEvent {
    @Composable
    fun getActionMessage(): String
    val action: EventActionContext.() -> Unit
}

class StringNotificationEvent(
    private val message: String,
) : NotificationEvent {
    @Composable
    override fun getMessage(): String {
        return message
    }
}

open class StringResNotificationEvent(
    @StringRes val messageId: Int,
) : NotificationEvent {
    @Composable
    override fun getMessage(): String {
        return stringResource(id = messageId)
    }
}

data class EventActionContext(
    val context: Context,
    val navigator: INavigator,
)

class StringResWithActionNotificationEvent(
    private vararg val messageId: Int,
    private val separator: String = System.lineSeparator(),
    @StringRes private val actionId: Int,
    override val action: EventActionContext.() -> Unit,
) : NotificationWithActionEvent {
    @Composable
    override fun getActionMessage(): String {
        return stringResource(id = actionId)
    }

    @Composable
    override fun getMessage(): String {
        return messageId.map { stringResource(id = it) }.joinToString(separator)
    }
}

class InAppNotification {
    private val _source = MutableStateFlow<Event<NotificationEvent?>?>(null)
    private val source
        get() = _source.asSharedFlow()

    fun show(event: NotificationEvent) {
        _source.value = ((Event(event)))
    }

    fun show(message: String) {
        _source.value = Event(StringNotificationEvent(message))
    }

    fun show(@StringRes messageId: Int) {
        _source.value = Event(StringResNotificationEvent(messageId = messageId))
    }

    @Composable
    fun observeAsState(initial: Event<NotificationEvent?>? = null) =
        source.observeAsState(initial = initial)
}
