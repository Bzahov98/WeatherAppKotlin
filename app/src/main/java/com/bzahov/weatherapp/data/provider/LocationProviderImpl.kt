package com.bzahov.weatherapp.data.provider

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.provider.interfaces.LocationProvider
const val CUSTOM_LOCATION = "CUSTOM_LOCATION"
class LocationProviderImpl(context: Context) : LocationProvider {
    private val appContext = context.applicationContext
    private val preferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)

    override fun getLocation(): String {
        val selectedName = preferences.getString(CUSTOM_LOCATION, appContext.getString(R.string.default_location))
        return selectedName!!
    }

}