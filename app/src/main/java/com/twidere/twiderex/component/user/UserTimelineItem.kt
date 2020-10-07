package com.twidere.twiderex.component.user

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.VectorAsset
import com.twidere.twiderex.model.ui.UiUser

class UserTimelineItem : UserTabItem() {
    override val icon: VectorAsset = Icons.Default.List

    @Composable
    override fun onCompose(user: UiUser) {
    }
}