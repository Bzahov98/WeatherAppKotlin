package com.bzahov.weatherapp.data.provider

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import com.bzahov.weatherapp.ForecastApplication
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.entity.current.WeatherLocation
import com.bzahov.weatherapp.data.provider.interfaces.LocationProvider
import com.bzahov.weatherapp.data.provider.interfaces.LocationProvider.Companion.DEFAULT_TIMEZONE_OFFSET
import com.bzahov.weatherapp.internal.asDeferred
import com.bzahov.weatherapp.internal.exceptions.LocationPermissionNotGrantedException
import com.bzahov.weatherapp.ui.settings.SettingsFragment.Companion.CUSTOM_LOCATION
import com.bzahov.weatherapp.ui.settings.SettingsFragment.Companion.USE_DEVICE_LOCATION
import com.google.android.gms.location.FusedLocationProviderClient
import kotlin.math.abs


class LocationProviderImpl(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    val context: Context
) : PreferenceProvider(context), LocationProvider {

    override var offsetDateTime: Int = DEFAULT_TIMEZONE_OFFSET

    override fun resetCustomLocationToDefault() {
        val defaultLocation = ForecastApplication.getAppString(R.string.default_location)
        preferences.edit().putString(CUSTOM_LOCATION,
            defaultLocation
        ).apply()
        Log.e(TAG,"Wrong Location! Reset custom location to default: $defaultLocation")
    }

    override fun getLocationString(): String {
        return getCustomLocationName()!!
    }

    override suspend fun hasLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        val deviceLocationChanged = try {
            hasDeviceLocationChanged(lastWeatherLocation)
        } catch (e: LocationPermissionNotGrantedException) {
            false
        }
        return deviceLocationChanged || hasCustomLocationChanged(lastWeatherLocation)
    }

    // default is false for WeatherStack api and true for OpenWeather api
    override suspend fun getPreferredLocationString(): String {
        var customLocationName: String = "${getCustomLocationName()}"
        if (isUsingDeviceLocation()) {
            try {
                val deviceLocation = getLastPhysicalDeviceLocation()
                if (deviceLocation == null) {
                    Log.d(TAG, "getPreferredLocationString 1)new location is: $customLocationName")
                    return customLocationName
                }
                Log.d(
                    TAG,
                    ">>>getPreferredLocationString 2)new location is: ${deviceLocation.latitude},${deviceLocation.longitude} "
                )
                return "${deviceLocation.latitude},${deviceLocation.longitude}"
            } catch (e: LocationPermissionNotGrantedException) {
                Log.d(
                    TAG,
                    "getPreferredLocationString 3)new location is: $customLocationName"
                )
                return customLocationName
            }
        } else
            Log.d(TAG, "getPreferredLocationString 4)new location is: $customLocationName")
        return customLocationName
    }

    override fun isDeviceLocationSelected(): Boolean {
        return preferences.getBoolean(USE_DEVICE_LOCATION, true)
    }

    private suspend fun hasDeviceLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        if (!isUsingDeviceLocation())
            return false

        val deviceLocation = getLastPhysicalDeviceLocation()
            ?: return false

        val comparisonThreshold = 0.03
        return abs(deviceLocation.latitude - lastWeatherLocation.lat) > comparisonThreshold &&
                abs(deviceLocation.longitude - lastWeatherLocation.lon) > comparisonThreshold
    }

    private fun hasCustomLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        return if (!isUsingDeviceLocation()) {
            val customLocationName = getCustomLocationName()
            customLocationName != lastWeatherLocation.name
        }else
            false
    }

    private fun isUsingDeviceLocation(): Boolean {
        return preferences.getBoolean(USE_DEVICE_LOCATION, true)
    }

    private fun getCustomLocationName(): String? {
        return preferences.getString(
            CUSTOM_LOCATION,
            appContext.getString(R.string.default_location)
        )
    }

    @SuppressLint("MissingPermission")
    override suspend fun getLastPhysicalDeviceLocation(): Location? {
        return (if (hasLocationPermission())
            fusedLocationProviderClient.lastLocation.asDeferred()
        else
            throw LocationPermissionNotGrantedException()).await()
    }

    override fun isLocationEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // This is new method provided in API 28
            val lm =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            lm.isLocationEnabled
        } else {
            // This is Deprecated in API 28
            val mode: Int = Settings.Secure.getInt(
                context.contentResolver, Settings.Secure.LOCATION_MODE,
                Settings.Secure.LOCATION_MODE_OFF
            )
            mode != Settings.Secure.LOCATION_MODE_OFF
        }
    }


    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}