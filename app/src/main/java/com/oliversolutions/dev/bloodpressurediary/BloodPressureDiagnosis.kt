package com.oliversolutions.dev.bloodpressurediary


sealed class BloodPressureDiagnosis {
    object Stage2 : BloodPressureDiagnosis()
    object Stage1 : BloodPressureDiagnosis()
    object Pre : BloodPressureDiagnosis()
    object Normal : BloodPressureDiagnosis()
    object Hypotension : BloodPressureDiagnosis()
}

fun getBloodPressureDiagnosis(systolic: Int, diastolic: Int) : BloodPressureDiagnosis {
    return if (systolic > 160 || diastolic > 100) {
        BloodPressureDiagnosis.Stage2
    } else if ((141..160).contains(systolic) || (91..100).contains(diastolic)) {
        BloodPressureDiagnosis.Stage1
    } else if ((121..140).contains(systolic) || (81..90).contains(diastolic)) {
        BloodPressureDiagnosis.Pre
    } else if ((91..120).contains(systolic) || (61..80).contains(diastolic)) {
        BloodPressureDiagnosis.Normal
    } else {
        BloodPressureDiagnosis.Hypotension
    }
}