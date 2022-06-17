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

const val DEFAULT_SYSTOLIC = 120
const val DEFAULT_DIASTOLIC = 65
const val DEFAULT_PULSE = 60

class BloodPressureEditViewModel(bloodPressure: BloodPressure?, application: Application) : AndroidViewModel(application) {

    val systolic = MutableLiveData(DEFAULT_SYSTOLIC)
    val diastolic = MutableLiveData(DEFAULT_DIASTOLIC)
    val pulse = MutableLiveData(DEFAULT_PULSE)
    val notes = MutableLiveData<String>()
    val insertMode = bloodPressure == null
    val creationDate = MutableLiveData(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)
        } else {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(System.currentTimeMillis()))
        }
    )
    val creationTime = MutableLiveData(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        } else {
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(System.currentTimeMillis()))
        }
    )
    private var highPressureId: Long = 0
    private val database = BloodPressureDatabase.getInstance(application)
    private val highPressureRepository = BloodPressureRepository(database)

    init {
        if (bloodPressure != null) {
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
    }

    fun deleteRecord() {
        viewModelScope.launch {
            highPressureRepository.deleteRecord(highPressureId.toInt())
        }
    }

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