package com.example.heartspiek

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.heartspiek.manager.StreakManager
import com.example.heartspiek.service.LocationService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // Navigation Variable
    private lateinit var btnShowStats: ImageView
    private lateinit var btnShowHome: ImageView

    // Streak Tracking Variable
    private lateinit var streakManager: StreakManager
    private lateinit var streakTextView: TextView

    // GPS Tracking Variable
    private lateinit var tvDistance: TextView
    private lateinit var tvInterval: TextView
    private lateinit var btnShowMap: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastLocation: Location? = null
    private var totalDistance = 0f
    private val MIN_DISTANCE_THRESHOLD = 5
    private val MAX_ACCURACY = 10f
    private val PREFS_NAME = "DistanceTrackerPrefs"
    private val DISTANCE_KEY = "totalDistance"
    private val GPS_INTERVAL = 30 * 1000L // 30 Sekunden
    private var countDownTimer: CountDownTimer? = null
    private val handler = Handler(Looper.getMainLooper())

    // Alarm Variable
    private lateinit var btnSetAlarm: Button
    private lateinit var txtAlarmName: TextView
    private lateinit var txtAlarmTime: TextView

    // Bodymass Tracking Variable
    private lateinit var btnSetBodyMass: Button
    private lateinit var txtShowBodyMass: TextView
    private lateinit var txtBodyMassTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Streak Tracking
        streakManager = StreakManager(this)
        streakTextView = findViewById(R.id.streakTextView)
        val streak = streakManager.updateStreak()
        streakTextView.text = streak.toString()

        // GPS Tracking
        tvDistance = findViewById(R.id.tvDistance)
        tvInterval = findViewById(R.id.tvInterval)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
        } else {
            loadDistance()
            requestLocationUpdates()
            startIntervalTimer()
            startLocationService()
        }
        btnShowMap = findViewById(R.id.btnShowMap)
        btnShowMap.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        // Alarm View
        btnSetAlarm = findViewById(R.id.btnSetAlarm)
        btnSetAlarm.setOnClickListener {
            val intent = Intent(this, SetAlarmActivity::class.java)
            startActivity(intent)
        }
        checkForActiveAlarm()
        txtAlarmName = findViewById(R.id.txtAlarmName)
        txtAlarmTime = findViewById(R.id.txtAlarmTime)
        loadAlarmDetails()

        // Bodymass View
        btnSetBodyMass = findViewById(R.id.btnSetBodyMass)
        btnSetBodyMass.setOnClickListener {
            val intent = Intent(this, SetBodymassActivity::class.java)
            startActivity(intent)
        }
        txtBodyMassTitle = findViewById(R.id.txtBodyMassTitle)
        txtShowBodyMass = findViewById(R.id.txtShowBodyMass)
        loadBodyMass()

        // Navigation
        btnShowStats = findViewById(R.id.btnShowStats)
        btnShowStats.setOnClickListener {
            val intent = Intent(this, StatisticsActivity::class.java)
            startActivity(intent)
        }
        btnShowHome = findViewById(R.id.btnShowHome)
        btnShowHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    // GPS
    private fun startCountdown(timeInMillis: Long) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(timeInMillis, 100) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val milliseconds = (millisUntilFinished % 1000) / 10
                tvInterval.text = "Nächster GPS-Request: $seconds.$milliseconds s"
            }

            override fun onFinish() {
                tvInterval.text = "GPS-Request läuft..."
            }
        }.start()
    }
    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, GPS_INTERVAL).build()
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val newLocation = locationResult.lastLocation ?: return

                if (newLocation.accuracy > MAX_ACCURACY) return

                if (lastLocation != null) {
                    val distance = lastLocation!!.distanceTo(newLocation)
                    if (distance > MIN_DISTANCE_THRESHOLD) {
                        totalDistance += distance
                        tvDistance.text = String.format(Locale.getDefault(), "%.2f km", totalDistance / 1000)
                        saveDistance()
                    }
                }

                lastLocation = newLocation
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }
    private fun startIntervalTimer() {
        handler.post(object : Runnable {
            override fun run() {
                startCountdown(GPS_INTERVAL)
                handler.postDelayed(this, GPS_INTERVAL)
            }
        })
    }
    private fun startLocationService() {
        val serviceIntent = Intent(this, LocationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }
    private fun saveDistance() {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat(DISTANCE_KEY, totalDistance)
        editor.apply()
    }
    private fun loadDistance() {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        totalDistance = sharedPreferences.getFloat(DISTANCE_KEY, 0f)
        tvDistance.text = String.format(Locale.getDefault(),"%.2f km", totalDistance / 1000)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1001 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    requestLocationUpdates()
                    loadDistance()
                    startIntervalTimer()
                    startLocationService()
                } else {
                    Toast.makeText(this, "GPS-Berechtigung erforderlich, um den Standort zu verfolgen.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Alarm
    private fun loadAlarmDetails() {
        val sharedPreferences = getSharedPreferences("AlarmPrefs", MODE_PRIVATE)
        val alarmName = sharedPreferences.getString("ALARM_NAME", "") ?: ""
        val alarmTime = sharedPreferences.getString("ALARM_TIME", "") ?: ""

        if (alarmName != "" && alarmTime != "") {
            updateAlarmDetails(alarmName, alarmTime)
        }
    }
    private fun updateAlarmDetails(alarmName: String, alarmTime: String) {
        txtAlarmName.text = "Name: $alarmName"
        txtAlarmTime.text = "Startet um: $alarmTime"
    }
    private fun checkForActiveAlarm() {
        val sharedPref = getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
        val alarmActive = sharedPref.getBoolean("alarm_active", false)

        if (alarmActive) {
            with(sharedPref.edit()) {
                putBoolean("alarm_active", false)
                apply()
            }
            val intent = Intent(this, AlarmActivity::class.java)
            startActivity(intent)
        }
    }

    // Bodymass
    private fun loadBodyMass() {
        val sharedPreferences = getSharedPreferences("BodyPrefs", MODE_PRIVATE)
        val weight = sharedPreferences.getString("BODYMASS_WEIGHT", "") ?: ""
        val height = sharedPreferences.getString("BODYMASS_HEIGHT", "") ?: ""

        if (weight != "" && height != "") {
            updateBodymassDetails(weight, height)
        }
    }
    private fun updateBodymassDetails(weight: String, height: String) {
        txtBodyMassTitle.text = "Gewicht und Höhe heute:"
        txtShowBodyMass.text = "$height cm mit $weight kg"
    }
}