package com.example.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "saved_analyses")
data class AnalysisEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String, // "MARKOWITZ", "TIME_SERIES", "DCF", "RISK_VAR", "ADVISOR_NOTE"
    val summary: String,
    val detailedMetricsJson: String,
    val cladrbInsight: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface AnalysisDao {
    @Query("SELECT * FROM saved_analyses ORDER BY timestamp DESC")
    fun getAllAnalyses(): Flow<List<AnalysisEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalysis(analysis: AnalysisEntity): Long

    @Query("DELETE FROM saved_analyses WHERE id = :id")
    suspend fun deleteAnalysisById(id: Long)

    @Query("SELECT * FROM saved_analyses WHERE id = :id LIMIT 1")
    suspend fun getAnalysisById(id: Long): AnalysisEntity?
}
