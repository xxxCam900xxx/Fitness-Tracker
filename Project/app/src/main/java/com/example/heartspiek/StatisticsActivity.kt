package com.example.heartspiek

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.heartspiek.manager.StreakManager
import com.example.heartspiek.uitils.DailyDistanceViewModel
import com.example.heartspiek.uitils.MeasurementViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class StatisticsActivity : AppCompatActivity() {

    // ViewModel Variable
    private val viewModelMeasurements: MeasurementViewModel by viewModels()
    private val viewModelDistance: DailyDistanceViewModel by viewModels()

    // Navigation Variable
    private lateinit var btnShowStats: ImageView
    private lateinit var btnShowHome: ImageView

    // Streak Tracking Variable
    private lateinit var streakManager: StreakManager
    private lateinit var streakTextView: TextView

    // Stats Variable
    private lateinit var lineChartHeight: LineChart
    private lateinit var lineChartWeight: LineChart
    private lateinit var lineChartDistance: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        // Stats
        lineChartHeight = findViewById(R.id.lineChartHeight)
        lineChartWeight = findViewById(R.id.lineChartWeight)
        lineChartDistance = findViewById(R.id.lineChartDistance)
        configureChart(lineChartHeight)
        configureChart(lineChartWeight)
        configureChart(lineChartDistance)

        // Stats Observe
        viewModelMeasurements.allMeasurements.observe(this, Observer { measurements ->
            updateChart(lineChartHeight, measurements.mapIndexed { index, m -> Entry(index.toFloat(), m.height) }, "Höhe")
            updateChart(lineChartWeight, measurements.mapIndexed { index, m -> Entry(index.toFloat(), m.weight) }, "Gewicht")
        })
        viewModelDistance.getDistanceForToday().observe(this, Observer { distances ->
            distances?.let {
                updateChart(lineChartDistance, listOf(Entry(0f, it.distance)), "Strecke")
            }
        })

        // Streak Tracking
        streakManager = StreakManager(this)
        streakTextView = findViewById(R.id.streakTextView)
        val streak = streakManager.updateStreak()
        streakTextView.text = streak.toString()

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

    // Stats
    private fun configureChart(chart: LineChart) {
        chart.axisLeft.setDrawGridLines(false)
        chart.axisRight.isEnabled = true
        chart.axisRight.textColor = android.graphics.Color.WHITE
    }
    private fun updateChart(chart: LineChart, entries: List<Entry>, label: String) {
        val dataSet = LineDataSet(entries, label)
        dataSet.color = android.graphics.Color.RED
        dataSet.valueTextColor = android.graphics.Color.WHITE

        chart.data = LineData(dataSet)
        chart.invalidate()
    }
}