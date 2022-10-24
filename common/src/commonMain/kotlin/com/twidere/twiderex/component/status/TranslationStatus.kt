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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.utils.ITranslationRepo
import com.twidere.twiderex.utils.TranslationParam
import com.twidere.twiderex.utils.TranslationState

@Composable
fun TranslationStatus(
  translationParam: TranslationParam,
  translationRepo: ITranslationRepo = get(),
) {
  val translationState by translationRepo.translation(translationParam)
  Column {
    with(translationState) {
      when (this) {
        TranslationState.NoNeed -> {
          Text(TranslationDefaults.NoNeedTip)
        }
        is TranslationState.Success -> {
          Spacer(
            modifier = Modifier.height(
              TranslationDefaults.gap
            )
          )
          HtmlText(
            htmlText = result,
            openLink = {},
          )
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
  }
}

private object TranslationDefaults {
  val gap = 16.dp
  val progressPadding = 12.dp
  val progressSize = 24.dp
  const val FailedTip = "translation failed"
  const val NoNeedTip = "No need translation"
}
