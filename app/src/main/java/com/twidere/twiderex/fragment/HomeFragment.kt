package com.twidere.twiderex.fragment

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.contentColor
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.ui.tooling.preview.Preview
import com.twidere.twiderex.component.home.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : ComposeFragment() {

    @Composable
    fun BottomNavigation(
        items: List<HomeNavigationItem>,
        selectedItem: Int,
        onItemSelected: (Int) -> Unit,
    ) {
        BottomNavigation(
            backgroundColor = MaterialTheme.colors.background
        ) {
            items.forEachIndexed { index, item ->
                BottomNavigationItem(
                    selectedContentColor = MaterialTheme.colors.primary,
                    unselectedContentColor = EmphasisAmbient.current.medium.applyEmphasis(
                        contentColor()
                    ),
                    icon = { Icon(item.icon) },
                    label = { Text(item.name) },
                    selected = selectedItem == index,
                    onSelect = { onItemSelected.invoke(index) }
                )
            }
        }
    }


    @Preview
    @Composable
    override fun onCompose() {
        val (selectedItem, setSelectedItem) = remember { mutableStateOf(0) }
        val menus = listOf(
            HomeTimelineItem(),
            MentionItem(),
            SearchItem(),
            MeItem(),
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    backgroundColor = MaterialTheme.colors.background,
                    title = {
                        Text(text = menus[selectedItem].name)
                    },
                )
            },
            bottomBar = {
                BottomNavigation(menus, selectedItem) {
                    setSelectedItem(it)
                }
            },
        ) {
            Crossfade(current = selectedItem) {
                menus[it].onCompose()
            }
        }
    }
}