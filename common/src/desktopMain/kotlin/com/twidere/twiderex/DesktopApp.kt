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

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.application
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.di.setupModules
import com.twidere.twiderex.init.Initializer
import com.twidere.twiderex.init.TwidereServiceFactoryInitialTask
import com.twidere.twiderex.kmp.LocalPlatformWindow
import com.twidere.twiderex.kmp.PlatformWindow
import com.twidere.twiderex.navigation.twidereXSchema
import com.twidere.twiderex.preferences.PreferencesHolder
import com.twidere.twiderex.preferences.ProvidePreferences
import com.twidere.twiderex.preferences.model.DisplayPreferences
import com.twidere.twiderex.ui.LocalVideoPlayback
import com.twidere.twiderex.utils.CustomTabSignInChannel
import com.twidere.twiderex.utils.OperatingSystem
import com.twidere.twiderex.utils.WindowsDatastoreModifier
import com.twidere.twiderex.utils.WindowsRegistry
import com.twidere.twiderex.utils.currentOperatingSystem
import it.sauronsoftware.junique.AlreadyLockedException
import it.sauronsoftware.junique.JUnique
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import moe.tlaster.kfilepicker.FilePicker
import moe.tlaster.precompose.PreComposeWindow
import moe.tlaster.precompose.navigation.NavController
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import java.awt.Desktop
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

private val navController = NavController()
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
            "MimeType=x-scheme-handler/$twidereXSchema;"
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
        LaunchedEffect(Unit) {
            preferencesHolder.warmup()
        }
        Initializer.withScope(rememberCoroutineScope())
            .add(TwidereServiceFactoryInitialTask())
            .execute()
        ProvidePreferences(preferencesHolder) {
            PreComposeWindow(
                onCloseRequest = {
                    stopKoin()
                    exitApplication()
                },
                title = "Twidere X",
                icon = painterResource(MR.files.ic_launcher.filePath),
            ) {
                FilePicker.init(window)
                CompositionLocalProvider(
                    LocalPlatformWindow provides PlatformWindow(),
                    LocalVideoPlayback provides DisplayPreferences.AutoPlayback.Off
                ) {
                    App(navController = navController)
                }
            }
        }
    }
}

private fun onDeeplink(url: String) {
    if (CustomTabSignInChannel.canHandle(url)) {
        mainScope.launch {
            CustomTabSignInChannel.send(url)
        }
    } else {
        navController.navigate(url)
    }
}
