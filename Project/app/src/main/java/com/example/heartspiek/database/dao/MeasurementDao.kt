package com.example.heartspiek.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.heartspiek.database.entity.Measurement
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(measurement: Measurement)

    @Query("SELECT * FROM measurements WHERE date = :date LIMIT 1")
    suspend fun getMeasurementForDate(date: Long): Measurement?

    @Query("SELECT * FROM measurements ORDER BY date DESC")
    fun getAllMeasurements(): Flow<List<Measurement>>
}