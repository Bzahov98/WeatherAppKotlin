package com.bzahov.weatherapp.data.db.entity


import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bzahov.weatherapp.data.db.entity.Condition
import com.bzahov.weatherapp.data.db.entity.WindCondition
import com.google.gson.annotations.SerializedName

const val CURRENT_WEATHER_ID = 0
@Entity(tableName= "current_weather")
    data class CurrentWeatherEntry(
    val cloudcover: Int,
    val feelslike: Double,
    val humidity: Int,
    @SerializedName("is_day")
    val isDay: String,
    @SerializedName("observation_time")
    val observationTime: String,
    val precip: Double,
    val pressure: Double,
    val temperature: Int,
    @SerializedName("uv_index")
    val uvIndex: Int,
    val visibility: Double,
    @Embedded(prefix = "condition_")
    val condition: Condition,
    @Embedded(prefix = "wind_")
    val wind : WindCondition
){
    @PrimaryKey(autoGenerate = false)
    var id : Int = CURRENT_WEATHER_ID // we have only 1 instance in db
}