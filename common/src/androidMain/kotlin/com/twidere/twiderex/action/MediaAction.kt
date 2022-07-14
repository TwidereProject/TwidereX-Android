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
package com.twidere.twiderex.action

import android.content.Context
import androidx.work.WorkManager
import com.twidere.twiderex.extensions.toUri
import com.twidere.twiderex.kmp.StorageProvider
import com.twidere.twiderex.kmp.cacheFiles
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.worker.DownloadMediaWorker
import com.twidere.twiderex.worker.ShareMediaWorker

actual class MediaAction(
    private val workManager: WorkManager,
    private val context: Context,
    private val storageProvider: StorageProvider
) {
    actual fun download(
        source: String,
        target: String,
        accountKey: MicroBlogKey
    ) {
        workManager.enqueue(
            DownloadMediaWorker.create(
                accountKey = accountKey,
                source = source,
                target = target
            )
        )
    }

    actual fun share(source: String, fileName: String, accountKey: MicroBlogKey, extraText: String) {
        val uri = storageProvider.cacheFiles.mediaFile(fileName).toUri(context)
        DownloadMediaWorker.create(
            accountKey = accountKey,
            source = source,
            target = uri.toString()
        ).let {
            workManager.beginWith(it)
                .then(
                    ShareMediaWorker.create(
                        target = uri,
                        extraText = extraText,
                    )
                ).enqueue()
        }
    }
}
