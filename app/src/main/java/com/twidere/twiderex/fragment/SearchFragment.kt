package com.twidere.twiderex.fragment

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Icon
import androidx.compose.foundation.ProvideTextStyle
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.AppBar
import com.twidere.twiderex.component.AppBarNavigationButton
import com.twidere.twiderex.component.TabsComponent
import com.twidere.twiderex.component.TextInput
import com.twidere.twiderex.component.TopAppBarElevation

class SearchFragment : JetFragment() {
    @OptIn(ExperimentalFoundationApi::class, ExperimentalFocus::class)
    @Composable
    override fun onCompose() {
        val (textState, setTextState) = remember { mutableStateOf(TextFieldValue()) }
        Scaffold {
            Column {
                Surface(
                    elevation = TopAppBarElevation,
                ) {
                    Column {
                        AppBar(
                            navigationIcon = {
                                AppBarNavigationButton()
                            },
                            elevation = 0.dp,
                            title = {
                                ProvideTextStyle(value = MaterialTheme.typography.body1) {
                                    Row {
                                        TextInput(
                                            modifier = Modifier
                                                .align(Alignment.CenterVertically)
                                                .weight(1F),
                                            value = textState,
                                            onValueChange = {
                                                setTextState(it)
                                            },
                                            placeholder = {
                                                Text(text = "Tap to search...")
                                            },
                                            imeAction = ImeAction.Search,
                                            alignment = Alignment.CenterStart,
                                        )
                                        IconButton(onClick = {}) {
                                            Icon(asset = Icons.Default.Save)
                                        }
                                    }
                                }
                            }
                        )
                        TabsComponent(
                            items = listOf(
                                Icons.Default.List,
                                Icons.Default.Image,
                                Icons.Default.AccountBox,
                            ),
                            selectedItem = 0,
                            onItemSelected = {},
                        )
                    }
                }

                Box(
                    modifier = Modifier.weight(1F),
                ) {

                }
            }
        }
    }
}