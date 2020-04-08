package com.bzahov.weatherapp.data.db.entity

import com.google.gson.annotations.SerializedName

class Condition (
    @SerializedName("weather_code")
    val weatherCode: Int

//    ,@SerializedName("weather_descriptions")
//    val weatherDescriptions: List<String>,
//    @SerializedName("weather_icons")
//    val weatherIcons: List<String>
)