package com.oliversolutions.dev.bloodpressurediary.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.oliversolutions.dev.bloodpressurediary.database.BloodPressureDatabase
import com.oliversolutions.dev.bloodpressurediary.repository.BloodPressureRepository
import kotlinx.coroutines.launch

class DataViewModel(application: Application, private val highPressureRepository: BloodPressureRepository) : AndroidViewModel(application) {

    fun clear() {
        viewModelScope.launch {
            highPressureRepository.clear()
        }
    }
}