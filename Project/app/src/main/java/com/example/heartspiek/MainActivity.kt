package com.example.heartspiek

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.heartspiek.manager.StreakManager

class MainActivity : AppCompatActivity() {

    // Streak Tracking Variable
    private lateinit var streakManager: StreakManager
    private lateinit var streakTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Streak Tracking
        streakManager = StreakManager(this)
        streakTextView = findViewById(R.id.streakTextView)
        val streak = streakManager.updateStreak()
        streakTextView.text = streak.toString()

    }
}