package com.bzahov.weatherapp.ui.remoteviews.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.provider.TAG
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
    imageUrlString: String = "",
    context: Context
) {
    // Create the content intent for the notification, which launches this activity
    val contentPendingIntent = createNotificationIntent(context)
    if (contentPendingIntent == null) {
        Log.e(TAG, "ERROR in notification intent null")
        return
    }


    //snoozeIntent(applicationContext)

    // Get an instance of NotificationCompat.Builder
    // Build the notification

    val builder = createNotificationBuilder(
        context,
        imageUrlString,
        notificationTitle,
        notificationBody,
        contentPendingIntent,
        this
    )
    // Call notify
    notify(NOTIFICATION_ID, builder.build())

    notify(0, builder.build())
}

private fun createNotificationIntent(applicationContext: Context): PendingIntent? {
    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    // create PendingIntent
    return PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}

// Create intent
private fun createNotificationBuilder(
    context: Context,
    weatherImageUrl: String,
    notificationTitle: String,
    notificationBody: String,
    contentPendingIntent: PendingIntent,
    notificationManager: NotificationManager
): NotificationCompat.Builder {
//
//    val target = NotificationTarget(
//        context,
//        ,
//        remoteView,
//        notification,
//        NOTIFICATION_ID
//    )
//
//    Glide.with(context.applicationContext)
//        .asBitmap()
//        .load("TODO")
//        .into(target)
//
    //add style
    val weatherImage = BitmapFactory.decodeResource(
        context.resources,
        R.drawable.icons8_no_rain_50
    )
    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(weatherImage)
        .bigLargeIcon(null)


    val builder = NotificationCompat.Builder(
        context,
        context.getString(R.string.notification_channel_id)
    )

        //.setStyle(bigPicStyle)
        .setLargeIcon(weatherImage)
        //.setLargeIcon(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(, R.drawable.icon128), 128, 128, false))
        .setSmallIcon(R.drawable.snowflake/*cooked_egg*/)
        .setContentTitle(notificationTitle)
        .setContentText(notificationBody)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setWhen(System.currentTimeMillis())

//        // TODO: Step 2.3 add snooze action
//        .addAction(
//            R.drawable.egg_icon,
//            applicationContext.getString(R.string.snooze),
//            snoozePendingIntent
//        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)
    if (weatherImageUrl != "") {
        setNotificationImage(context, weatherImageUrl, builder)
    }else{
        Log.e(TAG,"Image url is wrong: $weatherImageUrl")
    }
    return builder
}

private fun setNotificationImage(
    context: Context,
    weatherImageUrl: String,
    builder: NotificationCompat.Builder
) {
    val futureTarget = Glide.with(context)
        .asBitmap()
        .load(weatherImageUrl)
        .circleCrop()
        .submit()

    val bitmap = futureTarget.get()
    builder.setLargeIcon(bitmap)

    Glide.with(context).clear(futureTarget)
}


// add snooze action
private fun snoozeIntent(applicationContext: Context) {
//    val snoozeIntent = Intent(applicationContext, SnoozeReceiver::class.java)
//    val snoozePendingIntent: PendingIntent = PendingIntent.getBroadcast(
//        applicationContext,
//        REQUEST_CODE,
//        snoozeIntent,
//        FLAGS
//    )
}

/**
 * Cancels all notifications.
 *
 */
fun NotificationManager.cancelNotifications() {
    cancelAll()
}