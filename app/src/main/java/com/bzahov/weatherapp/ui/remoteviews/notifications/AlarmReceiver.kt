package com.bzahov.weatherapp.ui.remoteviews.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bzahov.weatherapp.ForecastApplication.Companion.getAppString
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.ForecastDatabase
import com.bzahov.weatherapp.data.db.entity.current.CurrentWeatherEntry
import com.bzahov.weatherapp.data.provider.PreferenceProvider.Companion.getUnitAbbreviation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AlarmReceiver(
) : BroadcastReceiver() {

    var weatherData: CurrentWeatherEntry? = null

    override fun onReceive(context: Context, intent: Intent) {
        // TODO: Step 1.10 [Optional] remove toast
        Toast.makeText(context, context.getText(R.string.notification_title), Toast.LENGTH_SHORT)
            .show()

        // TODO: Step 1.9 add call to sendNotification
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        val action = intent.action

        if (action.equals("notification.action.title")) {
            val notificationTitle = intent.getStringArrayListExtra(
                getAppString(R.string.notification_action_title_weather_data_key)
            )
            // TODO not reachable code
            notificationManager.sendNotification(
                notificationTitle[0],
                notificationTitle[1],
                "",
                context
            )
        } else {

            /*val notificationTarget = NotificationTarget(
                context,
                R.id.remoteview_notification_icon,
                remoteViews,
                notification,
                NOTIFICATION_ID
            );*/


            val unitAbbreviation = getUnitAbbreviation(context)
            CoroutineScope(Dispatchers.IO).launch {
                // Fetch data from database
                val weather =
                    ForecastDatabase.invoke(context.applicationContext).currentWeatherDao()
                        .getCurrentWeatherAsync()
                val location =
                    ForecastDatabase.invoke(context.applicationContext).currentLocationDao()
                        .getLocationNotLive()

                notificationManager.sendNotification(
                    "Weather in ${location.extractLocationString()}",
                    "${weather.getCondition()} ${weather.temperature} $unitAbbreviation, \n" +
                            "Feels like:${weather.feelslike} $unitAbbreviation",
                    weather.weatherIcons.last(),
                    context
                )
            }
        }
    }

    private fun getLocationSharedPreferences(context: Context) =
        context.getSharedPreferences(
            getAppString(R.string.preference_current_location),
            Context.MODE_PRIVATE
        )

}