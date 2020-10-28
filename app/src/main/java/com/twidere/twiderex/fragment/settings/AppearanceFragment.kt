package com.twidere.twiderex.fragment.settings

import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.AppBar
import com.twidere.twiderex.component.AppBarNavigationButton
import com.twidere.twiderex.component.lazy.itemDivider
import com.twidere.twiderex.fragment.JetFragment

class AppearanceFragment : JetFragment() {
    @OptIn(ExperimentalLazyDsl::class)
    @Composable
    override fun onCompose() {
        Scaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        Text(text = "Appearance")
                    },
                )
            }
        ) {
            LazyColumn {
                item {
                    ListItem(
                        text = {
                            Text(text = "Highlight color")
                        },
                        trailing = {
                            Box(
                                modifier = Modifier
                                    .preferredHeight(24.dp)
                                    .preferredWidth(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .aspectRatio(1F)
                                    .background(MaterialTheme.colors.primary),
                            ) {
                            }
                        }
                    )
                }
                itemDivider()
                item {
                    ListItem(
                        text = {
                            Text(text = "Tab position")
                        }
                    )
                }
                itemDivider()
                item {
                    ListItem(
                        text = {
                            Text(text = "Dark Mode")
                        },
                        secondaryText = {
                            Text(text = "Match system")
                        },
                    )
                }
            }
        }
    }
}