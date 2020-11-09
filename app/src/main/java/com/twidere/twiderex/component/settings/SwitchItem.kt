package com.twidere.twiderex.component.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.ListItem
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.twidere.twiderex.component.ColoredSwitch
import com.twidere.twiderex.settings.types.BooleanSettingItem


fun LazyListScope.switchItem(
    data: BooleanSettingItem,
) {
    item {
        val checked by data.data.observeAsState(initial = data.initialValue)
        ListItem(
            modifier = Modifier.clickable(onClick = { data.apply(!checked) }),
            text = {
                data.title.invoke()
            },
            trailing = {
                ColoredSwitch(
                    checked = checked,
                    onCheckedChange = {
                        data.apply(it)
                    },
                )
            }
        )
    }
}
