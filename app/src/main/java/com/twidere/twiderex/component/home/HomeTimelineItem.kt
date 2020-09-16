package com.twidere.twiderex.component.home

import androidx.compose.foundation.Text
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.launchInComposition
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.viewinterop.viewModel
import androidx.ui.tooling.preview.Preview
import com.twidere.twiderex.viewmodel.twitter.HomeTimelineViewModel

class HomeTimelineItem : HomeNavigationItem() {
    override val name: String
        get() = "Home"
    override val icon: VectorAsset
        get() = Icons.Default.Home

    @Preview
    @Composable
    override fun onCompose() {
        val viewModel = viewModel<HomeTimelineViewModel>()
        val items by viewModel.source.observeAsState(listOf())
        launchInComposition {
            viewModel.refresh()
        }
        LazyColumnForIndexed(items = items) { index, item ->
            Text(text = item.status.text)
        }
    }
}


