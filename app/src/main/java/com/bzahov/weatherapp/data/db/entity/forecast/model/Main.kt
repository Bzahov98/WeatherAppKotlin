package com.bzahov.weatherapp.data.db.entity.forecast.model


import com.google.gson.annotations.SerializedName

data class Main(
    @SerializedName("grnd_level")
    val grndLevel: Double,
    val humidity: Int,
    @SerializedName("feels_like")
    val feelsLike : Double,
    val pressure: Double,
    @SerializedName("sea_level")
    val seaLevel: Double,
    val temp: Double,
    @SerializedName("temp_max")
    val tempMax: Double,
    @SerializedName("temp_min")
    val tempMin: Double
)