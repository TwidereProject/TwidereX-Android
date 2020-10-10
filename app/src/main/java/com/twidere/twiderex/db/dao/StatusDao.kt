package com.twidere.twiderex.db.dao

import androidx.room.*
import com.twidere.twiderex.db.model.DbStatus

@Dao
interface StatusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(status: List<DbStatus>)

    @Query("SELECT * FROM status")
    suspend fun getAll(): List<DbStatus>

    @Query("SELECT * FROM status WHERE statusId == :id")
    suspend fun findWithStatusId(id: String): DbStatus?

    @Update
    suspend fun update(vararg user: DbStatus)
}

