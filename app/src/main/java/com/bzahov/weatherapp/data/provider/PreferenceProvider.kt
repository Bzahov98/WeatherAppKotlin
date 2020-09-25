package com.bzahov.weatherapp.data.provider

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.bzahov.weatherapp.ForecastApplication
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.internal.UIConverterFieldUtils
import com.bzahov.weatherapp.ui.settings.SettingsFragment

open class PreferenceProvider(context: Context) {
    protected val appContext: Context = context.applicationContext

    val preferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)

    companion object{
        fun getUnitAbbreviation(context: Context): String {

            return UIConverterFieldUtils.chooseLocalizedUnitAbbreviation(
                getCurrentUnitSystemState(context),
                ForecastApplication.getAppString(R.string.metric_temperature),
                ForecastApplication.getAppString(R.string.imperial_temperature)
            )
        }

        fun getCurrentUnitSystemState(context: Context): Boolean {
            val metricString =
                ForecastApplication.getAppString(R.string.unit_system_full_name_metric)
//            val currentUnitSystem = getSharedPreferences(context)
//                .getString(SettingsFragment.UNIT_SYSTEM, metricString)
//
//            return currentUnitSystem == metricString
            return getFromSharedPreferenceStringToBoolean(
                context,
                SettingsFragment.UNIT_SYSTEM,
                metricString
            )
        }

        fun getFromSharedPreferenceStringToBoolean(
            context: Context,
            preferenceKey: String,
            defaultValue: String
        ): Boolean {
            val currentPreferenceValue = getSharedPreferences(context)
                .getString(preferenceKey, defaultValue)

            return currentPreferenceValue == defaultValue
        }

        fun getFromSharedPreferenceBoolean(
            context: Context,
            preferenceKey: String,
            defaultValue: Boolean
        ): Boolean {

            return getSharedPreferences(context)
                .getBoolean(preferenceKey, defaultValue)
        }

        fun getSharedPreferences(context: Context): SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)
    }
}