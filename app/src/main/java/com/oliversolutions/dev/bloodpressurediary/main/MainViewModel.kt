package com.oliversolutions.dev.bloodpressurediary.main

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.oliversolutions.dev.bloodpressurediary.App
import com.oliversolutions.dev.bloodpressurediary.R
import com.oliversolutions.dev.bloodpressurediary.base.BaseViewModel
import com.oliversolutions.dev.bloodpressurediary.database.asDomainModel
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import com.oliversolutions.dev.bloodpressurediary.utils.Result
import com.oliversolutions.dev.bloodpressurediary.database.BloodPressureDTO
import com.oliversolutions.dev.bloodpressurediary.repository.BloodPressureDataSource

@SuppressLint("SimpleDateFormat")
@RequiresApi(Build.VERSION_CODES.O)
class MainViewModel(
    application: Application,
    private val bloodPressureRepository: BloodPressureDataSource
) : BaseViewModel(application) {
    val bloodPressureValues = MutableLiveData<List<BloodPressure>>()
    var fromDate = ""
    var toDate = ""
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
        setAllRecords(fromDate, toDate)
    }

    fun setAllRecords(fromDate: String, toDate: String) {
        showLoading.value = true
        viewModelScope.launch {
            val result = if (fromDate.isEmpty() || toDate.isEmpty()) {
                bloodPressureRepository.getAllRecords()
            } else {
                bloodPressureRepository.getRecordsByDate(fromDate, toDate)
            }
            showLoading.postValue(false)
            when (result) {
                is Result.Success<*> -> {
                    val data = result.data as List<BloodPressureDTO>
                    bloodPressureValues.value = data.asDomainModel()
                }
                is Result.Error ->
                    showSnackBar.value = result.message
            }
            invalidateShowNoData()
        }
    }

    fun updateFilter(item: MenuItem, customFromDate: String? = null, customToDate: String? = null) {
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, 0)
        val today = dateFormat.format(cal.time)
        when (item.itemId) {
            R.id.custom -> {
                fromDate = customFromDate!!
                toDate = customToDate!!
            }
            R.id.all_records -> {
                fromDate = ""
                toDate = ""
            }
            R.id.today -> {
                fromDate = today
                toDate = today
            }
            R.id.yesterday -> {
                cal.add(Calendar.DATE, -1)
                val yesterday = dateFormat.format(cal.time)
                fromDate = yesterday
                toDate = yesterday
            }
            R.id.last_7_days -> {
                cal.add(Calendar.DATE, -7)
                val sevenDaysBefore = dateFormat.format(cal.time)
                fromDate = sevenDaysBefore
                toDate = today
            }
            R.id.last_14_days -> {
                cal.add(Calendar.DATE, -14)
                val fourteenDaysBefore = dateFormat.format(cal.time)
                fromDate = fourteenDaysBefore
                toDate = today
            }
            R.id.last_30_days -> {
                cal.add(Calendar.DATE, -30)
                val thirtyDaysBefore = dateFormat.format(cal.time)
                fromDate = thirtyDaysBefore
                toDate = today
            }
            R.id.last_60_days -> {
                cal.add(Calendar.DATE, -60)
                val sixtyDaysBefore = dateFormat.format(cal.time)
                fromDate = sixtyDaysBefore
                toDate = today
            }
            R.id.last_90_days -> {
                cal.add(Calendar.DATE, -90)
                val ninetyDaysBefore = dateFormat.format(cal.time)
                fromDate = ninetyDaysBefore
                toDate = today
            }
        }
        setAllRecords(fromDate, toDate)
    }

    private fun invalidateShowNoData() {
        showNoData.value = bloodPressureValues.value == null || bloodPressureValues.value!!.isEmpty()
    }
}