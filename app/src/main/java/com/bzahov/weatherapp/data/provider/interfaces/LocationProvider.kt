package com.bzahov.weatherapp.data.provider.interfaces

import android.location.Location
import com.bzahov.weatherapp.data.db.entity.current.WeatherLocation

interface LocationProvider {
    var offsetDateTime : Int

    fun getLocationString(): String
    fun isDeviceLocationSelected(): Boolean
    fun resetCustomLocationToDefault()
    fun isLocationEnabled(): Boolean

    suspend fun hasLocationChanged(lastWeatherLocation: WeatherLocation): Boolean
    suspend fun getPreferredLocationString(): String
    suspend fun getLastPhysicalDeviceLocation(): Location?
}
