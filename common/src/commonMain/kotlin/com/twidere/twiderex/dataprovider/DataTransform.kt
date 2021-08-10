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
package com.twidere.twiderex.dataprovider

import com.twidere.services.microblog.model.IListModel
import com.twidere.services.microblog.model.INotification
import com.twidere.services.microblog.model.IStatus
import com.twidere.services.microblog.model.IUser
import com.twidere.services.twitter.model.DirectMessageEvent
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.paging.PagingTimeLineWithStatus
import com.twidere.twiderex.model.ui.UiDMEvent
import com.twidere.twiderex.model.ui.UiList
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiUser

expect fun IUser.toUi(accountKey: MicroBlogKey): UiUser

expect fun IStatus.toUi(accountKey: MicroBlogKey): UiStatus

expect fun INotification.toUi(accountKey: MicroBlogKey): UiStatus

expect fun IStatus.toPagingTimeline(accountKey: MicroBlogKey, pagingKey: String): PagingTimeLineWithStatus

expect fun IListModel.toUi(accountKey: MicroBlogKey): UiList

expect fun DirectMessageEvent.toUi(accountKey: MicroBlogKey, sender: UiUser): UiDMEvent
