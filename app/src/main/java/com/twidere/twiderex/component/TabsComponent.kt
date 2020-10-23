package com.twidere.twiderex.component

import androidx.compose.foundation.AmbientContentColor
import androidx.compose.foundation.Icon
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AmbientEmphasisLevels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabConstants
import androidx.compose.material.TabConstants.defaultTabIndicatorOffset
import androidx.compose.material.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.extensions.withElevation

@Composable
fun TabsComponent(
    items: List<VectorAsset>,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
) {
    TabRow(
        selectedTabIndex = selectedItem,
        backgroundColor = MaterialTheme.colors.surface.withElevation(),
        indicator = { tabPositions ->
            TabConstants.DefaultIndicator(
                modifier = Modifier.defaultTabIndicatorOffset(tabPositions[selectedItem]),
                color = MaterialTheme.colors.primary,
            )
        }
    ) {
        for (i in 0 until items.count()) {
            Tab(
                selected = selectedItem == i,
                onClick = {
                    onItemSelected(i)
                },
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = AmbientEmphasisLevels.current.medium.applyEmphasis(
                    AmbientContentColor.current
                ),
            ) {
                Box(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(asset = items[i])
                }
            }
        }
    }
}
