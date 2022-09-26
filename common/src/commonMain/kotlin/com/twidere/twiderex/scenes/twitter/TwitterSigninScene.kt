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
import com.twidere.twiderex.component.navigation.twitterSignInWeb
import com.twidere.twiderex.di.ext.getViewModel
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.navigation.Root
import com.twidere.twiderex.navigation.RootDeepLinks
import com.twidere.twiderex.viewmodel.twitter.PinCodeProvider
import com.twidere.twiderex.viewmodel.twitter.TwitterSignInViewModel
import io.github.seiko.precompose.annotation.NavGraphDestination
import io.github.seiko.precompose.annotation.Path
import moe.tlaster.precompose.navigation.Navigator
import org.koin.core.parameter.parametersOf

@NavGraphDestination(
  route = Root.SignIn.Twitter.route,
  deepLink = [RootDeepLinks.SignIn]
)
@Composable
fun TwitterSignInScene(
  @Path("consumerKey") consumerKey: String,
  @Path("consumerSecret") consumerSecret: String,
  navigator: Navigator,
) {
  val pinCodeProvider: PinCodeProvider = { target ->
    navigator.twitterSignInWeb(target)
  }
  val viewModel: TwitterSignInViewModel = getViewModel {
    parametersOf(
      consumerKey,
      consumerSecret,
      pinCodeProvider,
      { success: Boolean ->
        navigator.goBackWith(success)
      }
    )
  }
  val loading by viewModel.loading.observeAsState(initial = false)

  SignInScaffold(popBackStack = {
    navigator.popBackStack()
  }) {
    if (loading) {
      CircularProgressIndicator()
    }
  }
}
