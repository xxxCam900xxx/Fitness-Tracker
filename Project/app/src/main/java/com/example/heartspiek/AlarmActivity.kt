package com.example.heartspiek

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.heartspiek.manager.StreakManager
import com.example.heartspiek.receiver.AlarmReceiver
import kotlin.random.Random

class AlarmActivity : AppCompatActivity() {

    // Navigation Variable
    private lateinit var btnShowStats: ImageView
    private lateinit var btnShowHome: ImageView

    // Streak Tracking Variable
    private lateinit var streakManager: StreakManager
    private lateinit var streakTextView: TextView

    // Random Number Variable
    private lateinit var tvRndLiegestuetze: TextView

    // Alarm Variable
    private lateinit var txtAlarmName: TextView
    private lateinit var txtAlarmTime: TextView
    private lateinit var btnDoneAlarm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_alarm)

        // Random anzahl Liegestützen
        tvRndLiegestuetze = findViewById<TextView>(R.id.tvRndLiegestuetze)
        val randomNumber = Random.nextInt(5, 30)
        tvRndLiegestuetze.text = "$randomNumber"

        // Streak Tracking
        streakManager = StreakManager(this)
        streakTextView = findViewById(R.id.streakTextView)
        val streak = streakManager.updateStreak()
        streakTextView.text = streak.toString()

        // Alarm
        val sharedPref = getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
        val alarmActive = sharedPref.getBoolean("alarm_active", false)
        if (alarmActive) {
            with(sharedPref.edit()) {
                putBoolean("alarm_active", false)
                apply()
            }
        }
        txtAlarmName = findViewById(R.id.txtAlarmName)
        txtAlarmTime = findViewById(R.id.txtAlarmTime)
        val sharedPreferences = getSharedPreferences("AlarmPrefs", MODE_PRIVATE)
        val alarmName = sharedPreferences.getString("ALARM_NAME", "Kein Alarm gesetzt")
        val alarmTime = sharedPreferences.getString("ALARM_TIME", "Keine Zeit gesetzt")
        if (alarmName != null) {
            if (alarmTime != null) {
                updateAlarmDetails(alarmName, alarmTime)
            }
        }
        val btnDoneAlarm = findViewById<Button>(R.id.btnDoneAlarm)
        btnDoneAlarm.setOnClickListener {
            AlarmReceiver.stopAlarm(this);
        }

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

    // Alarm
    fun updateAlarmDetails(alarmName: String, alarmTime: String) {
        txtAlarmName.text = "$alarmName"
        txtAlarmTime.text = "$alarmTime"
    }
}