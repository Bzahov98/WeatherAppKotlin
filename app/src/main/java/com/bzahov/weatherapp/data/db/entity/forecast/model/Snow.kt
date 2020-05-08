package com.bzahov.weatherapp.data.db.entity.forecast.model

import com.google.gson.annotations.SerializedName

data class Snow(
    @SerializedName("3h")
    val precipitationsForLast3hours : Double = 0.0
)