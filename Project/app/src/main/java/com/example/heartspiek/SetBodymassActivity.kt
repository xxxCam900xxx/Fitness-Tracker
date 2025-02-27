package com.example.heartspiek

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.heartspiek.database.entity.Measurement
import com.example.heartspiek.uitils.MeasurementViewModel
import com.example.heartspiek.uitils.MeasurementViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SetBodymassActivity : AppCompatActivity() {
    private val viewModel: MeasurementViewModel by viewModels {
        MeasurementViewModelFactory(application)
    }

    // Navigation Variable
    private lateinit var btnShowStats: ImageView
    private lateinit var btnShowHome: ImageView

    // Date Variable
    private lateinit var tvcurrentDate: TextView

    // Bodymass Variable
    private lateinit var buttonSave: Button
    private lateinit var editTextHeight: EditText
    private lateinit var editTextWeight: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_bodymass)

        // Date
        tvcurrentDate = findViewById(R.id.tvcurrentDate)
        tvcurrentDate.text = getCurrentDate()

        // Bodymass
        buttonSave = findViewById(R.id.button_save)
        editTextHeight = findViewById(R.id.editText_height)
        editTextWeight = findViewById(R.id.editText_weight)
        buttonSave.setOnClickListener {
            val height = editTextHeight.text.toString().toFloatOrNull()
            val weight = editTextWeight.text.toString().toFloatOrNull()

            if (height != null && weight != null) {
                lifecycleScope.launch {
                    viewModel.saveMeasurement(height, weight)
                    editTextHeight.text.clear()
                    editTextWeight.text.clear()
                }
            }

            val sharedPreferences = getSharedPreferences("BodyPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("BODYMASS_WEIGHT", weight.toString())
            editor.putString("BODYMASS_HEIGHT", height.toString())
            editor.apply()

            val mainActivityIntent = Intent(this, MainActivity::class.java)
            if (weight != null) {
                mainActivityIntent.putExtra("BODYMASS_WEIGHT", weight.toString())
            } else {
                mainActivityIntent.putExtra("BODYMASS_WEIGHT", "0")
            }
            if (height != null) {
                mainActivityIntent.putExtra("BODYMASS_HEIGHT", height.toInt().toString())
            } else {
                mainActivityIntent.putExtra("BODYMASS_HEIGHT", "0")
            }
            startActivity(mainActivityIntent)
        }

    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }
}