package com.twidere.twiderex.db.dao

import androidx.room.*
import com.twidere.twiderex.db.model.DbUser

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(user: List<DbUser>)

    @Query("SELECT * FROM user WHERE userId == :id")
    suspend fun findWithUserId(id: String): DbUser?

    @Update
    suspend fun update(vararg user: DbUser)
}