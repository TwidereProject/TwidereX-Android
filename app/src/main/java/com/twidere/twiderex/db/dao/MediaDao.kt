package com.twidere.twiderex.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.twidere.twiderex.db.model.DbMedia

@Dao
interface MediaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(media: List<DbMedia>)
}