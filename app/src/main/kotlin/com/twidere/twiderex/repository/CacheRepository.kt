package com.twidere.twiderex.repository

import com.bumptech.glide.Glide
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.CacheDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.File

class CacheRepository(
    private val database: CacheDatabase,
    private val appDatabase: AppDatabase,
    private val glide: Glide,
    private val cacheDirs: List<File>,
) {
    suspend fun clearDatabaseCache() = coroutineScope {
        launch(Dispatchers.IO) {
            database.clearAllTables()
        }
    }

    suspend fun clearImageCache() = coroutineScope {
        launch(Dispatchers.Main) {
            glide.clearMemory()
        }
        launch(Dispatchers.IO) {
            glide.clearDiskCache()
        }
    }

    suspend fun clearCacheDir() = coroutineScope {
        launch(Dispatchers.IO) {
            cacheDirs.forEach {
                it.listFiles()?.forEach { file ->
                    file.deleteRecursively()
                }
            }
        }
    }

    suspend fun clearSearchHistory() = coroutineScope {
        launch(Dispatchers.IO) {
            appDatabase.searchDao().clear()
        }
    }
}