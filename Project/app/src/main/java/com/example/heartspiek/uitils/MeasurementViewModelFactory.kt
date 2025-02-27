package com.example.heartspiek.uitils

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MeasurementViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MeasurementViewModel::class.java)) {
            return MeasurementViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}