package com.oliversolutions.dev.bloodpressurediary.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oliversolutions.dev.bloodpressurediary.BloodPressure
import com.oliversolutions.dev.bloodpressurediary.repository.BloodPressureRepository

class BloodPressureEditViewModelFactory(private val bloodPressure: BloodPressure?, private val application: Application, private val highPressureRepository: BloodPressureRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BloodPressureEditViewModel::class.java)) {
            return BloodPressureEditViewModel(bloodPressure, application, highPressureRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
