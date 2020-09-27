package com.twidere.twiderex.fragment

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Box
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.contentColor
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import com.twidere.twiderex.component.home.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : JetFragment() {

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
                    onClick = { onItemSelected.invoke(index) }
                )
            }
        }
    }


    @Composable
    override fun onCompose() {
        val (selectedItem, setSelectedItem) = savedInstanceState { 0 }
        val menus = listOf(
            HomeTimelineItem(),
            MentionItem(),
            SearchItem(),
            MeItem(),
        )

        Scaffold(
            topBar = {
                com.twidere.twiderex.component.AppBar(
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
            Box(
                paddingBottom = it.bottom,
                paddingTop = it.top,
                paddingStart = it.start,
                paddingEnd = it.end,
            ) {
                Crossfade(current = selectedItem) {
                    menus[it].onCompose()
                }
            }
        }
    }
}