/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import com.twidere.twiderex.utils.Event

interface NotificationEvent
data class StringNotificationEvent(val message: String) : NotificationEvent
data class StringResNotificationEvent(@StringRes val messageId: Int) : NotificationEvent

class InAppNotification : MutableLiveData<Event<NotificationEvent?>>() {
    fun show(message: String) {
        postValue(Event(StringNotificationEvent(message)))
    }

    fun show(@StringRes messageId: Int) {
        postValue(Event(StringResNotificationEvent(messageId = messageId)))
    }
}
