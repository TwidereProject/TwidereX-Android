/*
 *  Twidere X
 *
 *  Copyright (C) TwidereProject and Contributors
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
package com.twidere.twiderex.scenes.twitter

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.twidere.twiderex.component.foundation.SignInScaffold
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.di.ext.getViewModel
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.viewmodel.twitter.PinCodeProvider
import com.twidere.twiderex.viewmodel.twitter.TwitterSignInViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun TwitterSignInScene(
    consumerKey: String,
    consumerSecret: String,
) {
    val navController = LocalNavController.current
    val navigator = LocalNavigator.current
    val pinCodeProvider: PinCodeProvider = { target ->
        navigator.twitterSignInWeb(target)
    }
    val viewModel: TwitterSignInViewModel = getViewModel {
        parametersOf(
            consumerKey,
            consumerSecret,
            pinCodeProvider,
            { success: Boolean ->
                navController.goBackWith(success)
            }
        )
    }
    val loading by viewModel.loading.observeAsState(initial = false)

    SignInScaffold {
        if (loading) {
            CircularProgressIndicator()
        }
    }
}
