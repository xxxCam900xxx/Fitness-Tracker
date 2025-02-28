package com.example.heartspiek

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
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

    // Spinner Variable
    private lateinit var statisticsSpinner: Spinner

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

        // Spinner
        statisticsSpinner = findViewById(R.id.statistics_dropdown)
        val adapter = ArrayAdapter.createFromResource(this, R.array.statistics_options, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        statisticsSpinner.adapter = adapter
        statisticsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> showLast7Days()
                    1 -> showLast30Days()
                    2 -> showAllTime()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                showLast7Days()
            }
        }

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

    // Data
    private fun showLast7Days() {
        viewModelMeasurements.allMeasurements.observe(this, Observer { measurements ->
            val last7Measurements = measurements.takeLast(7)
            updateChart(lineChartHeight, last7Measurements.mapIndexed { index, m -> Entry(index.toFloat(), m.height) }, "Höhe")
            updateChart(lineChartWeight, last7Measurements.mapIndexed { index, m -> Entry(index.toFloat(), m.weight) }, "Gewicht")
        })
        viewModelDistance.allDistance.observe(this, Observer { distances ->
            val last7Distances = distances.takeLast(7)
            updateChart(lineChartDistance, last7Distances.mapIndexed { index, m -> Entry(index.toFloat(), m.distance) }, "Strecke")
        })
    }
    private fun showLast30Days() {
        viewModelMeasurements.allMeasurements.observe(this, Observer { measurements ->
            val last30Measurements = measurements.takeLast(30)
            updateChart(lineChartHeight, last30Measurements.mapIndexed { index, m -> Entry(index.toFloat(), m.height) }, "Höhe")
            updateChart(lineChartWeight, last30Measurements.mapIndexed { index, m -> Entry(index.toFloat(), m.weight) }, "Gewicht")
        })
        viewModelDistance.allDistance.observe(this, Observer { distances ->
            val last30Distances = distances.takeLast(30)
            updateChart(lineChartDistance, last30Distances.mapIndexed { index, m -> Entry(index.toFloat(), m.distance) }, "Strecke")
        })
    }
    private fun showAllTime() {
        viewModelMeasurements.allMeasurements.observe(this, Observer { measurements ->
            updateChart(lineChartHeight, measurements.mapIndexed { index, m -> Entry(index.toFloat(), m.height) }, "Höhe")
            updateChart(lineChartWeight, measurements.mapIndexed { index, m -> Entry(index.toFloat(), m.weight) }, "Gewicht")
        })
        viewModelDistance.allDistance.observe(this, Observer { distances ->
            updateChart(lineChartDistance, distances.mapIndexed { index, m -> Entry(index.toFloat(), m.distance) }, "Strecke")
        })
    }

    // Chart
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