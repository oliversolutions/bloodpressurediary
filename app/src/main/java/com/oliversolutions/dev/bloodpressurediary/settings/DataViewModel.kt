package com.oliversolutions.dev.bloodpressurediary.settings

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.oliversolutions.dev.bloodpressurediary.base.BaseViewModel
import com.oliversolutions.dev.bloodpressurediary.repository.BloodPressureDataSource
import kotlinx.coroutines.launch

class DataViewModel(application: Application, private val bloodPressureRepository: BloodPressureDataSource) : BaseViewModel(application) {

    fun clear() {
        viewModelScope.launch {
            bloodPressureRepository.clear()
        }
    }
}