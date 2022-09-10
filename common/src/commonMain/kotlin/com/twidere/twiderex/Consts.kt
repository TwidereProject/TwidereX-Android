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
package com.twidere.twiderex

const val defaultLoadCount = 20

const val twitterHost1 = "https://twitter.com"
const val twitterHost2 = "https://mobile.twitter.com"
const val twitterHost3 = "https://www.twitter.com"
const val twitterHost4 = "http://twitter.com"
const val twitterHost5 = "http://mobile.twitter.com"
const val twitterHost6 = "http://www.twitter.com"

const val twitterStatusDeeplinkSuffix = "/{screenName}/status/{statusId:[0-9]+}"
const val twitterHomeDeeplinkSuffix = "/*"
const val twitterUserDeeplinkSuffix = "{screenName}"
const val twitterSearchDeeplinkSuffix = "/search?q={keyword}"

val twitterHosts = listOf(
  twitterHost1,
  twitterHost2,
  twitterHost3,
  twitterHost4,
  twitterHost5,
  twitterHost6,
)

internal const val twitterTonApiHost = "ton.twitter.com"
