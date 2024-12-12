package com.dicoding.tanicare.ui.history

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.tanicare.data.local.HistoryEntity
import com.dicoding.tanicare.data.repository.HistoryRepository
import kotlinx.coroutines.launch

class HistoryViewModel(context: Context) : ViewModel() {

    private val repository: HistoryRepository = HistoryRepository.getInstance(context)

    val allHistory: LiveData<List<HistoryEntity>> = repository.getAllHistory()

    fun insertHistory(history: HistoryEntity) {
        viewModelScope.launch {
            repository.insertHistory(history)
        }
    }

    fun deleteAllHistory() {
        viewModelScope.launch {
            repository.deleteAllHistory()
        }
    }
}
