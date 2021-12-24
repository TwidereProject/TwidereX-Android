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
package com.twidere.twiderex.db.sqldelight.transform

import com.twidere.twiderex.mock.model.mockUiUrlEntity
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.model.ui.UserMetrics
import com.twidere.twiderex.model.ui.twitter.TwitterUserExtra
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

internal class UserTransformTest {
    @Test
    fun transform() {
        val ui = UiUser(
            userKey = MicroBlogKey.valueOf("userKey@twitter.com"),
            id = UUID.randomUUID().toString(),
            acct = MicroBlogKey.valueOf("acct"),
            name = "name",
            screenName = "screenName",
            profileImage = "profileImage",
            profileBackgroundImage = "profileBackgroundImage",
            metrics = UserMetrics(
                fans = 1,
                follow = 2,
                status = 3,
                listed = 4
            ),
            rawDesc = "rawDesc",
            htmlDesc = "htmlDesc",
            website = "website",
            location = "location",
            verified = true,
            protected = false,
            platformType = PlatformType.Twitter,
            extra = TwitterUserExtra(
                pinned_tweet_id = "pinned_tweet_id",
                url = listOf(mockUiUrlEntity())
            )
        )
        val db = ui.toDbUser()
        assertEquals(ui, db.toUi())
    }
}
