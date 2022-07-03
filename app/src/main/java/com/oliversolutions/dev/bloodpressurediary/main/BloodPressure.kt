package com.oliversolutions.dev.bloodpressurediary.main

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