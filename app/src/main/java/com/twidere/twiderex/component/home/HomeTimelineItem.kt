package com.twidere.twiderex.component.home

import androidx.compose.foundation.Icon
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.viewinterop.viewModel
import com.twidere.twiderex.R
import com.twidere.twiderex.component.TimelineComponent
import com.twidere.twiderex.extensions.NavControllerAmbient
import com.twidere.twiderex.viewmodel.twitter.timeline.HomeTimelineViewModel

class HomeTimelineItem : HomeNavigationItem() {
    override val name: String
        get() = "Home"
    override val icon: VectorAsset
        get() = Icons.Default.Home

    @Composable
    override fun onCompose() {
        val viewModel = viewModel<HomeTimelineViewModel>()
        Scaffold(
            floatingActionButton = {
                val navController = NavControllerAmbient.current
                FloatingActionButton(
                    onClick = {
                        navController.navigate(R.id.compose_fragment)
                    }
                ) {
                    Icon(asset = Icons.Default.Add)
                }
            }
        ) {
            TimelineComponent(viewModel = viewModel)
        }
    }
}
