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
package com.twidere.twiderex.repository

import com.twidere.twiderex.mock.db.MockAppDatabase
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.ComposeType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

internal class DraftRepositoryTest {
    @Test
    fun sourceAndSourceCountWillUpdateAfterAddOrUpgrade() = runBlocking {
        val repo = DraftRepository(MockAppDatabase())
        assertEquals(0, repo.sourceCount.first())
        repo.addOrUpgrade(
            content = "draft",
            media = emptyList(),
            composeType = ComposeType.New,
            statusKey = MicroBlogKey.twitter("test"),
        )
        val insertDraft = repo.source.first().first()
        assertEquals("draft", insertDraft.content)
        assertEquals(1, repo.sourceCount.first())

        repo.addOrUpgrade(
            content = "upgrade",
            media = emptyList(),
            composeType = ComposeType.New,
            statusKey = MicroBlogKey.twitter("test"),
            draftId = insertDraft.draftId
        )
        assertEquals("upgrade", repo.source.first().first().content)
        assertEquals(1, repo.sourceCount.first())
    }

    @Test
    fun sourceAndSourceCountWillUpdateAfterDeleteDraft() = runBlocking {
        val repo = DraftRepository(MockAppDatabase())
        assertEquals(0, repo.sourceCount.first())
        repo.addOrUpgrade(
            content = "draft",
            media = emptyList(),
            composeType = ComposeType.New,
            statusKey = MicroBlogKey.twitter("test"),
        )
        val insertDraft = repo.source.first().first()

        assertEquals(1, repo.sourceCount.first())

        repo.remove(insertDraft.draftId)
        assertEquals(0, repo.sourceCount.first())
        assert(repo.source.first().isEmpty())
    }
}
