package com.example.heartspiek.uitils

import android.app.Application
import android.icu.util.Calendar
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.heartspiek.database.AppDatabase
import com.example.heartspiek.database.dao.DailyDistanceDao
import com.example.heartspiek.database.entity.DailyDistance
import com.example.heartspiek.database.entity.Measurement
import kotlinx.coroutines.launch

class DailyDistanceViewModel(application: Application) : AndroidViewModel(application) {
    private val dailyDistanceDao: DailyDistanceDao = AppDatabase.getDatabase(application).dailyDistanceDao()

    val allDistance = dailyDistanceDao.getAllDistance().asLiveData()

    fun getDistanceForToday(): DailyDistance? {
        val today = getCurrentDateTimestamp()
        val distance = dailyDistanceDao.getDistanceForDate(today)
        return distance
    }

    fun updateDistance(dailyDistance: DailyDistance) {
        viewModelScope.launch {
            val date = System.currentTimeMillis().let { timestamp ->
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = timestamp
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                calendar.timeInMillis
            }

            val existingMeasurement = dailyDistanceDao.getDistanceForDate(date)
            val distance = DailyDistance(date = date, distance = dailyDistance.distance)

            if (existingMeasurement != null) {
                dailyDistanceDao.insert(distance.copy(date = existingMeasurement.date))
            } else {
                dailyDistanceDao.insert(distance)
            }
        }
    }

    private fun getCurrentDateTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
