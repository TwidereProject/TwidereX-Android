package com.twidere.twiderex.launcher

import android.net.Uri
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

class MultipleFilePickerLauncher(
    private val registry: ActivityResultRegistry,
) {
    private val channel = Channel<MutableList<Uri>>()
    private lateinit var multipleFilePicker: ActivityResultLauncher<String>

    fun register(owner: LifecycleOwner) {
        this.multipleFilePicker = registry.register(
            UUID.randomUUID().toString(),
            owner,
            ActivityResultContracts.GetMultipleContents()
        ) {
            GlobalScope.launch {
                channel.send(it)
            }
        }
    }

    suspend fun launch(type: String) =
        withContext(Dispatchers.Default) {
            multipleFilePicker.launch(type)
            channel.receive()
        }
}

