package com.oliversolutions.dev.bloodpressurediary.statistics

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.oliversolutions.dev.bloodpressurediary.*
import com.oliversolutions.dev.bloodpressurediary.base.BaseViewModel
import com.oliversolutions.dev.bloodpressurediary.database.BloodPressureDTO
import com.oliversolutions.dev.bloodpressurediary.database.asDomainModel
import com.oliversolutions.dev.bloodpressurediary.main.BloodPressure
import com.oliversolutions.dev.bloodpressurediary.repository.BloodPressureDataSource
import com.oliversolutions.dev.bloodpressurediary.utils.BloodPressureDiagnosis
import com.oliversolutions.dev.bloodpressurediary.utils.Result
import com.oliversolutions.dev.bloodpressurediary.utils.getBloodPressureDiagnosis
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
@RequiresApi(Build.VERSION_CODES.O)
class StatisticViewModel(application: Application, private val bloodPressureRepository: BloodPressureDataSource) : BaseViewModel(application) {

    var bloodPressureStatisticData = BloodPressureStatistic()
    val bloodPressureValues = MutableLiveData<List<BloodPressure>>()
    var fromDate  = ""
    var toDate  = ""
    var numRecords = 0

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

    fun setBloodPressureStatisticData(bloodPressureList: List<BloodPressure>) {

        var total = 0f
        var stage1 = 0f
        var stage2 = 0f
        var normal = 0f
        var prehypertension = 0f
        var hypotension = 0f

        var totalSys = 0.0
        var totalDia = 0.0
        var totalPulse = 0.0

        val setOfSys = mutableSetOf<Double>()
        val setOfDia = mutableSetOf<Double>()
        val setOfPulse = mutableSetOf<Double>()

        for (bloodPressure in bloodPressureList) {
            totalSys += bloodPressure.systolic!!
            totalDia += bloodPressure.diastolic!!
            totalPulse += bloodPressure.pulse!!
            setOfSys.add(bloodPressure.systolic)
            setOfDia.add(bloodPressure.diastolic)
            setOfPulse.add(bloodPressure.pulse)

            when (getBloodPressureDiagnosis(bloodPressure.systolic.toInt(), bloodPressure.diastolic.toInt())) {
                BloodPressureDiagnosis.Stage2 -> {
                    stage2++
                }
                BloodPressureDiagnosis.Stage1 -> {
                    stage1++
                }
                BloodPressureDiagnosis.Pre -> {
                    prehypertension++
                }
                BloodPressureDiagnosis.Normal -> {
                    normal++
                }
                BloodPressureDiagnosis.Hypotension -> {
                    hypotension++
                }
            }
            total++
        }

        bloodPressureStatisticData =
            BloodPressureStatistic(
                ((stage2 * 100) / total) / 100,
                ((stage1 * 100) / total) / 100,
                ((prehypertension * 100) / total) / 100,
                ((normal * 100) / total) / 100,
                ((hypotension * 100) / total) / 100,
                totalSys / total,
                totalPulse / total,
                totalDia / total,
                setOfSys.maxOf { it },
                setOfSys.minOf { it },
                setOfDia.maxOf { it },
                setOfDia.minOf { it },
                setOfPulse.maxOf { it },
                setOfPulse.minOf { it }
            )
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

    private fun invalidateShowNoData() {
        showNoData.value = bloodPressureValues.value == null || bloodPressureValues.value!!.isEmpty()
    }

}