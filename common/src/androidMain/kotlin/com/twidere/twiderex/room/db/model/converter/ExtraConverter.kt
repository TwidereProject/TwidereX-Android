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
package com.twidere.twiderex.room.db.model.converter

import androidx.room.TypeConverter
import com.twidere.twiderex.room.db.model.DbMastodonStatusExtra
import com.twidere.twiderex.room.db.model.DbMastodonUserExtra
import com.twidere.twiderex.room.db.model.DbPoll
import com.twidere.twiderex.room.db.model.DbPreviewCard
import com.twidere.twiderex.room.db.model.DbTwitterStatusExtra
import com.twidere.twiderex.room.db.model.DbTwitterUserExtra
import com.twidere.twiderex.utils.fromJson
import com.twidere.twiderex.utils.json

internal class ExtraConverter {
    @TypeConverter
    fun fromDbTwitterStatusExtraString(value: String?): DbTwitterStatusExtra? {
        return value?.fromJson()
    }

    @TypeConverter
    fun fromTarget(target: DbTwitterStatusExtra?): String? {
        return target?.json()
    }

    @TypeConverter
    fun fromDbMastodonStatusExtraString(value: String?): DbMastodonStatusExtra? {
        return value?.fromJson()
    }

    @TypeConverter
    fun fromTarget(target: DbMastodonStatusExtra?): String? {
        return target?.json()
    }

    @TypeConverter
    fun fromDbTwitterUserExtraString(value: String?): DbTwitterUserExtra? {
        return value?.fromJson()
    }

    @TypeConverter
    fun fromTarget(target: DbTwitterUserExtra?): String? {
        return target?.json()
    }

    @TypeConverter
    fun fromDbMastodonUserExtraString(value: String?): DbMastodonUserExtra? {
        return value?.fromJson()
    }

    @TypeConverter
    fun fromTarget(target: DbMastodonUserExtra?): String? {
        return target?.json()
    }

    @TypeConverter
    fun fromDbPreviewCard(value: String?): DbPreviewCard? {
        return value?.fromJson()
    }

    @TypeConverter
    fun fromTarget(target: DbPreviewCard?): String? {
        return target?.json()
    }

    @TypeConverter
    fun fromDbPoll(value: String?): DbPoll? {
        return value?.fromJson()
    }

    @TypeConverter
    fun fromTarget(target: DbPoll?): String? {
        return target?.json()
    }
}
