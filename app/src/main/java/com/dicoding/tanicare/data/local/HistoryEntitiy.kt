package com.dicoding.tanicare.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val diseaseName: String,
    val accuracy: String,
    val timestamp: Long,
    val imagePath: String
)

