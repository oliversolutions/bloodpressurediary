package com.oliversolutions.dev.bloodpressurediary.main

import android.app.Application
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.oliversolutions.dev.bloodpressurediary.BloodPressure
import com.oliversolutions.dev.bloodpressurediary.database.BloodPressureDTO
import com.oliversolutions.dev.bloodpressurediary.database.BloodPressureDatabase
import com.oliversolutions.dev.bloodpressurediary.repository.BloodPressureRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class BloodPressureSaveViewModel(application: Application) : AndroidViewModel(application) {

    val systolic = MutableLiveData(120)
    val diastolic = MutableLiveData(65)
    val pulse = MutableLiveData(60)
    val notes = MutableLiveData<String>()
    val currentDate = MutableLiveData(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)
        } else {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(System.currentTimeMillis()))
        }
    )
    val currentTime = MutableLiveData(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        } else {
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(System.currentTimeMillis()))
        }
    )
    private val database = BloodPressureDatabase.getInstance(application)
    private val highPressureRepository = BloodPressureRepository(database)

    /**
     * Save the reminder to the data source
     */
    fun saveHighPressure(bloodPressure: BloodPressure) {
        viewModelScope.launch {
            highPressureRepository.saveHighPressure(
                BloodPressureDTO(
                    bloodPressure.systolic,
                    bloodPressure.diastolic,
                    bloodPressure.pulse,
                    bloodPressure.notes,
                    bloodPressure.creationTime,
                    bloodPressure.creationDate,
                    null,
                    null,
                    null
                )
            )
        }
    }
}