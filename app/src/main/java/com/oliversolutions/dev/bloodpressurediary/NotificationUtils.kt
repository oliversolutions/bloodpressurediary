package com.oliversolutions.dev.bloodpressurediary

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat

const val NOTIFICATION_MEASUREMENT_ID = 0
const val NOTIFICATION_MEDICATION_ID = 1
private lateinit var action: NotificationCompat.Action

fun NotificationManager.sendNotification(messageTitle: String, applicationContext: Context, pendingIntent: PendingIntent?) {
    if (pendingIntent == null) {
        val builder = NotificationCompat.Builder(applicationContext, applicationContext.getString(R.string.notification_channel_id))
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle(messageTitle)
        notify(NOTIFICATION_MEDICATION_ID, builder.build())
    } else {
        action = NotificationCompat.Action(null, applicationContext.getString(R.string.measure_it_now), pendingIntent)
        val builder = NotificationCompat.Builder(applicationContext, applicationContext.getString(R.string.notification_channel_id))
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle(messageTitle)
            .addAction(action)
        notify(NOTIFICATION_MEASUREMENT_ID, builder.build())
    }
}

fun createChannel(channelId: String, channelName: String, applicationContext: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH).apply {
            setShowBadge(false)
        }
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.description = applicationContext.resources.getString(R.string.notification_channel_description)
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }
}

