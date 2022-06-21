package com.oliversolutions.dev.bloodpressurediary

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BloodPressure(
    val systolic: Double?,
    val diastolic: Double?,
    val pulse: Double?,
    val notes: String?,
    val creationTime: String?,
    val creationDate: String?,
    val averageDiastolic: String?,
    val averageSystolic: String?,
    val averagePulse: String?,
    val id: Long = 0) : Parcelable

enum class BloodPressureFilter(val value: String) {
    SHOW_ALL("show_all"),
    SHOW_TODAY("show_today"),
    SHOW_YESTERDAY("show_yesterday"),
    SHOW_LAST_7_DAYS("show_last_7_days"),
    SHOW_LAST_14_DAYS("show_last_14_days"),
    SHOW_LAST_30_DAYS("show_last_30_days"),
    SHOW_LAST_60_DAYS("show_last_60_days"),
    SHOW_LAST_90_DAYS("show_last_90_days"),
    SHOW_CUSTOM("show_custom")

}
