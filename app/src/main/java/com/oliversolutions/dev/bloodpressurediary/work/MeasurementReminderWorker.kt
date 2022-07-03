package com.oliversolutions.dev.bloodpressurediary.work

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.oliversolutions.dev.bloodpressurediary.*
import com.oliversolutions.dev.bloodpressurediary.database.BloodPressureDatabase
import com.oliversolutions.dev.bloodpressurediary.repository.BloodPressureRepository
import retrofit2.HttpException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MeasurementReminderWorker(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "MeasurementReminderWorker"
    }
    private lateinit var notificationManager: NotificationManager

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        return try {
            if (App.prefs.sendMeasurementReminder) {
                val database = BloodPressureDatabase.getInstance(applicationContext)
                val bloodPressureRepository = BloodPressureRepository(database)
                val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
                val cal = Calendar.getInstance()
                cal.add(Calendar.DATE, 0)
                val toDate = dateFormat.format(cal.time)
                cal.add(Calendar.DATE, (App.prefs.measurementReminderDays * -1))
                val fromDate = dateFormat.format(cal.time)
                if (bloodPressureRepository.getRecordsByDateInList(fromDate, toDate).isEmpty()) {
                    notificationManager =
                        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    createChannel(
                        applicationContext.resources.getString(R.string.notification_channel_id),
                        applicationContext.resources.getString(R.string.notification_channel_name),
                        applicationContext
                    )
                    val notificationIntent = Intent(applicationContext, MainActivity::class.java)
                    val pendingIntent = PendingIntent.getActivity(
                        applicationContext,
                        0,
                        notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    notificationManager.sendNotification(
                        applicationContext.resources.getString(R.string.notification_measurement_title),
                        applicationContext,
                        pendingIntent
                    )
                }
                if (App.prefs.measurementReminderTime != null) {
                    RecurringWork.setup(WORK_NAME, App.prefs.measurementReminderTime!!, applicationContext,1)
                }
            }
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}