package com.bzahov.weatherapp.ui.remoteviews.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.AppWidgetTarget
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.ForecastDatabase
import com.bzahov.weatherapp.data.provider.PreferenceProvider.Companion.getUnitAbbreviation
import com.bzahov.weatherapp.ui.activities.MainActivity
import com.bzahov.weatherapp.ui.remoteviews.widgets.interfaces.CurrentWidgetRefresher
import com.bzahov.weatherapp.ui.settings.SettingsFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [CurrentWeatherWidgetConfigureActivity]
 */
private const val TAG = "CurrentWeatherWidget"
const val REFRESH_BUTTON_TAG = "myOnClickTag"
const val WIDGET_NOTIFICATION_TAG = "myOnClickTag2"
const val WIDGET_LAUNCH_ACTIVITY_TAG = "myOnClickTag3"

open class CurrentWeatherWidget() :
    AppWidgetProvider(),
    CurrentWidgetRefresher/*,
    KodeinAware*/ {

    private var refreshButtonActive: Boolean = false
    private var notificationButtonActive: Boolean = false

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {

            val remoteViews = RemoteViews(context.packageName, R.layout.weather_widget)
            Log.e(TAG, "onUpdate")
            remoteViews.setOnClickPendingIntent(
                R.id.widgetRefreshData,
                getPendingSelfIntent(context, REFRESH_BUTTON_TAG)
            )
            remoteViews.setOnClickPendingIntent(
                R.id.widgetOtherButton,
                getPendingSelfIntent(context, WIDGET_NOTIFICATION_TAG)
            )
            remoteViews.setOnClickPendingIntent(
                R.id.widgetCurrentWeather,
                getPendingSelfIntent(context, WIDGET_LAUNCH_ACTIVITY_TAG)
            )


            Log.e(TAG, "onUpdate after remote views Intent")

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
            updateAppWidget(
                context,
                appWidgetManager,
                appWidgetId
            )
        }
    }

    override fun onDeleted(
        context: Context,
        appWidgetIds: IntArray
    ) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            CurrentWeatherWidgetConfigureActivity.deleteTitlePref(
                context,
                appWidgetId
            )
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        Log.d(TAG, "OnReceive")
        val remoteViews = RemoteViews(context?.packageName, R.layout.weather_widget)
        if (intent != null) {

            when (intent.action) {
                REFRESH_BUTTON_TAG -> {
                    if (!refreshButtonActive) {
                        refreshButtonActive = true
                        Log.d(TAG, "REFRESH_BUTTON_TAG clicked")

                        refreshButtonOnClick(context, remoteViews, intent)

                        refreshButtonActive = false
                    } else {
                        Log.d(TAG, "REFRESH_BUTTON_TAG refused")
                    }
                }
                WIDGET_NOTIFICATION_TAG -> {
                    if (!notificationButtonActive) {
                        notificationButtonActive = true
                        Log.d(TAG, "WIDGET_NOTIFICATION_TAG clicked")

                        notificationButtonOnClick(context, remoteViews, intent)

                        notificationButtonActive = false
                    } else Log.d(TAG, "WIDGET_NOTIFICATION_TAG refused")

                }
                WIDGET_LAUNCH_ACTIVITY_TAG -> {
                    launchCurrentWeatherActivity(context, remoteViews, intent)
                }
                else -> {
                    Log.d(TAG, "OnReceive with tag: ${intent.action}")

                }

            }
        }
    }

    private fun launchCurrentWeatherActivity(
        context: Context?,
        remoteViews: RemoteViews,
        intent: Intent
    ) {
        Toast.makeText(context, "Launching Activity", Toast.LENGTH_SHORT).show()
        val mainActivityIntent = Intent(context, MainActivity::class.java)
        mainActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context!!.startActivity(mainActivityIntent)
    }

    private fun refreshButtonOnClick(
        context: Context?,
        remoteViews: RemoteViews,
        intent: Intent
    ) {
        Toast.makeText(context, "Trying to refresh the data", Toast.LENGTH_SHORT).show()
        CoroutineScope(Dispatchers.IO).launch {
            // Instruct the widget manager to update the widget
            //currentForecastRepository.requestRefreshOfData()

            context?.refreshWidgetData()
        }
    }

    private fun notificationButtonOnClick(
        context: Context?,
        remoteViews: RemoteViews,
        intent: Intent
    ) {
        Toast.makeText(context, "Trying to notif", Toast.LENGTH_SHORT).show()

        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor: SharedPreferences.Editor = preferences.edit()
        //editor.putInt("storedInt", storedPreference); // value to store
        // editor.commit()

        var isNotificationsOn =
            PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                SettingsFragment.SHOW_NOTIFICATIONS,
                true
            )

        if (isNotificationsOn) {
            Toast.makeText(context, "Turning Off app notifications", Toast.LENGTH_SHORT)
                .show()
            // TODO set notificationsOn app preferences to false
            isNotificationsOn = false
            editor.putBoolean(SettingsFragment.SHOW_NOTIFICATIONS, isNotificationsOn)
            editor.apply()

            var appWidgetTarget =
                AppWidgetTarget(
                    context,
                    R.id.widgetOtherButton,
                    remoteViews,
                    remoteViews.layoutId
                )
            if (context != null) {
                Glide.with(context)
                    .asBitmap()
                    .load(R.drawable.notifications_off_black_48px)
                    .into(appWidgetTarget)
            };

        } else {
            Toast.makeText(context, "Turning ON app notifications", Toast.LENGTH_SHORT)
                .show()
            // TODO set notificationsOn app preferences to true
            isNotificationsOn = true
            editor.putBoolean(SettingsFragment.SHOW_NOTIFICATIONS, isNotificationsOn)
            editor.apply()
            //                    remoteViews.setImageViewResource(
            //                        R.id.widgetOtherButton,
            //                        R.drawable.notifications_on_50px
            //                    )
            var appWidgetTarget =
                AppWidgetTarget(
                    context,
                    R.id.widgetOtherButton,
                    remoteViews,
                    remoteViews.layoutId
                )
            if (context != null) {
                Glide.with(context)
                    .asBitmap()
                    .load(R.drawable.notification_on_black_50px)
                    .into(appWidgetTarget)
            };
        }
        // currentWeatherRepository.requestRefreshOfData()
        //                CoroutineScope(Dispatchers.IO).launch {
        //                    // Instruct the widget manager to update the widget
        //                    //currentForecastRepository.requestRefreshOfData()
        //                    //context?.refreshWidgetData()
        //                }
        val appWidgetManager = AppWidgetManager.getInstance(context)
        appWidgetManager.updateAppWidget(intent.component, remoteViews)
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        super.onDisabled(context)
    }

    private fun getPendingSelfIntent(
        context: Context?,
        action: String?
    ): PendingIntent? {
        val intent = Intent(context, javaClass)
        intent.action = action
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }

    companion object {
        @JvmStatic

        fun updateAppWidget(
            context: Context, appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            Log.d(TAG, "update current weather widget")

            CoroutineScope(Dispatchers.IO).launch {
                val remoteViews = updateUI(context, appWidgetId)
                // Instruct the widget manager to update the widget
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
            }

            // Instruct the widget manager to update the widget
        }

        private fun updateUI(
            context: Context,
            appWidgetId: Int
        ): RemoteViews {

            val unitAbbreviation = getUnitAbbreviation(context)
            // Fetch data from database
            val weather =
                ForecastDatabase.invoke(context.applicationContext).currentWeatherDao()
                    .getCurrentWeatherAsync()
            val location =
                ForecastDatabase.invoke(context.applicationContext).currentLocationDao()
                    .getLocationNotLive()
            // Construct the RemoteViews object
            val remoteViews = RemoteViews(context.packageName, R.layout.weather_widget)

            // set values to views
            remoteViews.setTextViewText(
                R.id.widgetLocation,
                location.extractLocationString()
            )
            remoteViews.setTextViewText(
                R.id.widgetTemperatureText,
                "${weather.temperature} "
            )

            remoteViews.setTextViewText(
                R.id.widgetTemperatureFeelsLikeText,
                "Feels Like: ${weather.feelslike} "
            )
            remoteViews.setTextViewText(
                R.id.widgetTemperatureUnit,
                unitAbbreviation
            )
            remoteViews.setTextViewText(
                R.id.widgetTemperatureUnit2,
                unitAbbreviation
            )


            var appWidgetTarget =
                AppWidgetTarget(context, R.id.widgetWeatherIcon, remoteViews, appWidgetId)

            // load async image to remote image view
            Glide.with(context)
                .asBitmap()
                .load(weather.weatherIcons.last())
                .into(appWidgetTarget);
            return remoteViews
        }
    }


}