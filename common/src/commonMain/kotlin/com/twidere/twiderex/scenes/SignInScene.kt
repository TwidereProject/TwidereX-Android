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
package com.twidere.twiderex.scenes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.BuildConfig
import com.twidere.twiderex.MR
import com.twidere.twiderex.component.foundation.AlertDialog
import com.twidere.twiderex.component.foundation.SignInButton
import com.twidere.twiderex.component.foundation.SignInScaffold
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.kmp.Platform
import com.twidere.twiderex.kmp.currentPlatform
import com.twidere.twiderex.navigation.Root
import com.twidere.twiderex.navigation.RootDeepLinks
import io.github.seiko.precompose.annotation.NavGraphDestination
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.PopUpTo

@NavGraphDestination(
  route = Root.SignIn.General,
  deepLink = [RootDeepLinks.SignIn]
)
@Composable
fun SignInScene(
  navigator: Navigator,
) {
  val toHome = remember {
    {
      navigator.navigate(
        Root.Home,
        NavOptions(
          popUpTo = PopUpTo(
            route = Root.SignIn.General,
            inclusive = true,
          )
        )
      )
    }
  }

  SignInScaffold(popBackStack = {
    navigator.popBackStack()
  }) {
    TwitterSignIn(
      clickSignIn = {
        navigator.navigateForResult(
          Root.SignIn.Twitter(
            BuildConfig.CONSUMERKEY,
            BuildConfig.CONSUMERSECRET,
          )
        )?.let {
          it as Boolean
        }?.let {
          if (it) {
            toHome.invoke()
          }
        }
      },
      clickCustomKeySignIn = {
        navigator.navigateForResult(
          it
        )?.let {
          it as Boolean
        }?.let {
          if (it) {
            toHome.invoke()
          }
        }
      },

    )
    Spacer(modifier = Modifier.height(SignInSceneDefaults.ButtonSpacing))
    MastodonSignIn {
      navigator.navigateForResult(Root.SignIn.Mastodon)
        ?.let {
          it as Boolean
        }?.let {
          if (it) {
            toHome.invoke()
          }
        }
    }
  }
}

object SignInSceneDefaults {
  val ButtonSpacing = 16.dp
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MastodonSignIn(
  clickSignIn: suspend () -> Unit,
) {
  val scope = rememberCoroutineScope()
  SignInButton(
    onClick = {
      scope.launch {
        clickSignIn.invoke()
      }
    },
    border = ButtonDefaults.outlinedBorder,
    color = MaterialTheme.colors.surface,
    contentColor = MaterialTheme.colors.primary,
  ) {
    ListItem(
      icon = {
        Icon(
          painter = painterResource(res = com.twidere.twiderex.MR.files.ic_mastodon_logo_blue),
          contentDescription = stringResource(
            res = com.twidere.twiderex.MR.strings.accessibility_common_logo_mastodon
          ),
          modifier = Modifier.size(24.dp),
        )
      },
      text = {
        Text(
          text = stringResource(res = com.twidere.twiderex.MR.strings.scene_sign_in_sign_in_with_mastodon)
        )
      },
      trailing = {
        IconButton(
          enabled = false,
          onClick = {},
        ) {
          Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = stringResource(
              res = com.twidere.twiderex.MR.strings.scene_sign_in_sign_in_with_mastodon
            )
          )
        }
      }
    )
  }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TwitterSignIn(
  clickSignIn: suspend () -> Unit,
  clickCustomKeySignIn: suspend (String) -> Unit,
) {
  val scope = rememberCoroutineScope()
  var showKeyConfiguration by remember { mutableStateOf(false) }
  if (showKeyConfiguration) {
    TwitterCustomKeySignIn(
      onDismissRequest = {
        showKeyConfiguration = false
      },
      clickCustomKeySignIn = clickCustomKeySignIn,
    )
  }
  SignInButton(
    onClick = {
      scope.launch {
        clickSignIn.invoke()
      }
    },
  ) {
    ListItem(
      icon = {
        Icon(
          painter = painterResource(res = com.twidere.twiderex.MR.files.ic_twitter_logo_white),
          contentDescription = stringResource(
            res = com.twidere.twiderex.MR.strings.accessibility_common_logo_twitter
          ),
          modifier = Modifier.size(24.dp),
        )
      },
      text = {
        Text(
          text = stringResource(res = com.twidere.twiderex.MR.strings.scene_sign_in_sign_in_with_twitter)
        )
      },
      trailing = if (currentPlatform == Platform.Android) {
        {
          IconButton(
            onClick = {
              showKeyConfiguration = true
            }
          ) {
            Icon(
              imageVector = Icons.Default.MoreHoriz,
              contentDescription = stringResource(res = com.twidere.twiderex.MR.strings.accessibility_common_more)
            )
          }
        }
      } else {
        null
      }
    )
  }
}

@Composable
private fun TwitterCustomKeySignIn(
  onDismissRequest: () -> Unit,
  clickCustomKeySignIn: suspend (String) -> Unit,
) {
  val scope = rememberCoroutineScope()
  var apiKey by remember { mutableStateOf("") }
  var apiSecret by remember { mutableStateOf("") }
  AlertDialog(
    onDismissRequest = {
      onDismissRequest.invoke()
    },
    title = {
      Text(text = stringResource(res = MR.strings.scene_sign_in_twitter_options_sign_in_with_custom_twitter_key))
    },
    text = {
      Column {
        Text(text = stringResource(res = MR.strings.scene_sign_in_twitter_options_twitter_api_v2_access_is_required))
        OutlinedTextField(
          modifier = Modifier.fillMaxWidth(),
          value = apiKey,
          onValueChange = { apiKey = it },
          placeholder = {
            Text(text = "API key")
          }
        )
        OutlinedTextField(
          modifier = Modifier.fillMaxWidth(),
          value = apiSecret,
          onValueChange = { apiSecret = it },
          placeholder = {
            Text(text = "API secret key")
          }
        )
      }
    },
    dismissButton = {
      TextButton(
        onClick = {
          onDismissRequest.invoke()
        }
      ) {
        Text(text = stringResource(res = MR.strings.common_controls_actions_cancel))
      }
    },
    confirmButton = {
      TextButton(
        onClick = {
          scope.launch {
            clickCustomKeySignIn(
              Root.SignIn.Twitter(apiKey, apiSecret)
            )
          }
        },
        enabled = apiKey.isNotEmpty() && apiSecret.isNotEmpty()
      ) {
        Text(text = stringResource(res = MR.strings.scene_drawer_sign_in))
      }
    },
  )
}
