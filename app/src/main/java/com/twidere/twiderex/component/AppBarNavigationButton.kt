package com.twidere.twiderex.component

import androidx.compose.foundation.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.VectorAsset
import com.twidere.twiderex.extensions.NavControllerAmbient

@Composable
fun AppBarNavigationButton(
    icon: VectorAsset = Icons.Default.ArrowBack,
) {
    val navController = NavControllerAmbient.current
    IconButton(onClick = {
        navController.popBackStack()
    }) {
        Icon(asset = icon)
    }
}