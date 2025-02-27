package com.example.heartspiek.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.heartspiek.database.dao.DailyDistanceDao
import com.example.heartspiek.database.dao.MeasurementDao
import com.example.heartspiek.database.entity.DailyDistance
import com.example.heartspiek.database.entity.Measurement

@Database(entities = [DailyDistance::class, Measurement::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dailyDistanceDao(): DailyDistanceDao
    abstract fun measurementDao(): MeasurementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "heartspiek_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
