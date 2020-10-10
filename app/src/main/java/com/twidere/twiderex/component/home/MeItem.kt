package com.twidere.twiderex.component.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.viewinterop.viewModel
import com.twidere.twiderex.annotations.IncomingComposeUpdate
import com.twidere.twiderex.fragment.UserComponent
import com.twidere.twiderex.viewmodel.MeViewModel

class MeItem : HomeNavigationItem() {
    override val name: String
        get() = "Me"
    override val icon: VectorAsset
        get() = Icons.Default.AccountCircle
    override val noActionBar: Boolean
        get() = true

    @OptIn(IncomingComposeUpdate::class)
    @Composable
    override fun onCompose() {
        val viewModel = viewModel<MeViewModel>()
        val user = viewModel.user
        UserComponent(data = user)
    }
}