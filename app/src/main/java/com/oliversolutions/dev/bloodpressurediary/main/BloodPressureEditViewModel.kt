package com.oliversolutions.dev.bloodpressurediary.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.oliversolutions.dev.bloodpressurediary.BloodPressure
import com.oliversolutions.dev.bloodpressurediary.database.BloodPressureDTO
import com.oliversolutions.dev.bloodpressurediary.database.BloodPressureDatabase
import com.oliversolutions.dev.bloodpressurediary.repository.BloodPressureRepository
import kotlinx.coroutines.launch

class BloodPressureEditViewModel(bloodPressure: BloodPressure, application: Application) : AndroidViewModel(application) {

    val systolic = MutableLiveData<Int>()
    val diastolic = MutableLiveData<Int>()
    val pulse = MutableLiveData<Int>()
    val notes = MutableLiveData<String>()
    val creationDate = MutableLiveData<String>()
    val creationTime = MutableLiveData<String>()
    private var highPressureId: Long

    private val database = BloodPressureDatabase.getInstance(application)
    private val highPressureRepository = BloodPressureRepository(database)

    init {
        systolic.value = bloodPressure.systolic?.toInt()
        diastolic.value = bloodPressure.diastolic?.toInt()
        pulse.value = bloodPressure.pulse?.toInt()
        if (bloodPressure.notes != null) {
            notes.value = bloodPressure.notes!!
        }
        creationDate.value = bloodPressure.creationDate!!
        creationTime.value = bloodPressure.creationTime!!
        highPressureId = bloodPressure.id
    }

    fun deleteRecord() {
        viewModelScope.launch {
            highPressureRepository.deleteRecord(highPressureId.toInt())
        }
    }

    /**
     * Save the reminder to the data source
     */
    fun editHighPressure() {
        viewModelScope.launch {
            highPressureRepository.editHighPressure(
                BloodPressureDTO(
                    systolic.value?.toDouble(),
                    diastolic.value?.toDouble(),
                    pulse.value?.toDouble(),
                    notes.value,
                    creationTime.value,
                    creationDate.value,
                    null,
                    null,
                    null,
                    highPressureId
                )
            )
        }
    }
}