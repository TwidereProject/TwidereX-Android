package com.twidere.twiderex.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.ui.profileImageSize
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.ui.statusLineWidth

@Composable
fun StatusLineComponent(
    lineColor: Color = MaterialTheme.colors.primary,
    lineWidth: Dp = statusLineWidth,
    startPadding: Dp = standardPadding * 2 + profileImageSize / 2 - lineWidth / 2,
    topPoint: Dp = standardPadding * 2 + profileImageSize / 2,
    lineDown: Boolean = false,
    lineUp: Boolean = false,
    child: @Composable () -> Unit
) {
    Box {
        if (lineDown) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(
                        start = startPadding,
                        top = topPoint,
                    )
            ) {
                Box(
                    modifier = Modifier
                        .width(statusLineWidth)
                        .fillMaxHeight()
                        .align(Alignment.BottomStart)
                        .background(lineColor)

                )
            }
        }

        if (lineUp) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(y = (-1).dp)
                    .padding(
                        start = startPadding,
                    )
            ) {
                Box(
                    modifier = Modifier
                        .width(statusLineWidth)
                        .height(topPoint + 1.dp)
                        .align(Alignment.TopStart)
                        .background(lineColor)

                )
            }
        }
        child.invoke()
    }

}