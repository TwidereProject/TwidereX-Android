package com.twidere.twiderex.component.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.viewinterop.viewModel
import com.twidere.twiderex.component.TimelineComponent
import com.twidere.twiderex.viewmodel.twitter.timeline.MentionsTimelineViewModel

class MentionItem : HomeNavigationItem() {
    override val name: String
        get() = "Mentions"
    override val icon: VectorAsset
        get() = Icons.Default.AlternateEmail

    @Composable
    override fun onCompose() {
        val viewModel = viewModel<MentionsTimelineViewModel>()
        TimelineComponent(viewModel = viewModel)
    }
}