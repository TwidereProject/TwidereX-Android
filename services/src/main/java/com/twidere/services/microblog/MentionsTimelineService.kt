package com.twidere.services.microblog

import com.twidere.services.microblog.model.IStatus

interface MentionsTimelineService {
    suspend fun mentionsTimeline(
        count: Int = 20,
        since_id: String? = null,
        max_id: String? = null,
    ): List<IStatus>
}