package com.oliversolutions.dev.bloodpressurediary.statistics

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BloodPressureStatistic (
    val stage_2: Float? = null,
    val stage_1: Float? = null,
    val pre: Float? = null,
    val normal: Float? = null,
    val hypotension: Float? = null,
    val averageSys: Double? = null,
    val averagePulse: Double? = null,
    val averageDia: Double? = null,
    val maxSys: Double? = null,
    val minSys: Double? = null,
    val maxDia: Double? = null,
    val minDia: Double? = null,
    val maxPulse: Double? = null,
    val minPulse: Double? = null
    ) : Parcelable
