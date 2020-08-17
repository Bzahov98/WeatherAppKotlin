package com.bzahov.weatherapp.ui.remoteviews.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.ui.MainActivity

private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

// TODO: Step 1.1 extension function to send messages (GIVEN)
/**
 * Builds and delivers the notification.
 *
 * @param context, activity context.
 */

fun NotificationManager.sendNotification(
    notificationTitle: String,
    notificationBody: String,
    applicationContext: Context
) {
    // Create the content intent for the notification, which launches
    // this activity
    // TODO: Step 1.11 create intent
    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    // TODO: Step 1.12 create PendingIntent
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    // TODO: Step 2.0 add style
    val weatherImage = BitmapFactory.decodeResource(
        applicationContext.resources,
        R.drawable.icons8_no_rain_50
    )
    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(weatherImage)
        .bigLargeIcon(null)

//    // TODO: Step 2.2 add snooze action
//    val snoozeIntent = Intent(applicationContext, SnoozeReceiver::class.java)
//    val snoozePendingIntent: PendingIntent = PendingIntent.getBroadcast(
//        applicationContext,
//        REQUEST_CODE,
//        snoozeIntent,
//        FLAGS)

    // TODO: Step 1.2 get an instance of NotificationCompat.Builder
    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )

        // TODO: Step 1.8 use the new 'breakfast' notification channel

        // TODO: Step 1.3 set title, text and icon to builder
        // .setStyle(bigPicStyle)
        .setLargeIcon(weatherImage)
        //.setLargeIcon(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(, R.drawable.icon128), 128, 128, false))
        .setSmallIcon(R.drawable.snowflake/*cooked_egg*/)
        .setContentTitle(notificationTitle)
        .setContentText(notificationBody)
        // TODO: Step 1.13 set content intent
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        // TODO: Step 2.1 add style to builder
        .setWhen(System.currentTimeMillis())


//        // TODO: Step 2.3 add snooze action
//        .addAction(
//            R.drawable.egg_icon,
//            applicationContext.getString(R.string.snooze),
//            snoozePendingIntent
//        )

        // TODO: Step 2.5 set priority
        .setPriority(NotificationCompat.PRIORITY_HIGH)
    // TODO: Step 1.4 call notify
    notify(NOTIFICATION_ID, builder.build())
}

// TODO: Step 1.14 Cancel all notifications
/**
 * Cancels all notifications.
 *
 */
fun NotificationManager.cancelNotifications() {
    cancelAll()
}