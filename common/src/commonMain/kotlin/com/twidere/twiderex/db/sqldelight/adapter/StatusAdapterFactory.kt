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
package com.twidere.twiderex.db.sqldelight.adapter

import com.soywiz.krypto.encoding.Base64
import com.soywiz.krypto.encoding.base64
import com.squareup.sqldelight.ColumnAdapter
import com.twidere.twiderex.db.sqldelight.model.DbStatusContent
import com.twidere.twiderex.sqldelight.table.DbStatus
import com.twidere.twiderex.utils.fromJson
import com.twidere.twiderex.utils.json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal object StatusAdapterFactory {
  fun create() = MicroBlogKeyColumnAdapter().let {
    DbStatus.Adapter(
      statusKeyAdapter = it,
      displayStatusKeyAdapter = it,
      contentAdapter = StatusContentColumnAdapter
    )
  }
}

internal object StatusContentColumnAdapter : ColumnAdapter<DbStatusContent, String> {
  override fun decode(databaseValue: String): DbStatusContent {
    val value = Base64.decode(databaseValue).decodeToString()
    val json = value.fromJson<JsonElement>()

    return when (val type = json.jsonObject.getValue("type").jsonPrimitive.content) {
      "twitter" -> value.fromJson<DbStatusContent.Twitter>()
      "mastodon" -> value.fromJson<DbStatusContent.Mastodon>()
      "mastodon-notification" -> value.fromJson<DbStatusContent.MastodonNotification>()
      else -> throw Error("DbStatusContent decode Failed with unknown type:$type")
    }
  }

  override fun encode(value: DbStatusContent): String {
    return value.json().encodeToByteArray().base64
  }
}
