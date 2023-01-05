/*
 *  Twidere X
 *
 *  Copyright (C) TwidereProject and Contributors
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

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.UrlAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import com.twidere.twiderex.component.foundation.NetworkImage
import com.twidere.twiderex.navigation.RootDeepLinks
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.coroutineScope
import moe.tlaster.twitter.parser.CashTagToken
import moe.tlaster.twitter.parser.EmojiToken
import moe.tlaster.twitter.parser.HashTagToken
import moe.tlaster.twitter.parser.StringToken
import moe.tlaster.twitter.parser.Token
import moe.tlaster.twitter.parser.UrlToken
import moe.tlaster.twitter.parser.UserNameToken

private const val ID_IMAGE = "image"

@OptIn(ExperimentalTextApi::class)
@Composable
fun TokenText(
  token: ImmutableList<Token>,
  layoutDirection: LayoutDirection = LocalLayoutDirection.current,
  modifier: Modifier = Modifier,
  maxLines: Int = Int.MAX_VALUE,
  color: Color = Color.Unspecified,
  fontSize: TextUnit = TextUnit.Unspecified,
  fontStyle: FontStyle? = null,
  fontWeight: FontWeight? = null,
  fontFamily: FontFamily? = null,
  letterSpacing: TextUnit = TextUnit.Unspecified,
  textDecoration: TextDecoration? = null,
  textAlign: TextAlign? = null,
  lineHeight: TextUnit = TextUnit.Unspecified,
  overflow: TextOverflow = TextOverflow.Ellipsis,
  softWrap: Boolean = true,
  textStyle: TextStyle = LocalTextStyle.current.copy(color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)),
  linkStyle: TextStyle = textStyle.copy(MaterialTheme.colors.primary),
  style: TextStyle = LocalTextStyle.current,
  linkResolver: (href: String) -> ResolvedLink = { ResolvedLink(it) },
  emojiResolver: (emoji: String) -> String = { it },
  onLinkClicked: (String) -> Unit,
) {
  val text = remember(token, textStyle, linkStyle, linkResolver, emojiResolver) {
    renderContentAnnotatedString(
      token = token,
      textStyle = textStyle,
      linkStyle = linkStyle,
      linkResolver = linkResolver,
      emojiResolver = emojiResolver,
    )
  }
  val inlineContent = remember {
    persistentMapOf(
      ID_IMAGE to InlineTextContent(
        Placeholder(
          width = textStyle.fontSize,
          height = textStyle.fontSize,
          placeholderVerticalAlign = PlaceholderVerticalAlign.Center,
        ),
      ) { target ->
        NetworkImage(
          data = target,
        )
      },
    )
  }

  var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

  CompositionLocalProvider(
    LocalLayoutDirection provides layoutDirection,
  ) {
    Text(
      text = text,
      modifier = modifier.pointerInput(Unit) {
        forEachGesture {
          coroutineScope {
            val change = awaitPointerEventScope {
              awaitFirstDown()
            }
            val annotation =
              layoutResult?.getOffsetForPosition(change.position)?.let {
                text.getUrlAnnotations(it, it)
                  .firstOrNull()
              }
            if (annotation != null) {
              if (change.pressed != change.previousPressed) change.consume()
              val up = awaitPointerEventScope {
                waitForUpOrCancellation()?.also {
                  if (it.pressed != it.previousPressed) it.consume()
                }
              }
              if (up != null) {
                onLinkClicked.invoke(annotation.item.url)
              }
            }
          }
        }
      },
      color = color,
      fontSize = fontSize,
      fontStyle = fontStyle,
      fontWeight = fontWeight,
      fontFamily = fontFamily,
      letterSpacing = letterSpacing,
      textDecoration = textDecoration,
      textAlign = textAlign,
      lineHeight = lineHeight,
      overflow = overflow,
      softWrap = softWrap,
      maxLines = maxLines,
      inlineContent = inlineContent,
      onTextLayout = {
        layoutResult = it
      },
      style = style,
    )
  }
}

private fun renderContentAnnotatedString(
  token: ImmutableList<Token>,
  textStyle: TextStyle,
  linkStyle: TextStyle,
  linkResolver: (href: String) -> ResolvedLink,
  emojiResolver: (emoji: String) -> String,
): AnnotatedString {
  val renderContext = RenderContext(
    linkResolver = linkResolver,
    emojiResolver = emojiResolver,
    textStyle = textStyle,
    linkStyle = linkStyle,
  )
  return buildAnnotatedString {
    token.forEach {
      renderToken(it, renderContext)
    }
  }
}

private fun AnnotatedString.Builder.renderToken(token: Token, context: RenderContext) {
  when (token) {
    is CashTagToken -> renderSymbol(token.value, RootDeepLinks.Search(token.value), context)
    is HashTagToken -> renderSymbol(token.value, RootDeepLinks.Search(token.value), context)
    is StringToken -> renderText(token.value, context.textStyle)
    is UrlToken -> renderLink(token.value, context)
    is UserNameToken -> renderSymbol(token.value, RootDeepLinks.Twitter.User(token.value.substring(1)), context)
    is EmojiToken -> renderEmoji(token.value, context)
  }
}

private fun AnnotatedString.Builder.renderEmoji(
  emoji: String,
  context: RenderContext,
) {
  appendInlineContent(ID_IMAGE, context.emojiResolver.invoke(emoji))
}

@OptIn(ExperimentalTextApi::class)
private fun AnnotatedString.Builder.renderSymbol(
  symbol: String,
  annotation: String,
  context: RenderContext,
) {
  pushUrlAnnotation(UrlAnnotation(annotation))
  renderText(symbol, context.linkStyle)
  pop()
}

@OptIn(ExperimentalTextApi::class)
private fun AnnotatedString.Builder.renderLink(
  link: String,
  context: RenderContext,
) {
  val resolvedLink = context.linkResolver.invoke(link)
  when {
    resolvedLink.expanded != null -> {
      if (resolvedLink.clickable) {
        pushUrlAnnotation(UrlAnnotation(resolvedLink.expanded))
        renderText(resolvedLink.display ?: resolvedLink.expanded, context.linkStyle)
        pop()
      } else {
        renderText(resolvedLink.display ?: resolvedLink.expanded, context.textStyle)
      }
    }

    resolvedLink.skip -> Unit
    resolvedLink.clickable -> {
      pushUrlAnnotation(UrlAnnotation(link))
      renderText(link, context.linkStyle)
      pop()
    }

    else -> {
      renderText(link, context.textStyle)
    }
  }
}

private fun AnnotatedString.Builder.renderText(
  text: String,
  textStyle: TextStyle,
) {
  withStyle(textStyle.toSpanStyle()) {
    append(text)
  }
}

private data class RenderContext(
  val linkResolver: (href: String) -> ResolvedLink,
  val emojiResolver: (emoji: String) -> String,
  val textStyle: TextStyle,
  val linkStyle: TextStyle,
)
