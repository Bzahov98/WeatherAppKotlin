package com.bzahov.weatherapp.data.provider

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.bzahov.weatherapp.data.db.entity.WeatherLocation
import com.bzahov.weatherapp.data.provider.interfaces.LocationProvider
import com.bzahov.weatherapp.internal.asDeferred
import com.bzahov.weatherapp.internal.exceptions.LocationPermissionNotGrantedException
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.Deferred


const val CUSTOM_LOCATION = "CUSTOM_LOCATION"
const val USE_DEVICE_LOCATION = "USE_DEVICE_LOCATION"

class LocationProviderImpl(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    context: Context
) : PreferenceProvider(context), LocationProvider {

//    override fun getLocation(): String {
//        val selectedName =
//            preferences.getString(CUSTOM_LOCATION, appContext.getString(R.string.default_location))
//        return selectedName!!
//    }

    override suspend fun hasLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        val deviceLocationChanged = try {
            hasDeviceLocationChanged(lastWeatherLocation)
        } catch (e: LocationPermissionNotGrantedException) {
            false
        }
        return deviceLocationChanged || hasCustomLocationChanged(lastWeatherLocation)
    }

    override suspend fun getPreferredLocationString(): String {
        var result: String = "${getCustomLocationName()}"
        if (isUsingDeviceLocation()) {
            try {
                val deviceLocation = getLastDeviceLocation().await()
                if (deviceLocation == null) {
                    Log.d(TAG, "getPreferredLocationString 1)new location is: $result")
                    return "${getCustomLocationName()}"
                }
                Log.d(TAG,">>>getPreferredLocationString 2)new location is: ${deviceLocation.latitude},${deviceLocation.longitude} " )
                return "${deviceLocation.latitude},${deviceLocation.longitude}"
            } catch (e: LocationPermissionNotGrantedException) {
                Log.d(TAG, "getPreferredLocationString 3)new location is: ${getCustomLocationName()}")
                return "${getCustomLocationName()}"
            }
        } else
            Log.d(TAG, "getPreferredLocationString 4)new location is: ${getCustomLocationName()}")
        return "${getCustomLocationName()}"
    }

    private suspend fun hasDeviceLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        if (!isUsingDeviceLocation())
            return false

        val deviceLocation = getLastDeviceLocation().await()
            ?: return false

        // Comparing doubles cannot be done with "=="
        val comparisonThreshold = 0.03
        val result =
            Math.abs(deviceLocation.latitude - lastWeatherLocation.lat) > comparisonThreshold &&
                    Math.abs(deviceLocation.longitude - lastWeatherLocation.lon) > comparisonThreshold
        return true
    }

    private fun hasCustomLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        val customLocationName = getCustomLocationName()
        return customLocationName != lastWeatherLocation.name
    }

    private fun isUsingDeviceLocation(): Boolean {
        return true
        //return preferences.getBoolean(USE_DEVICE_LOCATION, true)
    }

    private fun getCustomLocationName(): String? {
        return preferences.getString(CUSTOM_LOCATION, null)
    }

    @SuppressLint("MissingPermission")
    private fun getLastDeviceLocation(): Deferred<Location?> {
        return if (hasLocationPermission())
            fusedLocationProviderClient.lastLocation.asDeferred()
        else
            throw LocationPermissionNotGrantedException()
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}