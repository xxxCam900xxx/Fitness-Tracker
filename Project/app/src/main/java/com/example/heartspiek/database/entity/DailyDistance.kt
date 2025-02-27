package com.example.heartspiek.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_distance")
data class DailyDistance(
    @PrimaryKey val date: Long,
    val distance: Float
)
