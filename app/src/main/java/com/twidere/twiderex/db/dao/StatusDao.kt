package com.twidere.twiderex.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.twidere.twiderex.db.model.DbStatus

@Dao
interface StatusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(status: List<DbStatus>)

    @Query("SELECT * FROM status")
    suspend fun getAll(): List<DbStatus>
}

