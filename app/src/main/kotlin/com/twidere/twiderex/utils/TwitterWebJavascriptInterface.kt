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
package com.twidere.twiderex.utils

import android.webkit.JavascriptInterface
import androidx.navigation.NavController
import com.twidere.twiderex.extensions.setResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TwitterWebJavascriptInterface(
    val navController: NavController,
) {
    @JavascriptInterface
    fun tryPinCode(content: String?) {
        if (!content.isNullOrEmpty()) {
            content.toIntOrNull()?.let {
                GlobalScope.launch {
                    withContext(Dispatchers.Main) {
                        navController.setResult("pin_code", content)
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}
