package com.bzahov.weatherapp.data.provider.interfaces

import android.location.Location
import com.bzahov.weatherapp.data.db.entity.current.WeatherLocation

interface LocationProvider {
    fun getLocationString(): String
    //suspend fun getLastDeviceLocation(): Location?
    suspend fun hasLocationChanged(lastWeatherLocation: WeatherLocation): Boolean
    // default false for WeatherStack and true for OpenWeather api
    suspend fun getPreferredLocationString(): String
    fun isDeviceLocationSelected(): Boolean
    suspend fun getLastPhysicalDeviceLocation(): Location?
}
