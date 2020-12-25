package com.twidere.twiderex.worker.status

import androidx.work.workDataOf
import com.twidere.twiderex.model.MicroBlogKey

data class StatusResult(
    val statusKey: MicroBlogKey,
    val accountKey: MicroBlogKey,
    val retweeted: Boolean? = null,
    val liked: Boolean? = null,
    val retweetCount: Long? = null,
    val likeCount: Long? = null,
) {
    fun toWorkData() = workDataOf(
        "statusKey" to statusKey.toString(),
        "accountKey" to accountKey.toString(),
        "liked" to liked,
        "retweeted" to retweeted,
        "retweetCount" to retweetCount,
        "likeCount" to likeCount,
    )
}