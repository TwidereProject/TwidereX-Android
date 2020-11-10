/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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
