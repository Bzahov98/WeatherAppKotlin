package com.bzahov.weatherapp.ui.remoteviews.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.AppWidgetTarget
import com.bzahov.weatherapp.ForecastApplication
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.ForecastDatabase
import com.bzahov.weatherapp.data.repo.interfaces.CurrentForecastRepository
import com.bzahov.weatherapp.internal.UIConverterFieldUtils
import com.bzahov.weatherapp.ui.settings.SettingsFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [CurrentWeatherWidgetConfigureActivity]
 */
private const val TAG = "CurrentWeatherWidget"
private const val ONCLICKTAG = "myOnClickTag"

class CurrentWeatherWidget(
    val currentWeatherRepository: CurrentForecastRepository
) : AppWidgetProvider()/*, KodeinAware*/ {
    //
//    override val kodein by closestKodein()
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {

            val remoteViews = RemoteViews(context.packageName, R.layout.weather_widget)

            remoteViews.setOnClickPendingIntent(
                R.id.widgetRefreshData,
                getPendingSelfIntent(context, ONCLICKTAG)
            )
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
        if (intent != null) {
            if (ONCLICKTAG == intent.action){
                Toast.makeText(context,"Trying to refresh data",Toast.LENGTH_SHORT).show()
               // currentWeatherRepository.requestRefreshOfData()
            }
        }
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
            val unitAbbreviation = UIConverterFieldUtils.chooseLocalizedUnitAbbreviation(
                context.getSharedPreferences(""/*REWORK: FIND RIGHT PREFERENCE NAME*/, 0)
                    .getBoolean(SettingsFragment.UNIT_SYSTEM, true),
                ForecastApplication.getAppString(R.string.metric_temperature),
                ForecastApplication.getAppString(R.string.imperial_temperature)
            )
            // Construct the RemoteViews object

            CoroutineScope(Dispatchers.IO).launch {
                val remoteViews = updateUI(context, unitAbbreviation, appWidgetId)
                // Instruct the widget manager to update the widget
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
            }

            // Instruct the widget manager to update the widget
        }

        private fun updateUI(
            context: Context,
            unitAbbreviation: String,
            appWidgetId: Int
        ): RemoteViews {
            val weather =
                ForecastDatabase.invoke(context.applicationContext).currentWeatherDao()
                    .getCurrentWeatherAsync()
            val location =
                ForecastDatabase.invoke(context.applicationContext).currentLocationDao()
                    .getLocationNotLive()
            // set values to views
            val remoteViews = RemoteViews(context.packageName, R.layout.weather_widget)
            remoteViews.setTextViewText(
                R.id.widgetLocation,
                "${location.name}, ${location.country} | ${location.getLocalTimeClock()}"
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