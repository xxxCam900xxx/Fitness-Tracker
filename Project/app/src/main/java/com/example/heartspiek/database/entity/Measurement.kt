package com.example.heartspiek.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "measurements")
data class Measurement(
    @PrimaryKey val date: Long,
    val height: Float,
    val weight: Float,
)