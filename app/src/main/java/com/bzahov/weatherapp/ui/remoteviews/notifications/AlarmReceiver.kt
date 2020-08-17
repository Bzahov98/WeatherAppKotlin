package com.bzahov.weatherapp.ui.remoteviews.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bzahov.weatherapp.ForecastApplication.Companion.getAppString
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.entity.current.CurrentWeatherEntry


class AlarmReceiver(
): BroadcastReceiver() {

    var weatherData : CurrentWeatherEntry? = null

    override fun onReceive(context: Context, intent: Intent) {
        // TODO: Step 1.10 [Optional] remove toast
       Toast.makeText(context, context.getText(R.string.notification_title), Toast.LENGTH_SHORT).show()

        // TODO: Step 1.9 add call to sendNotification
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        val action = intent.action

        if(action.equals("notification.action.title")){
            val notificationTitle = intent.getStringArrayListExtra(
                getAppString(R.string.notification_action_title_weather_data_key))

            notificationManager.sendNotification(
                notificationTitle[0],
                notificationTitle[1],
                context
            )
        }else {

            notificationManager.sendNotification(
                context.getText(R.string.notification_ready).toString(),
                "Particularly Rainy",
                context
            )
        }
    }

    private fun getLocationSharedPreferences(context: Context) =
        context.getSharedPreferences(
            getAppString(R.string.preference_current_location),
            Context.MODE_PRIVATE
        )

}