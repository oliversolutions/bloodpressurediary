package com.oliversolutions.dev.bloodpressurediary.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oliversolutions.dev.bloodpressurediary.BloodPressure

class BloodPressureEditViewModelFactory(private val bloodPressure: BloodPressure?, private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BloodPressureEditViewModel::class.java)) {
            return BloodPressureEditViewModel(bloodPressure, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
