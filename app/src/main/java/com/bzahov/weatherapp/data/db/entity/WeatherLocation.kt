package com.bzahov.weatherapp.data.db.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

const val WEATHER_LOCATION_ID = 0

@Entity(tableName = "weather_location")
data class WeatherLocation(
    val name: String,
    val country: String,
    val region: String,
    val lat: String,
    val lon: String,
    val localtime: String,
    @SerializedName("localtime_epoch")
    val localtimeEpoch: Long,
    @SerializedName("timezone_id")
    val timezoneId: String,
    @SerializedName("utc_offset")
    val utcOffset: String
){
    @PrimaryKey(autoGenerate = false)
    var id: Int = WEATHER_LOCATION_ID

    val zonedDateTime: ZonedDateTime
    get(){
        val instant = Instant.ofEpochSecond(localtimeEpoch)
        val zoneId = ZoneId.of(timezoneId)
        return ZonedDateTime.ofInstant(instant, zoneId)
    }
}