package com.bzahov.weatherapp.ui.remoteviews.widgets.interfaces

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import com.bzahov.weatherapp.ui.remoteviews.widgets.CurrentWeatherWidget

interface CurrentWidgetRefresher : WidgetRefresher {
    override fun Context.refreshWidgetData() {
        val TAG = "CurrentWidgetRefresher"
        Log.d(TAG, "request update of current weather widget")
        val widgetUpdateIntent = Intent(this, CurrentWeatherWidget::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(
                AppWidgetManager.EXTRA_APPWIDGET_IDS,
                AppWidgetManager.getInstance(this@refreshWidgetData).getAppWidgetIds(
                    ComponentName(
                        this@refreshWidgetData,
                        CurrentWeatherWidget::class.java
                    )
                )
            )
        }
        sendBroadcast(widgetUpdateIntent)
    }
}