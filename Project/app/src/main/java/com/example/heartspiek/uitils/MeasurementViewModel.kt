package com.example.heartspiek.uitils

import android.app.Application
import android.icu.util.Calendar
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.heartspiek.database.AppDatabase
import com.example.heartspiek.database.dao.MeasurementDao
import com.example.heartspiek.database.entity.Measurement

class MeasurementViewModel(application: Application) : AndroidViewModel(application) {
    private val measurementDao: MeasurementDao = AppDatabase.getDatabase(application).measurementDao()

    val allMeasurements = measurementDao.getAllMeasurements().asLiveData()

    fun saveMeasurement(height: Float, weight: Float) {
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

            val existingMeasurement = measurementDao.getMeasurementForDate(date)
            val measurement = Measurement(date = date, height = height, weight = weight)

            if (existingMeasurement != null) {
                measurementDao.insert(measurement.copy(date = existingMeasurement.date))
            } else {
                measurementDao.insert(measurement)
            }
        }
    }

}