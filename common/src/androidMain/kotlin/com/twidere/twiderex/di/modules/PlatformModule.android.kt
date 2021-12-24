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

import android.accounts.AccountManager
import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkManager
import com.twidere.twiderex.http.TwidereHttpConfigProvider
import com.twidere.twiderex.kmp.ResLoader
import com.twidere.twiderex.model.AccountPreferencesFactory
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.preferences.PreferencesHolder
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.utils.PlatformResolver
import com.twidere.twiderex.worker.DownloadMediaWorker
import com.twidere.twiderex.worker.NotificationWorker
import com.twidere.twiderex.worker.ShareMediaWorker
import com.twidere.twiderex.worker.compose.MastodonComposeWorker
import com.twidere.twiderex.worker.compose.TwitterComposeWorker
import com.twidere.twiderex.worker.database.DeleteDbStatusWorker
import com.twidere.twiderex.worker.dm.DirectMessageDeleteWorker
import com.twidere.twiderex.worker.dm.DirectMessageFetchWorker
import com.twidere.twiderex.worker.dm.TwitterDirectMessageSendWorker
import com.twidere.twiderex.worker.draft.RemoveDraftWorker
import com.twidere.twiderex.worker.draft.SaveDraftWorker
import com.twidere.twiderex.worker.status.DeleteStatusWorker
import com.twidere.twiderex.worker.status.LikeWorker
import com.twidere.twiderex.worker.status.MastodonVoteWorker
import com.twidere.twiderex.worker.status.RetweetWorker
import com.twidere.twiderex.worker.status.UnLikeWorker
import com.twidere.twiderex.worker.status.UnRetweetWorker
import com.twidere.twiderex.worker.status.UpdateStatusWorker
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val platformModule = module {
    single {
        ResLoader(get())
    }
    single { AccountRepository(get(), get()) }
    single { AccountPreferencesFactory(get()) }
    single<AccountManager> { AccountManager.get(get()) }
    single { get<Context>().getSystemService(Context.LOCATION_SERVICE) as LocationManager }
    single { get<Context>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }
    single { get<Context>().contentResolver }
    single { NotificationManagerCompat.from(get()) }
    single { WorkManager.getInstance(get()) }
    single { TwidereHttpConfigProvider(get<PreferencesHolder>().miscPreferences) }
    single { InAppNotification() }
    single { PlatformResolver(get()) }
    workManager()
}

private fun Module.workManager() {
    worker { ShareMediaWorker(get(), get(), get()) }
    worker { NotificationWorker(get(), get(), get<PreferencesHolder>().notificationPreferences, get()) }
    worker { DownloadMediaWorker(get(), get(), get()) }
    worker { DeleteStatusWorker(get(), get(), get()) }
    worker { LikeWorker(get(), get(), get()) }
    worker { MastodonVoteWorker(get(), get(), get()) }
    worker { RetweetWorker(get(), get(), get()) }
    worker { UnLikeWorker(get(), get(), get()) }
    worker { UnRetweetWorker(get(), get(), get()) }
    worker { UpdateStatusWorker(get(), get(), get()) }
    worker { RemoveDraftWorker(get(), get(), get()) }
    worker { SaveDraftWorker(get(), get(), get()) }
    worker { DirectMessageDeleteWorker(get(), get(), get()) }
    worker { DirectMessageFetchWorker(get(), get(), get()) }
    worker { TwitterDirectMessageSendWorker(get(), get(), get()) }
    worker { DeleteDbStatusWorker(get(), get(), get()) }
    worker { MastodonComposeWorker(get(), get(), get()) }
    worker { TwitterComposeWorker(get(), get(), get()) }
}
