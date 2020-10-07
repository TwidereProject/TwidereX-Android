package com.twidere.twiderex.component.user

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.VectorAsset
import com.twidere.twiderex.model.ui.UiUser

class UserLikeItem : UserTabItem() {
    override val icon: VectorAsset = Icons.Default.Favorite

    @Composable
    override fun onCompose(user: UiUser) {
    }
}