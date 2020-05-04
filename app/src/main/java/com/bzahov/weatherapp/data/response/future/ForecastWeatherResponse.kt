package com.bzahov.weatherapp.data.response.future

import com.bzahov.weatherapp.data.db.entity.forecast.entities.FutureDayData
import com.bzahov.weatherapp.data.db.entity.forecast.model.City


data class ForecastWeatherResponse(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<FutureDayData>,
    val message: Double
)