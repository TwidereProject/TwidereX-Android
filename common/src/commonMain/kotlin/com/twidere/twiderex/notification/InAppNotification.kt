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
package com.twidere.twiderex.notification

import androidx.compose.runtime.Composable
import com.twidere.twiderex.compose.LocalResLoader
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.kmp.RemoteNavigator
import com.twidere.twiderex.utils.Event
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

interface NotificationEvent {
    @Composable
    fun getMessage(): String
}
data class EventActionContext(
    val remoteNavigator: RemoteNavigator
)
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

    companion object {
        fun InAppNotification.show(message: String) {
            show(StringNotificationEvent(message = message))
        }
    }
}

open class StringResNotificationEvent(
    val message: StringResource,
) : NotificationEvent {
    @Composable
    override fun getMessage(): String {
        return LocalResLoader.current.getString(message)
    }
}

class StringResWithActionNotificationEvent(
    private vararg val message: StringResource,
    private val separator: String = System.lineSeparator(),
    private val actionStr: StringResource,
    override val action: EventActionContext.() -> Unit,
) : NotificationWithActionEvent {
    @Composable
    override fun getActionMessage(): String {
        return LocalResLoader.current.getString(actionStr)
    }

    @Composable
    override fun getMessage(): String {
        return message.map { LocalResLoader.current.getString(it) }.joinToString(separator)
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
