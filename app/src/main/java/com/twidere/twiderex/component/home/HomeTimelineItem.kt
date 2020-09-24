package com.twidere.twiderex.component.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.viewinterop.viewModel
import com.twidere.twiderex.component.TimelineComponent
import com.twidere.twiderex.viewmodel.twitter.timeline.HomeTimelineViewModel

class HomeTimelineItem : HomeNavigationItem() {
    override val name: String
        get() = "Home"
    override val icon: VectorAsset
        get() = Icons.Default.Home

    @Composable
    override fun onCompose() {
        val viewModel = viewModel<HomeTimelineViewModel>()
        TimelineComponent(viewModel = viewModel)
    }
}
