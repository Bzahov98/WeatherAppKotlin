package com.bzahov.weatherapp.data.db.entity.current


data class CurrentWeatherRequest(
    val language: String,
    val query: String,
    val type: String,
    val units: String
)