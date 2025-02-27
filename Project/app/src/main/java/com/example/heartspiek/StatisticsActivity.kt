package com.example.heartspiek

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class StatisticsActivity : AppCompatActivity() {

    // Navigation Variable
    private lateinit var btnShowStats: ImageView
    private lateinit var btnShowHome: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_statistics)

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
}