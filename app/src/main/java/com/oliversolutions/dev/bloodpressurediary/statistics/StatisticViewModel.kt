package com.oliversolutions.dev.bloodpressurediary.statistics

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.oliversolutions.dev.bloodpressurediary.*
import com.oliversolutions.dev.bloodpressurediary.database.BloodPressureDatabase
import com.oliversolutions.dev.bloodpressurediary.database.asDomainModel
import com.oliversolutions.dev.bloodpressurediary.repository.BloodPressureRepository
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
@RequiresApi(Build.VERSION_CODES.O)
class StatisticViewModel(application: Application, private val highPressureRepository: BloodPressureRepository) : AndroidViewModel(application) {

    var bloodPressureStatisticData = BloodPressureStatistic()
    var bloodPressureValues: LiveData<List<BloodPressure>>
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
        bloodPressureValues = when (fromDate.isBlank() || toDate.isBlank()) {
            true -> {
                Transformations.map(highPressureRepository.getAllRecords()) {
                    it.asDomainModel()
                }
            }
            false -> {
                Transformations.map(highPressureRepository.getRecordsByDate(fromDate, toDate)) {
                    it.asDomainModel()
                }
            }
        }
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

    private fun setAllRecords() {
        bloodPressureValues = Transformations.map(highPressureRepository.getAllRecords()) {
            it.asDomainModel()
        }
    }

    private fun setRecordsByDate(fromDate: String, toDate: String) {
        bloodPressureValues = Transformations.map(highPressureRepository.getRecordsByDate(fromDate, toDate)) {
            it.asDomainModel()
        }
    }
}