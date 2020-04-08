package com.bzahov.weatherapp.data.db.entity

import com.google.gson.annotations.SerializedName

class WindCondition (
    @SerializedName("wind_degree")
    val degree: Int,
    @SerializedName("wind_dir")
    val dir: String,
    @SerializedName("wind_speed")
    val speed: Double
)
