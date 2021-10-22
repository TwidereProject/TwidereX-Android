/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
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
package com.twidere.twiderex

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.foundation.NetworkImage
import com.twidere.twiderex.compose.LocalResLoader
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.kmp.LocalRemoteNavigator
import java.io.File

@Composable
fun App() {
    CompositionLocalProvider(
        LocalResLoader provides get(),
        LocalRemoteNavigator provides get(),
    ) {
        MaterialTheme {
            Scaffold {
                // Text("Twidere X!")
                ImageTest()
            }
        }
    }
}

@Composable
private fun ImageTest() {
    Column {
        NetworkImage(data = File("/Users/mimao/Library/Application Support/TwidereX/mediaCaches/a41ba1abc4cb48ad4d32d4cea572774"), modifier = Modifier.width(300.dp).height(300.dp))
        NetworkImage(data = "/Users/mimao/Library/Application Support/TwidereX/mediaCaches/0d773a484fb911b6da361e2bff0c261e", modifier = Modifier.width(300.dp).height(300.dp))
        NetworkImage(data = "https://i.pinimg.com/750x/a1/0b/46/a10b460d9b4f6030d67cf98ec1580d82.jpg", modifier = Modifier.width(300.dp).height(300.dp))
        NetworkImage(data = "https://cdn.hswstatic.com/gif/10-breathtaking-views-1-orig.jpg", modifier = Modifier.width(300.dp).height(300.dp))
    }
}
