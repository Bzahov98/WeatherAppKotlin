package com.bzahov.weatherapp.data.provider

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.entity.current.WeatherLocation
import com.bzahov.weatherapp.data.provider.interfaces.LocationProvider
import com.bzahov.weatherapp.internal.asDeferred
import com.bzahov.weatherapp.internal.exceptions.LocationPermissionNotGrantedException
import com.google.android.gms.location.FusedLocationProviderClient


const val CUSTOM_LOCATION = "CUSTOM_LOCATION"
const val USE_DEVICE_LOCATION = "USE_DEVICE_LOCATION"

class LocationProviderImpl(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    context: Context
) : PreferenceProvider(context), LocationProvider {

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
                // return getLocationStringBasedOnProvider(deviceLocation)
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

    private fun getLocationStringBasedOnProvider(deviceLocation: Location): String {
        return if (isUsingDeviceLocation()) "${deviceLocation.latitude},${deviceLocation.longitude}" else "lat=${deviceLocation.latitude}&lon=${deviceLocation.longitude}"
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
        val result =
            Math.abs(deviceLocation.latitude - lastWeatherLocation.lat) > comparisonThreshold &&
                    Math.abs(deviceLocation.longitude - lastWeatherLocation.lon) > comparisonThreshold
        return result
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

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}