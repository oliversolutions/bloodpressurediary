package com.oliversolutions.dev.bloodpressurediary.work

import android.content.Context
import androidx.work.*
import java.util.*
import java.util.concurrent.TimeUnit

class RecurringWork {
    companion object {
        fun setup(workName: String, time: String, context: Context, days: Int? = null) {

            val workManager = WorkManager.getInstance(context)
            workManager.cancelUniqueWork(workName)

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresCharging(false)
                .build()
            val currentDate = Calendar.getInstance().timeInMillis
            val dueDate = Calendar.getInstance()
            dueDate.set(Calendar.HOUR_OF_DAY, time.split(":")[0].toInt())
            dueDate.set(Calendar.MINUTE, time.split(":")[1].toInt())

            if (days != null) {
                dueDate.add(Calendar.DAY_OF_MONTH, days)
            } else if (dueDate.timeInMillis <= currentDate) {
                dueDate.add(Calendar.HOUR_OF_DAY, 24)
            }
            val timeDiff = dueDate.timeInMillis - currentDate

            when (workName) {
                MeasurementReminderWorker.WORK_NAME -> {
                     val dailyWorkRequest = OneTimeWorkRequestBuilder<MeasurementReminderWorker>()
                        .setConstraints(constraints)
                         .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                        .build()
                    workManager.enqueueUniqueWork(
                        workName,
                        ExistingWorkPolicy.KEEP,
                        dailyWorkRequest
                    )
                }
                MedicationReminderWorker.WORK_NAME,
                MedicationReminderWorker.WORK_NAME_2,
                MedicationReminderWorker.WORK_NAME_3 -> {
                    val data = Data.Builder()
                    data.putString("worker_name", workName)
                    val dailyWorkRequest = OneTimeWorkRequestBuilder<MedicationReminderWorker>()
                        .setConstraints(constraints)
                        .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                        .setInputData(data.build())
                        .build()
                    workManager.enqueueUniqueWork(
                        workName,
                        ExistingWorkPolicy.KEEP,
                        dailyWorkRequest
                    )
                }
            }
        }
    }
}