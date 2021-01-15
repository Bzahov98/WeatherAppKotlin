package com.bzahov.weatherapp.data.db.entity.forecast.entities


import android.util.Log
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bzahov.weatherapp.data.db.DateConverters
import com.bzahov.weatherapp.data.db.entity.WeatherEntity
import com.bzahov.weatherapp.data.db.entity.forecast.model.*
import com.google.gson.annotations.SerializedName

@Entity(
	tableName = "forecast_day",
//	foreignKeys =
//	[ForeignKey(
//
//		entity = WeatherDetails::class,
//		parentColumns = ["futureDetailsID"],
//		childColumns = ["weatherID"],
//		onDelete = ForeignKey.CASCADE
//	)],
//	indices = [Index(value = ["dtTxt"], unique = true), Index(
//		value = ["weatherID"],
//		unique = false
//	)]
)
@TypeConverters(DateConverters::class)
data class FutureDayData(
	@PrimaryKey(autoGenerate = true)
	var futureID: Int? = null, // ownerId

	var weatherID: Int? = null, // ownerId

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
	val wind: Wind,

	@Embedded(prefix = "rain_")
	val rain: Rain?,

	@Embedded(prefix = "snow_")
	val snow: Snow?,
	var isEmptyData: Boolean = false
) : WeatherEntity {
	override fun toString(): String {
		return "FutureDayData(futureID=$futureID, weatherID=$weatherID, clouds=$clouds, dt=$dt, dtTxt='$dtTxt', weatherDetails=$weatherDetails, main=$main, sys=$sys, wind=$wind, rain=$rain, snow=$snow)"
	}

	constructor(isEmptyData : Boolean) : this(
		ERROR_ID_VALUE, ERROR_ID_VALUE, Clouds(), -1, "dtTxt",
		listOf(), Main(), Sys(), Wind(), Rain(), Snow()
	){
		this.isEmptyData = isEmptyData
		if(isEmptyData){
			Log.d("FutureDayData","Empty data of FutureDayData")
		}
	}

	companion object{
		const val ERROR_ID_VALUE = Int.MAX_VALUE
	}
}
//@Entity(foreignKeys = [ForeignKey(entity = WeatherDetails::class,childColumns = ["futureDetailsID"],parentColumns = ["futureID"],onDelete = ForeignKey.CASCADE)])