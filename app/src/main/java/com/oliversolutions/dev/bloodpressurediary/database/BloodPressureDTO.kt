package com.oliversolutions.dev.bloodpressurediary.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.oliversolutions.dev.bloodpressurediary.main.BloodPressure
import java.util.*

@Entity(tableName = "blood_pressure_table")
data class BloodPressureDTO(
    @ColumnInfo(name = "systolic")
    var systolic: Double?,
    @ColumnInfo(name = "diastolic")
    var diastolic: Double?,
    @ColumnInfo(name = "pulse")
    var pulse: Double?,
    @ColumnInfo(name = "notes")
    var notes: String?,
    @ColumnInfo(name = "creation_time")
    var creationTime: String?,
    @ColumnInfo(name = "creation_date")
    var creationDate: String?,
    @ColumnInfo(name = "average_diastolic")
    var averageDiastolic: String? = null,
    @ColumnInfo(name = "average_systolic")
    var averageSystolic: String? = null,
    @ColumnInfo(name = "average_pulse")
    var averagePulse: String? = null,
    @PrimaryKey val id: String = UUID.randomUUID().toString()
    )

fun List<BloodPressureDTO>.asDomainModel(): List<BloodPressure> {
    return map {
        BloodPressure(
            systolic = it.systolic,
            diastolic = it.diastolic,
            pulse = it.pulse,
            notes = it.notes,
            creationTime = it.creationTime,
            creationDate = it.creationDate,
            averageDiastolic = it.averageDiastolic,
            averageSystolic = it.averageSystolic,
            averagePulse = it.averagePulse,
            id = it.id)
    }
}

