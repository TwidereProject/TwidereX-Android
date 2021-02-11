/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of Twidere X.
 * 
 *  Twidere X is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Twidere X is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Twidere X. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.component.status

import androidx.compose.foundation.ClickableText
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.model.ui.UiStatus
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

private const val TAG_URL = "url"

@Composable
fun StatusText(
    status: UiStatus,
    color: Color = Color.Unspecified,
    onStatusTextClicked: () -> Unit = {},
) {
    val navigator = LocalNavigator.current
    val textColor = color.takeOrElse {
        LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
    }
    Providers(
        LocalTextStyle provides MaterialTheme.typography.body1.copy(color = textColor)
    ) {
        RenderContent(
            status = status,
            onLinkClicked = {
                navigator.openLink(it)
            },
            onStatusTextClicked = {
                onStatusTextClicked.invoke()
            },
        )
    }
}

@Composable
private fun RenderContent(
    status: UiStatus,
    onLinkClicked: (String) -> Unit = {},
    onStatusTextClicked: () -> Unit = {},
) {
    val value = status.contentAnnotatedString()
    if (value.text.isNotEmpty()) {
        ClickableText(
            onClick = {
                value.getStringAnnotations(start = it, end = it)
                    .firstOrNull()
                    ?.let { annotation ->
                        when (annotation.tag) {
                            TAG_URL -> onLinkClicked.invoke(annotation.item)
                        }
                    } ?: run {
                    onStatusTextClicked.invoke()
                }
            },
            text = value,
        )
    }
}

@Composable
public fun UiStatus.contentAnnotatedString(): AnnotatedString {
    val document = Jsoup.parse(htmlText.replace("\n", "<br>"))
    return buildAnnotatedString {
        document.body().childNodes().forEach {
            RenderNode(it, this@contentAnnotatedString)
        }
    }
}

@Composable
private fun AnnotatedString.Builder.RenderNode(node: Node, status: UiStatus) {
    when (node) {
        is Element -> {
            this.RenderElement(node, status = status)
        }
        is TextNode -> {
            RenderText(node.text())
        }
    }
}

@Composable
private fun AnnotatedString.Builder.RenderText(text: String) {
    pushStyle(
        LocalTextStyle.current.toSpanStyle()
    )
    append(text)
    pop()
}

@Composable
private fun AnnotatedString.Builder.RenderElement(element: Element, status: UiStatus) {
    when (element.normalName()) {
        "a" -> {
            RenderLink(element, status)
        }
        "br" -> {
            RenderText("\n")
        }
        "span", "p" -> {
            element.childNodes().forEach {
                RenderNode(node = it, status = status)
            }
        }
    }
}

@Composable
private fun AnnotatedString.Builder.RenderLink(element: Element, status: UiStatus) {
    val href = element.attr("href")
    val entity = status.url.firstOrNull { it.url == href }
    val media = status.media.firstOrNull { it.url == href }
    Providers(
        LocalTextStyle provides LocalTextStyle.current.copy(color = MaterialTheme.colors.primary)
    ) {
        when {
            entity != null -> {
                if (!entity.displayUrl.contains("pic.twitter.com") && !
                    (status.quote != null && !entity.displayUrl.endsWith(status.quote.statusId))
                ) {
                    pushStringAnnotation(TAG_URL, entity.expandedUrl)
                    RenderText(entity.displayUrl)
                    pop()
                }
            }
            media != null -> {
            }
            else -> {
                pushStringAnnotation(TAG_URL, href)
                element.childNodes().forEach {
                    RenderNode(node = it, status = status)
                }
                pop()
            }
        }
    }
}
