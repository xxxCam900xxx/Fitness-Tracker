package com.example.heartspiek.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.heartspiek.database.entity.DailyDistance
import com.example.heartspiek.database.entity.Measurement
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyDistanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dailyDistance: DailyDistance)

    @Query("SELECT * FROM daily_distance WHERE date = :date LIMIT 1")
    fun getDistanceForDate(date: Long): DailyDistance?

    @Query("SELECT * FROM daily_distance ORDER BY date DESC")
    fun getAllDistance(): Flow<List<DailyDistance>>
}
