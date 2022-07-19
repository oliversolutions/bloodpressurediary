package com.oliversolutions.dev.bloodpressurediary.main

import android.app.Application
import android.os.Build
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.oliversolutions.dev.bloodpressurediary.R
import com.oliversolutions.dev.bloodpressurediary.base.BaseViewModel
import com.oliversolutions.dev.bloodpressurediary.base.NavigationCommand
import com.oliversolutions.dev.bloodpressurediary.database.BloodPressureDTO
import com.oliversolutions.dev.bloodpressurediary.repository.BloodPressureDataSource
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

const val DEFAULT_SYSTOLIC = 120
const val DEFAULT_DIASTOLIC = 65
const val DEFAULT_PULSE = 60

class BloodPressureViewModel(val bloodPressure: BloodPressure?, val app: Application, private val bloodPressureRepository: BloodPressureDataSource) : BaseViewModel(app) {

    val systolic = MutableLiveData(DEFAULT_SYSTOLIC)
    val diastolic = MutableLiveData(DEFAULT_DIASTOLIC)
    val pulse = MutableLiveData(DEFAULT_PULSE)
    val notes = MutableLiveData<String>()
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
        }
    }

    fun deleteRecord() {
        viewModelScope.launch {
            bloodPressure?.let {
                bloodPressureRepository.deleteRecord(it.id)
                showToast.value = app.getString(R.string.record_removed)
            }
            navigationCommand.value = NavigationCommand.To(BloodPressureFragmentDirections.actionHighPressureEditFragmentToNavigationHome())
        }
    }

    fun createNewBloodPressure(bloodPressure: BloodPressure) {
        showLoading.value = true
        viewModelScope.launch {
            bloodPressureRepository.createNewBloodPressure(
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
            showLoading.value = false
            showToast.value = app.getString(R.string.blood_pressure_saved)
            navigationCommand.value = NavigationCommand.To(BloodPressureFragmentDirections.actionHighPressureEditFragmentToNavigationHome())

        }
    }

    fun editBloodPressure() {
        viewModelScope.launch {
            bloodPressure?.let {
                bloodPressureRepository.editBloodPressure(
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
                        bloodPressure.id
                    )
                )
            }
            showToast.value = app.getString(R.string.blood_pressure_saved)
            navigationCommand.value = NavigationCommand.To(BloodPressureFragmentDirections.actionHighPressureEditFragmentToNavigationHome())
        }
    }
}