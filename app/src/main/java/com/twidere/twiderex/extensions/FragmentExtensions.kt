/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.extensions

import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.ambientOf
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.twidere.twiderex.fragment.JetFragment
import com.twidere.twiderex.settings.AmbientPrimaryColor
import com.twidere.twiderex.settings.AmbientTabPosition
import com.twidere.twiderex.settings.AmbientTheme

val AmbientWindow = ambientOf<Window> { error("No Window") }
val AmbientNavController = ambientOf<NavController> { error("No NavController") }
fun JetFragment.compose(content: @Composable () -> Unit): ComposeView {
    return ComposeView(requireContext()).apply {
        setContent {
            Providers(
                AmbientPrimaryColor provides primaryColor,
                AmbientTabPosition provides tabPosition,
                AmbientTheme provides theme,
                AmbientNavController provides findNavController(),
                AmbientWindow provides activity?.window!!,
            ) {
                content.invoke()
            }
        }
    }
}
