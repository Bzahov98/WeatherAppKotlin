package com.bzahov.weatherapp.data.provider.interfaces

import com.bzahov.weatherapp.data.db.entity.WeatherLocation

interface LocationProvider {
    fun getLocation(): String
    fun hasLocationChanged(lastWeatherLocation: WeatherLocation): Boolean
}
