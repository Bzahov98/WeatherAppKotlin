package com.bzahov.weatherapp.data.db.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bzahov.weatherapp.data.db.DateConverters
import com.google.gson.annotations.SerializedName

const val CURRENT_WEATHER_ID = 0

@Entity(tableName = "current_weather")
data class CurrentWeatherEntry(

    val temperature: Double,
    val feelslike: Double,
    val precipation: Double,
    val pressure: Double,
    val cloudcover: Int,
    val humidity: Int,
    val visibility: Double,

    @SerializedName("is_day")
    val isDay: String,
    @SerializedName("observation_time")
    val observationTime: String,
    @SerializedName("uv_index")
    val uvIndex: Int,
    @SerializedName("weather_code")
    val weatherCode: Int,
    // FIX: find way to serialize list of strings
    @TypeConverters(DateConverters::class)
    @SerializedName("weather_descriptions")
    val weatherDescriptions: List<String>,
    @SerializedName("weather_icons")
    val weatherIcons: List<String>,
    @SerializedName("wind_degree")
    val degree: Int,
    @SerializedName("wind_dir")
    val dir: String,

    @SerializedName("wind_speed")
    val speed: Double
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = CURRENT_WEATHER_ID // we have only 1 instance in db
}