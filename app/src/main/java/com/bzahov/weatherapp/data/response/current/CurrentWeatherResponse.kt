package com.bzahov.weatherapp.data.response.current

import com.bzahov.weatherapp.data.db.entity.current.CurrentWeatherEntry
import com.bzahov.weatherapp.data.db.entity.current.CurrentWeatherRequest
import com.bzahov.weatherapp.data.db.entity.current.WeatherLocation
import com.google.gson.annotations.SerializedName


data class CurrentWeatherResponse(
    @SerializedName("current")
    val currentWeatherEntry: CurrentWeatherEntry,
    val location: WeatherLocation,
    val request: CurrentWeatherRequest
)