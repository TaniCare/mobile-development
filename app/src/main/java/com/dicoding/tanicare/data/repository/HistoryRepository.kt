package com.dicoding.tanicare.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.dicoding.tanicare.data.local.AppDatabase
import com.dicoding.tanicare.data.local.HistoryDao
import com.dicoding.tanicare.data.local.HistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HistoryRepository private constructor(context: Context) {

    private val historyDao: HistoryDao = AppDatabase.getInstance(context).historyDao()

    fun getAllHistory(): LiveData<List<HistoryEntity>> {
        return historyDao.getAllHistory()
    }

    suspend fun insertHistory(history: HistoryEntity) {
        withContext(Dispatchers.IO) {
            historyDao.insertHistory(history)
        }
    }

    suspend fun deleteAllHistory() {
        withContext(Dispatchers.IO) {
            historyDao.deleteAllHistory()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: HistoryRepository? = null

        fun getInstance(context: Context): HistoryRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = HistoryRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }
}
