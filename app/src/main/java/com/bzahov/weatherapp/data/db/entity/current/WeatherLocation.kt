package com.bzahov.weatherapp.data.db.entity.current


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bzahov.weatherapp.data.db.entity.WeatherEntity
import com.google.gson.annotations.SerializedName
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

const val WEATHER_LOCATION_ID = 0

@Entity(tableName = "weather_location")
data class WeatherLocation(
    // REWORK Return City object temporary FIX for: current location isn't changed and stay with name current_weather_fragment all city attributes are null
    val lat: Double,
    val lon: Double,
    val name: String,
    val country: String,
    val region: String,

    val localtime: String,
    @SerializedName("localtime_epoch")
    val localtimeEpoch: Long,

    @SerializedName("timezone_id")
    val timezoneId: String,
    @SerializedName("utc_offset")
    val utcOffset: String
): WeatherEntity {
    constructor() : this(0.0,0.0,"TEST","TEEST","TEST","TEST",21312321,"TEST","TESR")
    //constructor(name : String,country: String,lat: Double,lon: Double) : this()
    @PrimaryKey(autoGenerate = false)
    var id: Int =
        WEATHER_LOCATION_ID

    val zonedDateTime: ZonedDateTime
    get(){
        val instant = Instant.ofEpochSecond(localtimeEpoch)
        val zoneId = ZoneId.of(timezoneId)
        return ZonedDateTime.ofInstant(instant, zoneId)
    }
    fun getLocalTimeClock(): String = localtime.substringAfter(" ")

    fun extractLocationString() =
        "${this.name}, ${this.country} | ${this.getLocalTimeClock()}"

}