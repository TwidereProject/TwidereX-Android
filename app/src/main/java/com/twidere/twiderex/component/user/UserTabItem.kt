package com.twidere.twiderex.component.user

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.VectorAsset
import com.twidere.twiderex.model.ui.UiUser

abstract class UserTabItem {
    abstract val icon: VectorAsset

    @Composable
    abstract fun onCompose(
        user: UiUser
    )
}