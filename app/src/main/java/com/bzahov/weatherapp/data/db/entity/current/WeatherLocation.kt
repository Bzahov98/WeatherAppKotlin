package com.bzahov.weatherapp.data.db.entity.current


import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bzahov.weatherapp.data.db.entity.model.City
import com.google.gson.annotations.SerializedName
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

const val WEATHER_LOCATION_ID = 0

@Entity(tableName = "weather_location")
data class WeatherLocation (
    @Embedded
    val city: City,
    val region: String,

    val localtime: String,
    @SerializedName("localtime_epoch")
    val localtimeEpoch: Long,

    @SerializedName("timezone_id")
    val timezoneId: String,
    @SerializedName("utc_offset")
    val utcOffset: String
) {
    constructor(city : City) : this(city,"","",0,"","")
    @PrimaryKey(autoGenerate = false)
    var id: Int =
        WEATHER_LOCATION_ID

    val zonedDateTime: ZonedDateTime
    get(){
        val instant = Instant.ofEpochSecond(localtimeEpoch)
        val zoneId = ZoneId.of(timezoneId)
        return ZonedDateTime.ofInstant(instant, zoneId)
    }
}