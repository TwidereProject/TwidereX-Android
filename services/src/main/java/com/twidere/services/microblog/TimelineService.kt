package com.twidere.services.microblog

import com.twidere.services.microblog.model.IStatus

interface TimelineService {
    suspend fun homeTimeline(
        count: Int = 20,
        since_id: String? = null,
        max_id: String? = null,
    ): List<IStatus>

    suspend fun mentionsTimeline(
        count: Int = 20,
        since_id: String? = null,
        max_id: String? = null,
    ): List<IStatus>


    suspend fun userTimeline(
        user_id: String,
        count: Int = 20,
        since_id: String? = null,
        max_id: String? = null,
    ): List<IStatus>
}