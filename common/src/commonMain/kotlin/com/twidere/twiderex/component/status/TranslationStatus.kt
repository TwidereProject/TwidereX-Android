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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.DoubleLiftContent
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.icon.IcTranslate
import com.twidere.twiderex.icon.TwidereIcons
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.preferences.LocalDisplayPreferences
import com.twidere.twiderex.utils.ITranslationRepo
import com.twidere.twiderex.utils.TranslationParam
import com.twidere.twiderex.utils.TranslationState

@Composable
fun TranslationWrappers(
  status: UiStatus,
  visibleText: String,
) {
  if (!LocalDisplayPreferences.current.showTranslationButton) {
    return
  }
  var showTranslate by rememberSaveable {
    mutableStateOf(false)
  }
  val interactionSource = remember {
    MutableInteractionSource()
  }
  DoubleLiftContent(
    modifier = Modifier.clickable(
      interactionSource = interactionSource,
      indication = null,
    ) {
      showTranslate = !showTranslate
    },
    state = showTranslate,
    content = {
      if (it) {
        TranslationStatus(
          translationParam = TranslationParam(
            key = status.statusId,
            text = visibleText,
            from = status.language ?: "auto",
          )
        )
      } else {
        Icon(
          modifier = Modifier.padding(
            top = StatusTextDefaults.TransLateIconPadding,
            bottom = StatusTextDefaults.TransLateIconPadding,
          ),
          imageVector = TwidereIcons.IcTranslate,
          contentDescription = "",
          tint = MaterialTheme.colors.primary,
        )
      }
    }
  )
}

@Composable
fun TranslationStatus(
  translationParam: TranslationParam,
  modifier: Modifier = Modifier,
  translationRepo: ITranslationRepo = get(),
) {
  val translationState by remember {
    translationRepo.translation(translationParam)
  }
  DoubleLiftContent(
    modifier = modifier,
    state = translationState,
    content = {
      when (it) {
        TranslationState.NoNeed -> {
          Text(TranslationDefaults.NoNeedTip)
        }
        is TranslationState.Success -> {
          Column {
            Spacer(
              modifier = Modifier.height(
                TranslationDefaults.gap
              )
            )
            HtmlText(
              htmlText = it.result,
              openLink = {},
            )
          }
        }
        TranslationState.InProgress -> {
          CircularProgressIndicator(
            modifier = Modifier.padding(
              TranslationDefaults.progressPadding
            ).size(
              TranslationDefaults.progressSize
            )
          )
        }
        is TranslationState.Failed -> {
          Text(TranslationDefaults.FailedTip)
        }
      }
    }
  )
}

private object TranslationDefaults {
  val gap = 16.dp
  val progressPadding = 12.dp
  val progressSize = 24.dp
  const val FailedTip = "translation failed"
  const val NoNeedTip = "No need translation"
}
