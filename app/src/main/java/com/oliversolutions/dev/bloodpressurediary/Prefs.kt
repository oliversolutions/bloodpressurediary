package com.oliversolutions.dev.bloodpressurediary

import android.content.Context
import android.content.SharedPreferences

class Prefs (context: Context) {

    private val PREFS_MEASUREMENT_REMINDER = "PREFS_MEASUREMENT_REMINDER"
    private val SHARED_MEASUREMENT_REMINDER = "SHARED_MEASUREMENT_REMINDER"

    private val PREFS_MEASUREMENT_REMINDER_DAYS = "PREFS_MEASUREMENT_REMINDER_DAYS"
    private val SHARED_MEASUREMENT_REMINDER_DAYS = "SHARED_MEASUREMENT_REMINDER_DAYS"

    private val PREFS_MEDICATION_REMINDER_DAYS = "PREFS_MEDICATION_REMINDER_DAYS"
    private val SHARED_MEDICATION_REMINDER_DAYS = "SHARED_MEDICATION_REMINDER_DAYS"

    private val PREFS_MEASUREMENT_REMINDER_TIME = "PREFS_MEASUREMENT_REMINDER_TIME"
    private val SHARED_MEASUREMENT_REMINDER_TIME = "SHARED_MEASUREMENT_REMINDER_TIME"

    private val PREFS_MEDICATION_REMINDER = "PREFS_MEDICATION_REMINDER"
    private val SHARED_MEDICATION_REMINDER = "SHARED_MEDICATION_REMINDER"

    private val PREFS_MEDICATION_REMINDER_TIME = "PREFS_MEDICATION_REMINDER_TIME"
    private val SHARED_MEDICATION_REMINDER_TIME = "SHARED_MEDICATION_REMINDER_TIME"

    private val PREFS_MEDICATION_SECOND_REMINDER_TIME = "PREFS_MEDICATION_SECOND_REMINDER_TIME"
    private val SHARED_MEDICATION_SECOND_REMINDER_TIME = "SHARED_MEDICATION_SECOND_REMINDER_TIME"

    private val PREFS_MEDICATION_THIRD_REMINDER_TIME = "PREFS_MEDICATION_THIRD_REMINDER_TIME"
    private val SHARED_MEDICATION_THIRD_REMINDER_TIME = "SHARED_MEDICATION_THIRD_REMINDER_TIME"

    private val PREFS_FROM_DATE = "PREFS_FROM_DATE"
    private val SHARED_FROM_DATE = "SHARED_FROM_DATE"

    private val PREFS_TO_DATE = "PREFS_TO_DATE"
    private val SHARED_TO_DATE = "SHARED_TO_DATE"

    private val PREFS_IS_LAUNCHED = "PREFS_IS_LAUNCHED"
    private val SHARED_IS_LAUNCHED = "SHARED_IS_LAUNCHED"

    private val measurementReminderPrefs: SharedPreferences = context.getSharedPreferences(PREFS_MEASUREMENT_REMINDER, 0)
    private val measurementReminderDaysPrefs: SharedPreferences = context.getSharedPreferences(PREFS_MEASUREMENT_REMINDER_DAYS, 0)
    private val medicationReminderDaysPrefs: SharedPreferences = context.getSharedPreferences(PREFS_MEDICATION_REMINDER_DAYS, 0)

    private val measurementReminderTimePrefs: SharedPreferences = context.getSharedPreferences(PREFS_MEASUREMENT_REMINDER_TIME, 0)

    private val medicationReminderPrefs: SharedPreferences = context.getSharedPreferences(PREFS_MEDICATION_REMINDER, 0)
    private val medicationReminderTimePrefs: SharedPreferences = context.getSharedPreferences(PREFS_MEDICATION_REMINDER_TIME, 0)
    private val medicationSecondReminderTimePrefs: SharedPreferences = context.getSharedPreferences(PREFS_MEDICATION_SECOND_REMINDER_TIME, 0)
    private val medicationThirdReminderTimePrefs: SharedPreferences = context.getSharedPreferences(PREFS_MEDICATION_THIRD_REMINDER_TIME, 0)

    private val fromDayPrefs: SharedPreferences = context.getSharedPreferences(PREFS_FROM_DATE, 0)
    private val toDatePrefs: SharedPreferences = context.getSharedPreferences(PREFS_TO_DATE, 0)
    private val isLaunchedPrefs: SharedPreferences = context.getSharedPreferences(PREFS_IS_LAUNCHED, 0)


    var sendMeasurementReminder: Boolean
        get() = measurementReminderPrefs.getBoolean(SHARED_MEASUREMENT_REMINDER, false)
        set(value) = measurementReminderPrefs.edit().putBoolean(SHARED_MEASUREMENT_REMINDER, value).apply()

    var measurementReminderDays: Int
        get() = measurementReminderDaysPrefs.getInt(SHARED_MEASUREMENT_REMINDER_DAYS, 7)
        set(value) = measurementReminderDaysPrefs.edit().putInt(SHARED_MEASUREMENT_REMINDER_DAYS, value).apply()

    var medicationReminderDays: Int
        get() = medicationReminderDaysPrefs.getInt(SHARED_MEASUREMENT_REMINDER_DAYS, 1)
        set(value) = medicationReminderDaysPrefs.edit().putInt(SHARED_MEASUREMENT_REMINDER_DAYS, value).apply()

    var measurementReminderTime: String?
        get() = measurementReminderTimePrefs.getString(SHARED_MEASUREMENT_REMINDER_TIME, null)
        set(value) = measurementReminderTimePrefs.edit().putString(SHARED_MEASUREMENT_REMINDER_TIME, value).apply()

    var sendMedicationReminder: Boolean
        get() = medicationReminderPrefs.getBoolean(SHARED_MEDICATION_REMINDER, false)
        set(value) = medicationReminderPrefs.edit().putBoolean(SHARED_MEDICATION_REMINDER, value).apply()

    var medicationReminderTime: String?
        get() = medicationReminderTimePrefs.getString(SHARED_MEDICATION_REMINDER_TIME, null)
        set(value) = medicationReminderTimePrefs.edit().putString(SHARED_MEDICATION_REMINDER_TIME, value).apply()

    var medicationSecondReminderTime: String?
        get() = medicationSecondReminderTimePrefs.getString(SHARED_MEDICATION_SECOND_REMINDER_TIME, null)
        set(value) = medicationSecondReminderTimePrefs.edit().putString(SHARED_MEDICATION_SECOND_REMINDER_TIME, value).apply()

    var medicationThirdReminderTime: String?
        get() = medicationThirdReminderTimePrefs.getString(SHARED_MEDICATION_THIRD_REMINDER_TIME, null)
        set(value) = medicationThirdReminderTimePrefs.edit().putString(SHARED_MEDICATION_THIRD_REMINDER_TIME, value).apply()

    var fromDate: String?
        get() = fromDayPrefs.getString(SHARED_FROM_DATE, null)
        set(value) = fromDayPrefs.edit().putString(SHARED_FROM_DATE, value).apply()

    var toDate: String?
        get() = toDatePrefs.getString(SHARED_TO_DATE, null)
        set(value) = toDatePrefs.edit().putString(SHARED_TO_DATE, value).apply()

    var isLaunched: Boolean
        get() = isLaunchedPrefs.getBoolean(SHARED_IS_LAUNCHED, false)
        set(value) = isLaunchedPrefs.edit().putBoolean(SHARED_IS_LAUNCHED, value).apply()

}
