package com.bzahov.weatherapp.ui

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.ui.WeatherWidget.Companion.updateAppWidget

/**
 * The configuration screen for the [WeatherWidget] AppWidget.
 */
class WeatherWidgetConfigureActivity : Activity() {
    var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    var mAppWidgetText: EditText? = null
    var mOnClickListener =
        View.OnClickListener {
            val context: Context = this@WeatherWidgetConfigureActivity

            // When the button is clicked, store the string locally
            val widgetText = mAppWidgetText!!.text.toString()
            saveTitlePref(
                context,
                mAppWidgetId,
                widgetText
            )

            // It is the responsibility of the configuration activity to update the app widget
            val appWidgetManager = AppWidgetManager.getInstance(context)
            updateAppWidget(context, appWidgetManager, mAppWidgetId)

            // Make sure we pass back the original appWidgetId
            val resultValue = Intent()
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
            setResult(RESULT_OK, resultValue)
            finish()
        }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED)
        setContentView(R.layout.weather_widget_configure)
        mAppWidgetText = findViewById<View>(R.id.widgetLocation) as EditText
        findViewById<View>(R.id.add_button).setOnClickListener(mOnClickListener)

        // Find the widget id from the intent.
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
        mAppWidgetText!!.setText(
            loadTitlePref(
                this@WeatherWidgetConfigureActivity,
                mAppWidgetId
            )
        )
    }

    companion object {
        private const val PREFS_NAME = "com.bzahov.weatherapp.ui.WeatherWidget"
        private const val PREF_PREFIX_KEY = "appwidget_"

        // Write the prefix to the SharedPreferences object for this widget
        fun saveTitlePref(
            context: Context,
            appWidgetId: Int,
            text: String?
        ) {
            val prefs =
                context.getSharedPreferences(PREFS_NAME, 0)
                    .edit()
            prefs.putString(
                PREF_PREFIX_KEY + appWidgetId,
                text
            )
            prefs.apply()
        }

        // Read the prefix from the SharedPreferences object for this widget.
        // If there is no preference saved, get the default from a resource
        fun loadTitlePref(context: Context, appWidgetId: Int): String {
            val prefs =
                context.getSharedPreferences(PREFS_NAME, 0)
            val titleValue = prefs.getString(
                PREF_PREFIX_KEY + appWidgetId,
                null
            )
            return titleValue ?: context.getString(R.string.widget_text)
        }

        fun deleteTitlePref(context: Context, appWidgetId: Int) {
            val prefs =
                context.getSharedPreferences(PREFS_NAME, 0)
                    .edit()
            prefs.remove(PREF_PREFIX_KEY + appWidgetId)
            prefs.apply()
        }
    }
}