package com.oliversolutions.dev.bloodpressurediary.settings

import android.app.Application
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.oliversolutions.dev.bloodpressurediary.App
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class RemindersViewModel(application: Application) : AndroidViewModel(application) {
    var measurementReminderTime = MutableLiveData<String>()
    var medicationReminderTime = MutableLiveData<String>()
    var medicationSecondReminderTime = MutableLiveData<String>()
    var medicationThirdReminderTime = MutableLiveData<String>()
    init {
        val currentTime: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        } else {
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(System.currentTimeMillis()))
        }

        if (App.prefs.measurementReminderTime.isNullOrEmpty()) {
            measurementReminderTime.postValue(currentTime)
        } else {
            measurementReminderTime.postValue(App.prefs.measurementReminderTime)
        }
        if (App.prefs.medicationReminderTime.isNullOrEmpty()) {
            medicationReminderTime.postValue(currentTime)
        } else {
            medicationReminderTime.postValue(App.prefs.medicationReminderTime)
        }

        if (App.prefs.medicationSecondReminderTime.isNullOrEmpty()) {
            medicationSecondReminderTime.postValue(currentTime)
        } else {
            medicationSecondReminderTime.postValue(App.prefs.medicationSecondReminderTime)
        }

        if (App.prefs.medicationThirdReminderTime.isNullOrEmpty()) {
            medicationThirdReminderTime.postValue(currentTime)
        } else {
            medicationThirdReminderTime.postValue(App.prefs.medicationThirdReminderTime)
        }
    }
}