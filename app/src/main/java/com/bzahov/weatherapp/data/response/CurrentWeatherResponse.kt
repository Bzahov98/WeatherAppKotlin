package com.bzahov.weatherapp.data.response

import com.bzahov.weatherapp.data.db.entity.CurrentWeatherEntry
import com.bzahov.weatherapp.data.db.entity.Location
import com.bzahov.weatherapp.data.db.entity.Request
import com.google.gson.annotations.SerializedName


data class CurrentWeatherResponse(
    @SerializedName("current")
    val currentWeatherEntry: CurrentWeatherEntry,
    val location: Location,
    val request: Request
)