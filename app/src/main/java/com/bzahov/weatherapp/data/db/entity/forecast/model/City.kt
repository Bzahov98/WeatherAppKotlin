package com.bzahov.weatherapp.data.db.entity.forecast.model

import com.google.gson.annotations.SerializedName


data class City(
    val lat: Double,
    val lon: Double,
    val name: String,
    val country: String,
    val timezone: Int,
    val population : Long?,
    val sunrise : Long?,
    val sunset : Long?,
    @SerializedName("city.id")
    val openWeatherCityID: Int? = 0 // openweatherCityID
)