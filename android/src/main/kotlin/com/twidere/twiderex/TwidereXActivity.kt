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
package com.twidere.twiderex

import android.Manifest
import android.content.ContentResolver.SCHEME_ANDROID_RESOURCE
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.ImageLoaderBuilder
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.cache.disk.DiskCacheBuilder
import com.seiko.imageloader.cache.memory.MemoryCacheBuilder
import com.seiko.imageloader.component.keyer.Keyer
import com.seiko.imageloader.request.Options
import com.twidere.twiderex.action.LocalStatusActions
import com.twidere.twiderex.action.StatusActions
import com.twidere.twiderex.component.LocalWindowInsetsController
import com.twidere.twiderex.component.PermissionCheck
import com.twidere.twiderex.component.foundation.LocalInAppNotification
import com.twidere.twiderex.compose.LocalResLoader
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.extensions.flowWithLifecycle
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.kmp.LocalPlatformWindow
import com.twidere.twiderex.kmp.LocalRemoteNavigator
import com.twidere.twiderex.kmp.PlatformWindow
import com.twidere.twiderex.kmp.RemoteNavigator
import com.twidere.twiderex.kmp.ResLoader
import com.twidere.twiderex.kmp.StorageProvider
import com.twidere.twiderex.kmp.commonConfig
import com.twidere.twiderex.navigation.Router
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.preferences.PreferencesHolder
import com.twidere.twiderex.preferences.ProvidePreferences
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalActiveAccountViewModel
import com.twidere.twiderex.ui.LocalIsActiveNetworkMetered
import com.twidere.twiderex.utils.BrowserLoginDeepLinksChannel
import com.twidere.twiderex.utils.LocalPlatformResolver
import com.twidere.twiderex.utils.PlatformResolver
import moe.tlaster.kfilepicker.FilePicker
import moe.tlaster.precompose.lifecycle.PreComposeActivity
import moe.tlaster.precompose.lifecycle.setContent
import moe.tlaster.precompose.navigation.Navigator
import okio.Path.Companion.toPath
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TwidereXActivity : PreComposeActivity(), KoinComponent {

  private val navController by lazy {
    Navigator()
  }
  val permissions = listOfNotNull(
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.POST_NOTIFICATIONS else null,
  )

  private val statusActions: StatusActions by inject()

  private val preferencesHolder: PreferencesHolder by inject()

  private val inAppNotification: InAppNotification by inject()

  private val platformResolver: PlatformResolver by inject()

  private val remoteNavigator: RemoteNavigator by inject()

  private val isActiveNetworkMetered =
    TwidereApp.isNetworkActiveFlow.flowWithLifecycle(lifecycle)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    FilePicker.init(activityResultRegistry, this, contentResolver)
    WindowCompat.setDecorFitsSystemWindows(window, false)
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    setContent {
      var showSplash by rememberSaveable { mutableStateOf(true) }
      LaunchedEffect(Unit) {
        preferencesHolder.warmup()
        showSplash = false
      }
      App()
      AnimatedVisibility(
        visible = showSplash,
        enter = fadeIn(),
        exit = fadeOut(),
      ) {
        Splash()
      }
    }
    intent.data?.let {
      onDeeplink(it)
    }
  }

  @Composable
  private fun Splash() {
    MaterialTheme(
      colors = if (isSystemInDarkTheme()) {
        darkColors()
      } else {
        lightColors()
      },
    ) {
      Box(Modifier.fillMaxSize(), Alignment.Center) {
        Image(
          painter = painterResource(id = R.drawable.ic_login_logo),
          contentDescription = stringResource(id = com.twidere.common.R.string.accessibility_common_logo_twidere),
        )
      }
    }
  }

  @Composable
  private fun App() {
    val windowInsetsControllerCompat =
      remember { WindowInsetsControllerCompat(window, window.decorView) }
    val accountViewModel =
      com.twidere.twiderex.di.ext.getViewModel<com.twidere.twiderex.viewmodel.ActiveAccountViewModel>()
    val account by accountViewModel.account.observeAsState(null)
    val isActiveNetworkMetered by isActiveNetworkMetered.observeAsState(initial = false)
    CompositionLocalProvider(
      LocalInAppNotification provides inAppNotification,
      LocalWindowInsetsController provides windowInsetsControllerCompat,
      LocalActiveAccount provides account,
      LocalStatusActions provides statusActions,
      LocalActiveAccountViewModel provides accountViewModel,
      LocalIsActiveNetworkMetered provides isActiveNetworkMetered,
      LocalPlatformResolver provides platformResolver,
      LocalResLoader provides ResLoader(this),
      LocalRemoteNavigator provides remoteNavigator,
      LocalPlatformWindow provides PlatformWindow(window),
      LocalImageLoader provides generateImageLoader(get()),
    ) {
      ProvidePreferences(
        preferencesHolder,
      ) {
        PermissionCheck(permissions)
        Router(
          navController = navController,
          isDebug = moe.tlaster.kfilepicker.BuildConfig.DEBUG,
        )
      }
    }
  }

  private fun generateImageLoader(storageService: StorageProvider): ImageLoader {
    return ImageLoaderBuilder(this)
      .commonConfig()
      .memoryCache {
        MemoryCacheBuilder(this)
          // Set the max size to 25% of the app's available memory.
          .maxSizePercent(0.25)
          .build()
      }
      .diskCache {
        DiskCacheBuilder()
          .directory(storageService.cacheDir.toPath().resolve("image_cache"))
          .maxSizeBytes(512L * 1024 * 1024) // 512MB
          .build()
      }
      .components {
        add(
          // TODO remove after library upgrade
          // fix resource cache, resId will be change
          // @return android.resource://com.twidere.twiderex/2131755047-ic_heart-16
          object : Keyer {
            override fun key(data: Any, options: Options): String? {
              val androidUri = when {
                data is String && data.startsWith(SCHEME_ANDROID_RESOURCE) -> data
                data is com.eygraber.uri.Uri && data.scheme == SCHEME_ANDROID_RESOURCE -> data.toString()
                else -> return null
              }
              val id = androidUri.substringAfterLast('/', "").toIntOrNull() ?: return null
              val entryName = resources.getResourceEntryName(id)
              return "$data-$entryName-${resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK}"
            }
          },
        )
      }
      .build()
  }

  private fun onDeeplink(it: Uri) {
    if (BrowserLoginDeepLinksChannel.canHandle(it.toString())) {
      lifecycleScope.launchWhenResumed {
        BrowserLoginDeepLinksChannel.send(it.toString())
      }
    } else {
      navController.navigate(it.toString())
    }
  }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    intent?.data?.let {
      onDeeplink(it)
    }
  }
}
