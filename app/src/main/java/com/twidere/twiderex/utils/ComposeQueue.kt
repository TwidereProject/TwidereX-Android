package com.twidere.twiderex.utils

import com.twidere.services.microblog.StatusService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

object ComposeQueue {
    fun commit(
        service: StatusService,
        content: String,
        vararg images: File,
    ) {
        GlobalScope.launch {
            service.compose(content)
//            val picIds = images.mapNotNull { Api.uploadPic(it).picId }

        }
    }
}
