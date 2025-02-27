package com.example.heartspiek.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.location.Location
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.example.heartspiek.database.entity.DailyDistance
import com.example.heartspiek.uitils.DailyDistanceViewModel
import com.google.android.gms.location.*

class LocationService : Service() {

    // GPS Tracking
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastLocation: Location? = null
    private var totalDistance = 0f
    private val MIN_DISTANCE_THRESHOLD = 5 // Mindestdistanz in Metern
    private val MAX_ACCURACY = 5f // Maximale akzeptierte GPS-Genauigkeit in Metern

    // Datenhaltung
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: DailyDistanceViewModel
    val distanceLiveData = MutableLiveData<Float>() // LiveData für UI-Updates

    companion object {
        const val PREFS_NAME = "DistanceTrackerPrefs"
        const val DISTANCE_KEY = "totalDistance"
        const val NOTIFICATION_ID = 1
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val newLocation = locationResult.lastLocation ?: return

            if (newLocation.accuracy > MAX_ACCURACY) return // Schlechte GPS-Genauigkeit ignorieren
            if (lastLocation != null) {
                val distance = lastLocation!!.distanceTo(newLocation)
                if (distance > MIN_DISTANCE_THRESHOLD) {
                    totalDistance += distance
                    saveDistance()
                    distanceLiveData.postValue(totalDistance)
                    Log.d("LocationService", "Bewegung erkannt: $distance m, Gesamt: $totalDistance m")
                }
            }
            lastLocation = newLocation
        }
    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        totalDistance = sharedPreferences.getFloat(DISTANCE_KEY, 0f)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // TODO Notification
        requestLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 30000).build()
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun saveDistance() {
        val today = getCurrentDateTimestamp()
        val dailyDistance = DailyDistance(today, totalDistance)

        viewModel.updateDistance(dailyDistance)
        sharedPreferences.edit().putFloat(DISTANCE_KEY, totalDistance).apply()
    }

    // TODO Notification

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun getCurrentDateTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}