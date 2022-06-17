package com.oliversolutions.dev.bloodpressurediary.work

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.oliversolutions.dev.bloodpressurediary.*
import retrofit2.HttpException

class MedicationReminderWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "MedicationReminderWorker1"
        const val WORK_NAME_2 = "MedicationReminderWorker2"
        const val WORK_NAME_3 = "MedicationReminderWorker3"
    }

    private lateinit var notificationManager: NotificationManager

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        return try {
            if (App.prefs.sendMedicationReminder) {
                notificationManager =
                    applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                createChannel(
                    applicationContext.resources.getString(R.string.notification_channel_id),
                    applicationContext.resources.getString(R.string.notification_channel_name),
                    applicationContext
                )
                notificationManager.sendNotification(
                    applicationContext.resources.getString(R.string.notification_medication_title),
                    applicationContext,
                    null
                )
                val workName = inputData.getString("worker_name")
                if (App.prefs.medicationReminderTime != null && workName == WORK_NAME) {
                    RecurringWork.setup(workName, App.prefs.medicationReminderTime!!, applicationContext,1)
                } else if (App.prefs.medicationSecondReminderTime != null && workName == WORK_NAME_2) {
                    RecurringWork.setup(workName, App.prefs.medicationSecondReminderTime!!, applicationContext,1)
                } else if (App.prefs.medicationThirdReminderTime != null && workName == WORK_NAME_3) {
                    RecurringWork.setup(workName, App.prefs.medicationThirdReminderTime!!, applicationContext,1)
                }
            }
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}