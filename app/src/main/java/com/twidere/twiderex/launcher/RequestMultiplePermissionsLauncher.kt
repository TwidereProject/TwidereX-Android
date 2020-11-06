package com.twidere.twiderex.launcher

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class RequestMultiplePermissionsLauncher(
    private val registry: ActivityResultRegistry,
) {
    private lateinit var launcher: ActivityResultLauncher<Array<String>>
    private val channel = Channel<Map<String, Boolean>>()

    fun register(owner: LifecycleOwner) {
        this.launcher = registry.register(
            UUID.randomUUID().toString(),
            owner,
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            GlobalScope.launch {
                channel.send(it)
            }
        }
    }

    suspend fun launch(permissions: Array<String>) =
        withContext(Dispatchers.Default) {
            launcher.launch(permissions)
            channel.receive()
        }
}