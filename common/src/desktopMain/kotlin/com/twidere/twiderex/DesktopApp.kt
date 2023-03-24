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

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.ImageLoaderBuilder
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.cache.disk.DiskCacheBuilder
import com.seiko.imageloader.cache.memory.MemoryCacheBuilder
import com.twidere.twiderex.component.NativeWindow
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.di.setupModules
import com.twidere.twiderex.init.Initializer
import com.twidere.twiderex.init.TwidereServiceFactoryInitialTask
import com.twidere.twiderex.kmp.LocalPlatformWindow
import com.twidere.twiderex.kmp.PlatformWindow
import com.twidere.twiderex.kmp.StorageProvider
import com.twidere.twiderex.kmp.commonConfig
import com.twidere.twiderex.navigation.twidereXSchema
import com.twidere.twiderex.preferences.PreferencesHolder
import com.twidere.twiderex.preferences.ProvidePreferences
import com.twidere.twiderex.preferences.model.AppearancePreferences
import com.twidere.twiderex.preferences.model.DisplayPreferences
import com.twidere.twiderex.ui.LocalVideoPlayback
import com.twidere.twiderex.utils.BrowserLoginDeepLinksChannel
import com.twidere.twiderex.utils.OperatingSystem
import com.twidere.twiderex.utils.WindowsDatastoreModifier
import com.twidere.twiderex.utils.WindowsRegistry
import com.twidere.twiderex.utils.currentOperatingSystem
import it.sauronsoftware.junique.AlreadyLockedException
import it.sauronsoftware.junique.JUnique
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import moe.tlaster.kfilepicker.FilePicker
import moe.tlaster.precompose.navigation.Navigator
import okio.Path.Companion.toPath
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import java.awt.Desktop
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

private val navController = Navigator()
private val mainScope = MainScope()
private const val lockId = "b5b887ec-7fc0-45c9-b32d-47f37cb02f9f"
private const val entryFileName = "twiderex.desktop"

fun runDesktopApp(
  args: Array<String>,
) {
  when (currentOperatingSystem) {
    OperatingSystem.Windows -> {
      ensureWindowsDatastore()
      ensureWindowsRegistry()
      ensureSingleAppInstance(args)
    }
    OperatingSystem.Linux -> {
      ensureDesktopEntry()
      ensureMimeInfo()
      ensureSingleAppInstance(args)
    }
    else -> startDesktopApp()
  }
}

fun ensureWindowsDatastore() {
  WindowsDatastoreModifier.ensureWindowsDatastore()
}

private fun ensureSingleAppInstance(args: Array<String>) {
  val start = try {
    JUnique.acquireLock(lockId) {
      onDeeplink(it)
      null
    }
    true
  } catch (e: AlreadyLockedException) {
    false
  }
  if (start) {
    startDesktopApp()
  } else {
    args.forEach {
      JUnique.sendMessage(lockId, it)
    }
  }
}

private fun ensureDesktopEntry() {
  val entryFile = File("${System.getProperty("user.home")}/.local/share/applications/$entryFileName")
  if (!entryFile.exists()) {
    entryFile.createNewFile()
  }
  val path = Files.readSymbolicLink(Paths.get("/proc/self/exe"))
  entryFile.writeText(
    "[Desktop Entry]${System.lineSeparator()}" +
      "Type=Application${System.lineSeparator()}" +
      "Name=Twidere X${System.lineSeparator()}" +
      "Icon=\"${path.parent.parent.absolutePathString() + "/lib/Twidere X.png" + "\""}${System.lineSeparator()}" +
      "Exec=\"${path.absolutePathString() + "\" %u"}${System.lineSeparator()}" +
      "Terminal=false${System.lineSeparator()}" +
      "Categories=Network;Internet;${System.lineSeparator()}" +
      "MimeType=application/x-$twidereXSchema;x-scheme-handler/$twidereXSchema;x-scheme-handler/twitter;"
  )
}

private fun ensureMimeInfo() {
  val file = File("${System.getProperty("user.home")}/.local/share/applications/mimeinfo.cache")
  if (!file.exists()) {
    file.createNewFile()
  }
  val text = file.readText()
  if (text.isEmpty() || text.isBlank()) {
    file.writeText("[MIME Cache]${System.lineSeparator()}")
  }
  if (!file.readText().contains("x-scheme-handler/$twidereXSchema=$entryFileName;")) {
    file.appendText("${System.lineSeparator()}x-scheme-handler/$twidereXSchema=$entryFileName;")
  }
}

private fun ensureWindowsRegistry() {
  val protocol = WindowsRegistry.readRegistry("HKCR\\TwidereX", "URL Protocol")
  if (protocol?.contains(twidereXSchema) == true) return
  WindowsRegistry.registryUrlProtocol(twidereXSchema)
}

private fun startDesktopApp() {
  startKoin {
    printLogger(Level.NONE)
    setupModules()
  }
  val preferencesHolder = get<PreferencesHolder>()
  try {
    Desktop.getDesktop().setOpenURIHandler { event ->
      onDeeplink(url = event.uri.toString())
    }
  } catch (e: UnsupportedOperationException) {
    e.printStackTrace()
  }
  application {
    val state = rememberWindowState()
    LaunchedEffect(Unit) {
      preferencesHolder.warmup()
    }
    runBlocking {
      preferencesHolder
        .appearancePreferences
        .data.firstOrNull()
        ?.windowInfo
        ?.let {
          state.position = WindowPosition(it.start.dp, it.top.dp)
          state.size = DpSize(it.width.dp, it.height.dp)
        }
    }
    Initializer.withScope(rememberCoroutineScope())
      .add(TwidereServiceFactoryInitialTask())
      .execute()
    ProvidePreferences(preferencesHolder) {
      NativeWindow(
        onCloseRequest = {
          stopKoin()
          runBlocking {
            preferencesHolder.appearancePreferences.updateData {
              it.copy(
                windowInfo = AppearancePreferences.WindowInfo(
                  top = state.position.y.value,
                  start = state.position.x.value,
                  width = state.size.width.value,
                  height = state.size.height.value,
                )
              )
            }
          }
          exitApplication()
        },
        state = state,
        title = "Twidere X",
        icon = painterResource(MR.files.ic_launcher.filePath),
        onKeyEvent = ::handleKeyEvent,
      ) {
        FilePicker.init(window)
        CompositionLocalProvider(
          LocalPlatformWindow provides PlatformWindow(),
          LocalVideoPlayback provides DisplayPreferences.AutoPlayback.Off,
          LocalImageLoader provides generateImageLoader(get()),
        ) {
          App(navController = navController)
        }
      }
    }
  }
}

@OptIn(ExperimentalComposeUiApi::class)
private fun handleKeyEvent(
  event: KeyEvent,
): Boolean {
  return if (
    event.key == Key.Escape
  ) {
    navController.popBackStack()
    true
  } else {
    // config more event later
    false
  }
}

private fun onDeeplink(url: String) {
  if (BrowserLoginDeepLinksChannel.canHandle(url)) {
    mainScope.launch {
      BrowserLoginDeepLinksChannel.send(url)
    }
  } else {
    navController.navigate(url)
  }
}

private fun generateImageLoader(storageService: StorageProvider): ImageLoader {
  return ImageLoaderBuilder()
    .commonConfig()
    .memoryCache {
      MemoryCacheBuilder()
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
    .build()
}
