package com.oliversolutions.dev.bloodpressurediary.main

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.oliversolutions.dev.bloodpressurediary.App
import com.oliversolutions.dev.bloodpressurediary.BloodPressure
import com.oliversolutions.dev.bloodpressurediary.BloodPressureFilter
import com.oliversolutions.dev.bloodpressurediary.database.BloodPressureDatabase
import com.oliversolutions.dev.bloodpressurediary.database.asDomainModel
import com.oliversolutions.dev.bloodpressurediary.repository.BloodPressureRepository
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
@RequiresApi(Build.VERSION_CODES.O)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    var bloodPressureValues: LiveData<List<BloodPressure>>
    var fromDate  = ""
    var toDate  = ""

    private val _navigateToSelectedBloodPressure = MutableLiveData<BloodPressure?>()

    val navigateToSelectedBloodPressure: LiveData<BloodPressure?>
        get() = _navigateToSelectedBloodPressure

    private val database = BloodPressureDatabase.getInstance(application)
    private val bloodPressureRepository = BloodPressureRepository(database)

    init {
        if (App.prefs.fromDate != null && App.prefs.toDate != null) {
            fromDate = App.prefs.fromDate!!
            toDate = App.prefs.toDate!!
        } else {
            val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
            val cal = Calendar.getInstance()
            cal.add(Calendar.DATE, 0)
            val today = dateFormat.format(cal.time)
            cal.add(Calendar.DATE, -7)
            val sevenDaysBefore = dateFormat.format(cal.time)
            fromDate = sevenDaysBefore
            toDate = today
        }
        bloodPressureValues = when (fromDate.isBlank() || toDate.isBlank()) {
            true -> {
                Transformations.map(bloodPressureRepository.getAllRecords()) {
                    it.asDomainModel()
                }
            }
            false -> {
                Transformations.map(bloodPressureRepository.getRecordsByDate(fromDate, toDate)) {
                    it.asDomainModel()
                }
            }
        }

    }

    private fun setAllRecords() {
        bloodPressureValues = Transformations.map(bloodPressureRepository.getAllRecords()) {
            it.asDomainModel()
        }
    }

    private fun setRecordsByDate(fromDate: String, toDate: String) {
        bloodPressureValues = Transformations.map(bloodPressureRepository.getRecordsByDate(fromDate, toDate)) {
            it.asDomainModel()
        }
    }

    fun updateFilter(filter: BloodPressureFilter, selectedFromDate: String? = null, selectedToDate: String? = null) : Boolean {
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, 0)
        val today = dateFormat.format(cal.time)
        when (filter) {
            BloodPressureFilter.SHOW_ALL -> {
                setAllRecords()
                fromDate = ""
                toDate = ""
                return true
            }
            BloodPressureFilter.SHOW_TODAY -> {
                fromDate = today
                toDate = today
            }
            BloodPressureFilter.SHOW_YESTERDAY -> {
                cal.add(Calendar.DATE, -1)
                val yesterday = dateFormat.format(cal.time)
                fromDate = yesterday
                toDate = yesterday
            }
            BloodPressureFilter.SHOW_LAST_7_DAYS -> {
                cal.add(Calendar.DATE, -7)
                val sevenDaysBefore = dateFormat.format(cal.time)
                fromDate = sevenDaysBefore
                toDate = today
            }
            BloodPressureFilter.SHOW_LAST_14_DAYS -> {
                cal.add(Calendar.DATE, -14)
                val fourteenDaysBefore = dateFormat.format(cal.time)
                fromDate = fourteenDaysBefore
                toDate = today
            }
            BloodPressureFilter.SHOW_LAST_30_DAYS -> {
                cal.add(Calendar.DATE, -30)
                val thirtyDaysBefore = dateFormat.format(cal.time)
                fromDate = thirtyDaysBefore
                toDate = today
            }
            BloodPressureFilter.SHOW_LAST_60_DAYS -> {
                cal.add(Calendar.DATE, -60)
                val sixtyDaysBefore = dateFormat.format(cal.time)
                fromDate = sixtyDaysBefore
                toDate = today
            }
            BloodPressureFilter.SHOW_LAST_90_DAYS -> {
                cal.add(Calendar.DATE, -90)
                val ninetyDaysBefore = dateFormat.format(cal.time)
                fromDate = ninetyDaysBefore
                toDate = today
            }
            BloodPressureFilter.SHOW_CUSTOM -> {
                fromDate = selectedFromDate!!
                toDate = selectedToDate!!
            }

        }

        setRecordsByDate(fromDate, toDate)
        return true
    }

    fun displayEditBloodPressure(bloodPressure: BloodPressure?) {
        _navigateToSelectedBloodPressure.value = bloodPressure
    }
}