package com.bzahov.weatherapp.data.provider.interfaces

import com.bzahov.weatherapp.data.db.entity.WeatherLocation

interface LocationProvider {
    //fun getLocation(): String
    suspend fun hasLocationChanged(lastWeatherLocation: WeatherLocation): Boolean
    suspend fun getPreferredLocationString(): String
}
