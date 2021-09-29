package com.twidere.twiderex.utils

import android.net.Uri
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import com.twidere.twiderex.navigation.RootDeepLinksRoute

object CustomTabSignInChannel {
    private var waiting = false
    private val channel: Channel<Uri> = Channel()

    suspend fun send(uri: Uri) {
        if (waiting) {
            channel.send(uri)
        }
        waiting = false
    }

    fun canHandle(uri: Uri): Boolean {
        return uri.toString().startsWith(RootDeepLinksRoute.Callback.SignIn.Mastodon) ||
            uri.toString().startsWith(RootDeepLinksRoute.Callback.SignIn.Twitter)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun waitOne(): Uri {
        waiting = true
        return channel.receive()
    }

    suspend fun onClose() {
        if (waiting) {
            send(Uri.EMPTY)
            waiting = false
        }
    }
}