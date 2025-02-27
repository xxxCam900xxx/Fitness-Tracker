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
import kotlinx.coroutines.launch

class DailyDistanceViewModel(application: Application) : AndroidViewModel(application) {
    private val dailyDistanceDao: DailyDistanceDao = AppDatabase.getDatabase(application).dailyDistanceDao()

    val allMeasurements = dailyDistanceDao.getAllDistance().asLiveData()

    fun getDistanceForToday(): LiveData<DailyDistance?> {
        val today = getCurrentDateTimestamp()
        return dailyDistanceDao.getDistanceForDate(today).asLiveData()
    }

    fun updateDistance(dailyDistance: DailyDistance) {
        viewModelScope.launch {
            dailyDistanceDao.insertOrUpdateDistance(dailyDistance)
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
