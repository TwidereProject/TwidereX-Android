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
package com.twidere.twiderex.viewmodel

import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkManager
import com.twidere.twiderex.db.model.DbDraft
import com.twidere.twiderex.repository.DraftRepository
import com.twidere.twiderex.worker.draft.RemoveDraftWorker
import dagger.assisted.AssistedInject
import moe.tlaster.precompose.viewmodel.ViewModel

class DraftViewModel @AssistedInject constructor(
    private val repository: DraftRepository,
    private val workManager: WorkManager,
    private val notificationManagerCompat: NotificationManagerCompat,
) : ViewModel() {

    fun delete(it: DbDraft) {
        workManager.beginWith(RemoveDraftWorker.create(it._id)).enqueue()
        notificationManagerCompat.cancel(it._id.hashCode())
    }

    val source by lazy {
        repository.source
    }
}
