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
package com.twidere.twiderex.di.modules

import com.twidere.twiderex.jobs.common.DownloadMediaJob
import com.twidere.twiderex.jobs.common.NotificationJob
import com.twidere.twiderex.jobs.common.ShareMediaJob
import com.twidere.twiderex.jobs.compose.MastodonComposeJob
import com.twidere.twiderex.jobs.compose.TwitterComposeJob
import com.twidere.twiderex.jobs.database.DeleteDbStatusJob
import com.twidere.twiderex.jobs.dm.DirectMessageDeleteJob
import com.twidere.twiderex.jobs.dm.DirectMessageFetchJob
import com.twidere.twiderex.jobs.dm.TwitterDirectMessageSendJob
import com.twidere.twiderex.jobs.draft.RemoveDraftJob
import com.twidere.twiderex.jobs.draft.SaveDraftJob
import com.twidere.twiderex.jobs.status.DeleteStatusJob
import com.twidere.twiderex.jobs.status.LikeStatusJob
import com.twidere.twiderex.jobs.status.MastodonVoteJob
import com.twidere.twiderex.jobs.status.RetweetStatusJob
import com.twidere.twiderex.jobs.status.UnRetweetStatusJob
import com.twidere.twiderex.jobs.status.UnlikeStatusJob
import com.twidere.twiderex.jobs.status.UpdateStatusJob
import org.koin.core.module.Module
import org.koin.dsl.module

val jobsModule = module {
    common()
    compose()
    database()
    dm()
    draft()
    status()
}

private fun Module.status() {
    single { DeleteStatusJob(get(), get(), get()) }
    single { LikeStatusJob(get(), get(), get()) }
    single { MastodonVoteJob(get(), get(), get()) }
    single { RetweetStatusJob(get(), get(), get()) }
    single { UnlikeStatusJob(get(), get(), get()) }
    single { UnRetweetStatusJob(get(), get(), get()) }
    single { UpdateStatusJob(get(), get()) }
}

private fun Module.draft() {
    single { RemoveDraftJob(get()) }
    single { SaveDraftJob(get(), get()) }
}

private fun Module.dm() {
    single { DirectMessageDeleteJob(get(), get()) }
    single { DirectMessageFetchJob(get(), get(), get(), get()) }
    single { TwitterDirectMessageSendJob(get(), get(), get(), get(), get()) }
}

private fun Module.database() {
    single { DeleteDbStatusJob(get()) }
}

private fun Module.compose() {
    single { MastodonComposeJob(get(), get(), get(), get(), get(), get(), get()) }
    single { TwitterComposeJob(get(), get(), get(), get(), get(), get(), get(), get()) }
}

private fun Module.common() {
    single { DownloadMediaJob(get(), get(), get(), get()) }
    single { NotificationJob(get(), get(), get(), get()) }
    single { ShareMediaJob(get(), get()) }
}
