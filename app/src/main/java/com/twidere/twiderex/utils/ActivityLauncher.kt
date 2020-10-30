package com.twidere.twiderex.utils

import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.ambientOf
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ActivityLauncher(private val registry: ActivityResultRegistry) : DefaultLifecycleObserver {
    private lateinit var owner: LifecycleOwner

    override fun onCreate(owner: LifecycleOwner) {
        this.owner = owner
    }

    suspend fun <I, O> launchForResult(contract: ActivityResultContract<I, O>) =
        suspendCoroutine<O> { response ->
            registry.register(
                UUID.randomUUID().toString(),
                owner,
                contract
            ) {
                response.resume(it)
            }
        }
}

val AmbientLauncher = ambientOf<ActivityLauncher>()