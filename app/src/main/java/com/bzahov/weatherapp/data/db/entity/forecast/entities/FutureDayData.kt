package com.bzahov.weatherapp.data.db.entity.forecast.entities


import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bzahov.weatherapp.data.db.entity.forecast.model.Clouds
import com.bzahov.weatherapp.data.db.entity.forecast.model.Main
import com.bzahov.weatherapp.data.db.entity.forecast.model.Sys
import com.bzahov.weatherapp.data.db.entity.forecast.model.Wind
import com.google.gson.annotations.SerializedName
@Entity(tableName = "forecast_day")
data class FutureDayData(
    @PrimaryKey(autoGenerate = true)
    var futureID :Int? = null, // ownerId
    @Embedded(prefix = "clouds_")
    val clouds: Clouds,
    val dt: Int,
    @SerializedName("dt_txt")
    val dtTxt: String,

    @Embedded(prefix = "main_")//weatherDataAtThisHour
    val main: Main,

    @Embedded(prefix = "sys_")
    val sys: Sys,

    @Embedded(prefix = "wind_")
    val wind: Wind
)