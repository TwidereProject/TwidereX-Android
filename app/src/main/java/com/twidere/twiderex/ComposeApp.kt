package com.twidere.twiderex

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.twidere.twiderex.ui.AmbientNavController
import com.twidere.twiderex.ui.TwidereXTheme

@Composable
fun App() {
    val navController = rememberNavController()
    TwidereXTheme {
        Providers(
            AmbientNavController provides navController,
        ) {
            NavHost(navController = navController, startDestination = initialRoute) {
                route()
            }
        }
    }
}
