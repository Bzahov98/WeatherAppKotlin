package com.bzahov.weatherapp.data.db.entity.forecast.entities


import androidx.room.*
import com.bzahov.weatherapp.data.db.DateConverters
import com.bzahov.weatherapp.data.db.entity.forecast.model.Clouds
import com.bzahov.weatherapp.data.db.entity.forecast.model.Main
import com.bzahov.weatherapp.data.db.entity.forecast.model.Sys
import com.bzahov.weatherapp.data.db.entity.forecast.model.Wind
import com.google.gson.annotations.SerializedName
@Entity(tableName = "forecast_day",foreignKeys =
[ForeignKey(

    entity = WeatherDetails::class,
    parentColumns = ["futureDetailsID"],
    childColumns = ["weatherID"],
    onDelete = ForeignKey.CASCADE
)]
 , indices = [Index(value= ["dtTxt"],unique = true),Index(value = ["weatherID"], unique = false)])
@TypeConverters(DateConverters::class)
data class FutureDayData(
    @PrimaryKey(autoGenerate = true)
    var futureID :Int? = null, // ownerId

    var weatherID :Int? = null, // ownerId

    @Embedded(prefix = "clouds_")
    val clouds: Clouds,
    val dt: Long,
    @SerializedName("dt_txt")
    val dtTxt: String,

    @SerializedName("weather")
    val weatherDetails: List<WeatherDetails>,
    @Embedded(prefix = "main_")//weatherDataAtThisHour
    val main: Main,

    @Embedded(prefix = "sys_")
    val sys: Sys,

    @Embedded(prefix = "wind_")
    val wind: Wind
)

//@Entity(foreignKeys = [ForeignKey(entity = WeatherDetails::class,childColumns = ["futureDetailsID"],parentColumns = ["futureID"],onDelete = ForeignKey.CASCADE)])