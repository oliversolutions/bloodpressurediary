package com.oliversolutions.dev.bloodpressurediary.main

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class BloodPressure(
    val systolic: Double?,
    val diastolic: Double?,
    val pulse: Double?,
    val notes: String?,
    val creationTime: String?,
    val creationDate: String?,
    val averageDiastolic: String? = null,
    val averageSystolic: String? = null,
    val averagePulse: String? = null,
    val id: String = UUID.randomUUID().toString()) : Parcelable