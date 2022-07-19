package com.oliversolutions.dev.bloodpressurediary.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oliversolutions.dev.bloodpressurediary.repository.BloodPressureDataSource


class BloodPressureViewModelFactory(
    private val bloodPressure: BloodPressure?,
    private val application: Application,
    private val bloodPressureRepository: BloodPressureDataSource) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BloodPressureViewModel::class.java)) {
            return BloodPressureViewModel(bloodPressure, application, bloodPressureRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
