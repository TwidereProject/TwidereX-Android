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
package com.twidere.twiderex.db.base

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import java.util.concurrent.Executors

@RunWith(AndroidJUnit4::class)
internal abstract class BaseDaoTest<DB : RoomDatabase> {
    protected lateinit var roomDatabase: DB
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    open fun setUp() {
        roomDatabase = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), getDBClass())
            .setTransactionExecutor(Executors.newSingleThreadExecutor()).build()
    }

    @After
    open fun tearDown() {
        roomDatabase.close()
    }

    abstract fun getDBClass(): Class<DB>
}
