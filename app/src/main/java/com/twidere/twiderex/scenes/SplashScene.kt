package com.twidere.twiderex.scenes

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onActive
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.viewinterop.viewModel
import androidx.navigation.compose.navigate
import com.twidere.twiderex.R
import com.twidere.twiderex.ui.AmbientNavController
import com.twidere.twiderex.viewmodel.SplashViewModel
import kotlinx.coroutines.launch

@Composable
fun SplashScene() {
    val viewModel = viewModel<SplashViewModel>()
    val scope = rememberCoroutineScope()
    val navController = AmbientNavController.current
    Scaffold {
        Column(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TODO: replace with real icon
            Image(vectorResource(id = R.drawable.ic_launcher_foreground))
        }
    }
    onActive(
        callback = {
            scope.launch {
                if (viewModel.hasAccount()) {
                    navController.navigate("home")
                } else {
                    navController.navigate("signin/twitter")
                }
            }
        }
    )
}