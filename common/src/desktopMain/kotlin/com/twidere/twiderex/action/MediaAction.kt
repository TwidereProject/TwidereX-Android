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

import com.twidere.twiderex.extensions.launchCatching
import com.twidere.twiderex.jobs.common.DownloadMediaJob
import com.twidere.twiderex.jobs.common.ShareMediaJob
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.net.URI

actual class MediaAction(
    private val downloadMediaJob: DownloadMediaJob,
    private val shareMediaJob: ShareMediaJob,
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    actual fun download(
        source: String,
        target: String,
        accountKey: MicroBlogKey
    ) {
        scope.launchCatching {
            downloadMediaJob.execute(
                target = target,
                source = source,
                accountKey = accountKey,
            )
        }
    }

    actual fun share(source: String, fileName: String, accountKey: MicroBlogKey, extraText: String) {
        scope.launchCatching {
            val f = File(URI(source))
            val target = File.createTempFile(
                f.nameWithoutExtension,
                f.extension.let {
                    if (it.isEmpty()) {
                        null
                    } else {
                        ".$it"
                    }
                },
            ).absolutePath
            downloadMediaJob.execute(
                target = target,
                source = source,
                accountKey = accountKey,
            )
            shareMediaJob.execute(target, extraText)
        }
    }
}
